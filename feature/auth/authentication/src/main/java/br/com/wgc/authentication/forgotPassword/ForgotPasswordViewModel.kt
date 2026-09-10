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
 * ViewModel responsÃ¡vel pelas regras de negÃ³cio e fluxo de recuperaÃ§Ã£o de senha.
 *
 * Valida o formato do e-mail do usuÃ¡rio e executa o envio de instruÃ§Ãµes via [AuthRepository],
 * emitindo eventos de navegaÃ§Ã£o para retorno Ã  tela de login apÃ³s o sucesso.
 *
 * @param authRepository RepositÃ³rio de autenticaÃ§Ã£o provido via Hilt para comunicaÃ§Ã£o com o backend.
 */
@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseResetPasswordScreenTemplateViewModel() {

    private val _navigationEvent = Channel<AuthNavDestinations.ForgotPasswordScreen>(Channel.BUFFERED)

    /**
     * Fluxo de eventos de navegaÃ§Ã£o observados pela camada de apresentaÃ§Ã£o.
     */
    val navigationEvent = _navigationEvent.receiveAsFlow()

    /**
     * Processa a solicitaÃ§Ã£o de redefiniÃ§Ã£o de senha com validaÃ§Ã£o de formato e chamada de repositÃ³rio.
     */
    override fun onResetPasswordClick() {
        val email = uiState.value.email.trim()
        if (!email.isValidEmail()) {
            updateState { it.copy(emailError = "Informe um e-mail vÃ¡lido") }
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
     * Redireciona o usuÃ¡rio de volta Ã  tela de login.
     */
    override fun onBackToLoginClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.ForgotPasswordScreen.Login)
        }
    }
}