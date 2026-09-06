package br.com.gds.authentication.forgotPassword

import androidx.lifecycle.viewModelScope
import br.com.gds.authentication.navigation.AuthNavDestinations
import br.com.wgc.ds_templates.screens.resetpassword.viewmodel.BaseResetPasswordScreenTemplateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor() : BaseResetPasswordScreenTemplateViewModel() {
    private val _navigationEvent = Channel<AuthNavDestinations.ForgotPasswordScreen>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    override fun onResetPasswordClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.ForgotPasswordScreen.Login)
        }
    }

    override fun onBackToLoginClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.ForgotPasswordScreen.Login)
        }
    }
}