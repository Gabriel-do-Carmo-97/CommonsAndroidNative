package br.com.wgc.authentication.registerUser

import androidx.lifecycle.viewModelScope
import br.com.wgc.authentication.navigation.AuthNavDestinations
import br.com.wgc.authentication.utils.toUserFriendlyMessage
import br.com.wgc.core.validators.isValidEmail
import br.com.wgc.design_system.templates.screens.register.user.viewmodel.BaseRegisterUserTemplateViewModel
import br.wgc.omnibackend.core.repository.AuthRepository
import br.wgc.omnibackend.core.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel corporativo para controle de cadastro de novos usuários.
 *
 * Conecta-se diretamente ao [AuthRepository] do OmniBackend 1.2.0, realizando a criação
 * da conta em nuvem, validação de termos LGPD e navegação encadeada para cadastro de endereço.
 *
 * ### 📌 Quando Usar:
 * Utilize este ViewModel ao montar ou exibir o formulário inicial de registro (`RegisterUserScreen`),
 * coletando nome, e-mail, senha e confirmação de termos do serviço.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun MinhaTelaDeCadastro(
 *     viewModel: RegisterUserViewModel = hiltViewModel()
 * ) {
 *     // Observando eventos de navegação
 *     LaunchedEffect(viewModel.navigationEvent) {
 *         viewModel.navigationEvent.collect { event ->
 *             when (event) {
 *                 is AuthNavDestinations.RegisterUserScreen.RegisterAddress -> navegarParaEndereco()
 *                 is AuthNavDestinations.RegisterUserScreen.Login -> navegarParaLogin()
 *             }
 *         }
 *     }
 *
 *     RegisterUserScreenTemplate(viewModel = viewModel)
 * }
 * ```
 *
 * @param authRepository Repositório unificado de autenticação provido pelo OmniBackend Core.
 */
@HiltViewModel
class RegisterUserViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseRegisterUserTemplateViewModel() {

    private val _navigationEvent = Channel<AuthNavDestinations.RegisterUserScreen>(Channel.BUFFERED)

    /**
     * Fluxo de eventos de navegação para etapas subsequentes do fluxo de onboarding/registro.
     */
    val navigationEvent = _navigationEvent.receiveAsFlow()

    /**
     * Valida os termos de serviço, formato de e-mail e dispara a criação de conta no backend.
     *
     * ### 📌 Quando Usar:
     * Acionado pelo usuário ao clicar no botão de submissão do formulário de cadastro.
     *
     * ### 🛠️ Modo de Usar:
     * Chamado automaticamente pelo template `RegisterUserScreenTemplate`, ou manualmente:
     * ```kotlin
     * viewModel.onRegisterClick()
     * ```
     */
    override fun onRegisterClick() {
        val state = uiState.value

        if (!state.acceptedTerms) {
            updateState { it.copy(generalError = "Você deve aceitar os termos para continuar") }
            return
        }

        if (!state.email.isValidEmail()) {
            updateState { it.copy(emailError = "Informe um e-mail válido") }
            return
        }

        val email = state.email.trim()
        val password = state.password

        viewModelScope.launch {
            updateState { it.copy(isLoading = true, generalError = null) }
            when (val result = authRepository.createUser(email, password)) {
                is DataResult.Success -> {
                    updateState { it.copy(isLoading = false, generalError = null) }
                    _navigationEvent.send(AuthNavDestinations.RegisterUserScreen.RegisterAddress)
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
     * Redireciona o usuário para a tela de login existente.
     *
     * ### 📌 Quando Usar:
     * Acionado quando o usuário já possui cadastro e clica em "Já tem uma conta? Entrar".
     */
    override fun onLoginClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.RegisterUserScreen.Login)
        }
    }

    /**
     * Trata o clique para visualização dos Termos de Uso e Política de Privacidade.
     *
     * ### 📌 Quando Usar:
     * Acionado quando o usuário clica sobre o link dos termos para conformidade LGPD.
     */
    override fun onTermsClick() {
        // Callback para exibição de termos LGPD
    }

    /**
     * Retorna à tela anterior de login.
     *
     * ### 📌 Quando Usar:
     * Acionado ao clicar no ícone de voltar da TopBar.
     */
    override fun onBackClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.RegisterUserScreen.Login)
        }
    }
}