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
import br.com.wgc.ds_templates.screens.login.viewmodel.BaseLoginScreenTemplateViewModel
import br.wgc.omnibackend.firebase.domain.usecase.LoginUseCase
import br.wgc.omnibackend.firebase.utils.UseCaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel corporativo para controle de autenticação de usuários, sessão persistente e biometria.
 *
 * @param useCase Caso de uso de login do OmniBackend.
 * @param dataStore Utilitário de persistência de preferências do Core.
 * @param biometricAuthHelper Helper de autenticação biométrica do Core.
 */
@HiltViewModel
class LoginWGCViewModel @Inject constructor(
    private val useCase: LoginUseCase,
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
     */
    override fun onLoginClick() {
        val currentEmail = uiState.value.email.trim()
        val currentPassword = uiState.value.password

        viewModelScope.launch {
            useCase(
                email = currentEmail,
                password = currentPassword
            ).collect { result ->
                when (result) {
                    is UseCaseResult.Loading -> {
                        updateState { it.copy(isLoading = true, generalError = null) }
                    }
                    is UseCaseResult.Success<*> -> {
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
                    is UseCaseResult.Failure -> {
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
    }

    /**
     * Inicia autenticação biométrica utilizando o helper do Core.
     *
     * @param activity FragmentActivity para exibição do prompt nativo.
     * @param onSuccess Callback invocado após sucesso na validação biométrica.
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
     */
    override fun onRegisterClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.LoginScreen.RegisterUser)
        }
    }

    /**
     * Redireciona o usuário para o fluxo de recuperação de senha.
     */
    override fun onForgotPasswordClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.LoginScreen.ForgotPassword)
        }
    }

    /**
     * Trata alteração da opção de lembrar credenciais.
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