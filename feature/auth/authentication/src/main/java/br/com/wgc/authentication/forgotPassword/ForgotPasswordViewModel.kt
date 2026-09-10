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

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseResetPasswordScreenTemplateViewModel() {

    private val _navigationEvent = Channel<AuthNavDestinations.ForgotPasswordScreen>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

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

    override fun onBackToLoginClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.ForgotPasswordScreen.Login)
        }
    }
}