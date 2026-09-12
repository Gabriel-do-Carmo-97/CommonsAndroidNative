package br.com.wgc.authentication.forgotPassword

import androidx.lifecycle.viewModelScope
import br.com.wgc.authentication.navigation.AuthNavDestinations
import br.com.wgc.authentication.utils.toUserFriendlyMessage
import br.com.wgc.core.validators.isValidEmail
import br.com.wgc.ds_templates.screens.resetpassword.viewmodel.BaseResetPasswordScreenTemplateViewModel
import br.wgc.omnibackend.core.repository.AuthRepository
import br.wgc.omnibackend.core.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsável pelas regras de negócio e fluxo de recuperação de senha.
 *
 * Valida o formato do e-mail do usuário e executa o envio de instruções via [AuthRepository],
 * emitindo eventos de navegação para retorno à tela de login após o sucesso.
 *
 * @param authRepository Repositório de autenticação provido via Hilt para comunicação com o backend.
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
     * Processa a solicitação de redefinição de senha com validação de formato e chamada de repositório.
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
     */
    override fun onBackToLoginClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.ForgotPasswordScreen.Login)
        }
    }
}