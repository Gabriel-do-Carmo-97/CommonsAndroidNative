package br.com.gds.authentication.registerUser

import br.com.gds.authentication.navigation.AuthNavDestinations
import br.com.wgc.ds_templates.screens.register.user.viewmodel.BaseRegisterUserTemplateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class RegisterUserViewModel @Inject constructor(): BaseRegisterUserTemplateViewModel() {
    private val _navigationEvent = Channel<AuthNavDestinations.RegisterUserScreen>()
    val navigationEvent = _navigationEvent.receiveAsFlow()
    override fun onRegisterClick() {
        TODO("Not yet implemented")
    }

    override fun onLoginClick() {
        TODO("Not yet implemented")
    }

    override fun onTermsClick() {
        TODO("Not yet implemented")
    }

    override fun onBackClick() {
        TODO("Not yet implemented")
    }
}