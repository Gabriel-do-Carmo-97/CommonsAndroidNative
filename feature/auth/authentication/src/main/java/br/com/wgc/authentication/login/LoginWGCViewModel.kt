package br.com.wgc.authentication.login

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import br.com.wgc.authentication.navigation.AuthNavDestinations
import br.com.wgc.authentication.utils.toUserFriendlyMessage
import br.com.wgc.core.dataStorePreferences.DataStorePreferencesCore
import br.com.wgc.core.security.biometric.BiometricAuthHelper
import br.com.wgc.core.security.biometric.BiometricAuthResult
import br.com.wgc.core.security.biometric.BiometricAuthStatus
import br.com.wgc.core.security.biometric.BiometricPromptConfig
import br.com.wgc.design_system.factories.auth.templates.viewmodel.BaseLoginScreenTemplateViewModel
import br.wgc.omnibackend.core.repository.AuthRepository
import br.wgc.omnibackend.core.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel corporativo para controle de autenticação de usuários, sessão persistente e biometria.
 *
 * Desacoplado de implementações de nuvem específicas via [AuthRepository] do OmniBackend 1.2.0,
 * suportando Firebase, Supabase, Appwrite e REST com a mesma interface unificada.
 *
 * ### 📌 Quando Usar:
 * Utilize este ViewModel ao instanciar ou personalizar a tela principal de autenticação (`LoginWGCScreen`),
 * permitindo sign-in tradicional (e-mail e senha), biometria nativa pelo hardware do aparelho
 * e persistência de credenciais ("Lembrar de mim").
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun MinhaTelaDeLogin(
 *     viewModel: LoginWGCViewModel = hiltViewModel()
 * ) {
 *     val state by viewModel.uiState.collectAsState()
 *
 *     // Observando eventos de navegação
 *     LaunchedEffect(viewModel.navigationEvent) {
 *         viewModel.navigationEvent.collect { event ->
 *             when (event) {
 *                 is AuthNavDestinations.LoginScreen.LoginSuccess -> navegarParaHome()
 *                 is AuthNavDestinations.LoginScreen.RegisterUser -> navegarParaCadastro()
 *                 is AuthNavDestinations.LoginScreen.ForgotPassword -> navegarParaRecuperarSenha()
 *             }
 *         }
 *     }
 *
 *     LoginScreenTemplate(viewModel = viewModel)
 * }
 * ```
 *
 * @param authRepository Repositório de autenticação provido pelo OmniBackend Core.
 * @param dataStore Utilitário de persistência segura de preferências do Core.
 * @param biometricAuthHelper Helper de autenticação biométrica do Core.
 */
@HiltViewModel
class LoginWGCViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStore: DataStorePreferencesCore,
    private val biometricAuthHelper: BiometricAuthHelper
) : BaseLoginScreenTemplateViewModel() {

    private val _navigationEvent = Channel<AuthNavDestinations.LoginScreen>(Channel.BUFFERED)

    /**
     * Fluxo de navegação para redirecionamentos pós-autenticação, cadastro ou recuperação de senha.
     */
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            val rememberMe = dataStore.getBooleanFlow(KEY_REMEMBER_ME, false).firstOrNull() ?: false
            if (rememberMe) {
                val savedEmail = dataStore.getStringFlow(KEY_SAVED_EMAIL, null).firstOrNull()
                if (!savedEmail.isNullOrBlank()) {
                    updateState { currentState ->
                        currentState.copy(
                            email = savedEmail,
                            rememberMeChecked = true
                        )
                    }
                }
            }
        }
    }

    /**
     * Dispara autenticação via e-mail e senha junto ao OmniBackend.
     *
     * ### 📌 Quando Usar:
     * Acionado quando o usuário clica no botão principal de entrada da tela de login.
     *
     * ### 🛠️ Modo de Usar:
     * Invocado automaticamente pelo template `LoginScreenTemplate`, ou manualmente:
     * ```kotlin
     * viewModel.onLoginClick()
     * ```
     */
    override fun onLoginClick() {
        val currentEmail = uiState.value.email.trim()
        val currentPassword = uiState.value.password

        viewModelScope.launch {
            updateState { it.copy(isLoading = true, generalError = null) }
            when (val result = authRepository.login(currentEmail, currentPassword)) {
                is DataResult.Success -> {
                    updateState { it.copy(isLoading = false, generalError = null) }
                    if (uiState.value.rememberMeChecked) {
                        dataStore.saveString(KEY_SAVED_EMAIL, currentEmail)
                        dataStore.saveBoolean(KEY_REMEMBER_ME, true)
                    } else {
                        dataStore.removeKey(KEY_SAVED_EMAIL)
                        dataStore.saveBoolean(KEY_REMEMBER_ME, false)
                    }
                    _navigationEvent.send(
                        AuthNavDestinations.LoginScreen.LoginSuccess(email = currentEmail)
                    )
                }
                is DataResult.Failure -> {
                    updateState {
                        it.copy(
                            isLoading = false,
                            generalError = result.error.toUserFriendlyMessage()
                        )
                    }
                }
            }
        }
    }

    /**
     * Define programaticamente as credenciais de e-mail e senha no estado do formulário de autenticação.
     *
     * Permite que fluxos de preenchimento automático, restauração de sessão ou rotinas de teste unitário
     * atualizem os campos de entrada sem acionar validações de framework dependentes de runtime Android.
     *
     * ### 📌 Quando Usar:
     * - Durante o preenchimento automático de credenciais salvas em keychain ou DataStore criptografado.
     * - Em cenários de teste unitário onde o estado precisa ser configurado deterministicamente.
     *
     * ### 🛠️ Modo de Usar:
     * ```kotlin
     * viewModel.setCredentials("usuario@empresa.com", "senhaSegura123")
     * viewModel.onLoginClick()
     * ```
     *
     * @param email Endereço de e-mail válido a ser inserido no campo de entrada.
     * @param password Senha associada à conta do usuário.
     */
    fun setCredentials(email: String, password: String) {
        updateState { it.copy(email = email, password = password) }
    }

    /**
     * Inicia autenticação biométrica utilizando o helper do Core.
     *
     * ### 📌 Quando Usar:
     * Acionado quando o usuário toca no botão de biometria ou quando o app detecta biometria salva para login rápido.
     *
     * ### 🛠️ Modo de Usar:
     * ```kotlin
     * viewModel.authenticateWithBiometrics(activity) { email ->
     *     println("Usuário logado via biometria: $email")
     * }
     * ```
     *
     * @param activity FragmentActivity necessária para exibição do prompt do sistema Android.
     * @param onSuccess Callback opcional invocado após sucesso na validação biométrica.
     */
    fun authenticateWithBiometrics(
        activity: FragmentActivity,
        onSuccess: (email: String) -> Unit = {}
    ) {
        val status = biometricAuthHelper.canAuthenticate()
        if (status != BiometricAuthStatus.Ready) {
            updateState { it.copy(generalError = "Biometria não configurada ou indisponível neste aparelho.") }
            return
        }

        val savedEmail = uiState.value.email.ifBlank { "Usuário Autenticado" }
        biometricAuthHelper.authenticate(
            activity = activity,
            config = BiometricPromptConfig(
                title = "Autenticação Biométrica",
                subtitle = "Toque no sensor para entrar",
                negativeButtonText = "Cancelar"
            )
        ) { result ->
            when (result) {
                is BiometricAuthResult.Success -> {
                    viewModelScope.launch {
                        _navigationEvent.send(
                            AuthNavDestinations.LoginScreen.LoginSuccess(email = savedEmail)
                        )
                    }
                    onSuccess(savedEmail)
                }
                is BiometricAuthResult.Error -> {
                    updateState { it.copy(generalError = result.errorMessage.toString()) }
                }
                is BiometricAuthResult.Failed -> {
                    updateState { it.copy(generalError = "Biometria não reconhecida.") }
                }
                is BiometricAuthResult.Cancelled -> Unit
            }
        }
    }

    /**
     * Redireciona o usuário para o fluxo de cadastro.
     *
     * ### 📌 Quando Usar:
     * Acionado pelo clique em "Cadastre-se" / "Criar conta".
     */
    override fun onRegisterClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.LoginScreen.RegisterUser)
        }
    }

    /**
     * Redireciona o usuário para o fluxo de recuperação de senha.
     *
     * ### 📌 Quando Usar:
     * Acionado pelo clique em "Esqueceu sua senha?".
     */
    override fun onForgotPasswordClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.LoginScreen.ForgotPassword)
        }
    }

    /**
     * Trata alteração da opção de lembrar credenciais no DataStore seguro.
     *
     * ### 📌 Quando Usar:
     * Acionado quando o usuário marca ou desmarca a caixa "Lembrar de mim".
     *
     * @param isChecked Indica se a credencial deve ser persistida localmente.
     */
    override fun onRememberMeCheckedChange(isChecked: Boolean) {
        super.onRememberMeCheckedChange(isChecked)
        viewModelScope.launch {
            dataStore.saveBoolean(KEY_REMEMBER_ME, isChecked)
            if (!isChecked) {
                dataStore.removeKey(KEY_SAVED_EMAIL)
            }
        }
    }

    companion object {
        private const val KEY_REMEMBER_ME = "wgc_auth_remember_me"
        private const val KEY_SAVED_EMAIL = "wgc_auth_saved_email"
    }
}