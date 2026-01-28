package br.com.gds.authentication.forgotPassword

import br.com.gds.authentication.navigation.AuthNavDestinations
import br.com.wgc.ds_templates.screens.resetpassword.viewmodel.BaseResetPasswordScreenTemplateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(): BaseResetPasswordScreenTemplateViewModel() {
    private val _navigationEvent = Channel<AuthNavDestinations.ForgotPasswordScreen>()
    val navigationEvent = _navigationEvent.receiveAsFlow()
    override fun onResetPasswordClick() {
        TODO("Not yet implemented")
    }

    override fun onBackToLoginClick() {
        TODO("Not yet implemented")
    }
}