package br.com.wgc.authentication.forgotPassword

import androidx.lifecycle.viewModelScope
import br.com.wgc.authentication.navigation.AuthNavDestinations
import br.com.wgc.authentication.utils.toUserFriendlyMessage
import br.com.wgc.core.validators.isValidEmail
import br.com.wgc.design_system.templates.screens.resetpassword.viewmodel.BaseResetPasswordScreenTemplateViewModel
import br.wgc.omnibackend.core.repository.AuthRepository
import br.wgc.omnibackend.core.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel corporativo para controle do fluxo de recuperação e redefinição de senha.
 *
 * Valida o formato do e-mail informado e despacha a requisição de reset para o [AuthRepository]
 * do OmniBackend 1.2.0, emitindo eventos de navegação para a interface de usuário.
 *
 * ### 📌 Quando Usar:
 * Utilize este ViewModel ao exibir a tela de recuperação de senha (`ForgotPasswordScreen`),
 * tratando erros amigáveis de usuário inexistente ou e-mail malformado.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun TelaRecuperacaoSenha(
 *     viewModel: ForgotPasswordViewModel = hiltViewModel()
 * ) {
 *     LaunchedEffect(viewModel.navigationEvent) {
 *         viewModel.navigationEvent.collect { event ->
 *             when (event) {
 *                 is AuthNavDestinations.ForgotPasswordScreen.Login -> voltarParaLogin()
 *             }
 *         }
 *     }
 *
 *     ResetPasswordScreenTemplate(viewModel = viewModel)
 * }
 * ```
 *
 * @param authRepository Repositório unificado de autenticação provido pelo OmniBackend Core.
 */
@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseResetPasswordScreenTemplateViewModel() {

    private val _navigationEvent = Channel<AuthNavDestinations.ForgotPasswordScreen>(Channel.BUFFERED)

    /**
     * Fluxo de eventos de navegação observados pela camada de apresentação.
     */
    val navigationEvent = _navigationEvent.receiveAsFlow()

    /**
     * Valida o e-mail informado e dispara o envio de instruções de recuperação de senha.
     *
     * ### 📌 Quando Usar:
     * Acionado quando o usuário confirma a solicitação de redefinição de senha.
     */
    override fun onResetPasswordClick() {
        val email = uiState.value.email.trim()
        if (!email.isValidEmail()) {
            updateState { it.copy(emailError = "Informe um e-mail válido") }
            return
        }

        viewModelScope.launch {
            updateState { it.copy(isLoading = true, emailError = null) }
            when (val result = authRepository.resetPassword(email)) {
                is DataResult.Success -> {
                    updateState { it.copy(isLoading = false, emailError = null) }
                    _navigationEvent.send(AuthNavDestinations.ForgotPasswordScreen.Login)
                }
                is DataResult.Failure -> {
                    updateState {
                        it.copy(
                            isLoading = false,
                            emailError = result.error.toUserFriendlyMessage()
                        )
                    }
                }
            }
        }
    }

    /**
     * Redireciona o usuário de volta à tela de login.
     *
     * ### 📌 Quando Usar:
     * Acionado pelo usuário ao clicar no botão de cancelamento ou link de retorno ao login.
     */
    override fun onBackToLoginClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.ForgotPasswordScreen.Login)
        }
    }
}