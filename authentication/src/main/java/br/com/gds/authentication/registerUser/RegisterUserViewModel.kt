package br.com.gds.authentication.registerUser

import androidx.lifecycle.viewModelScope
import br.com.gds.authentication.navigation.AuthNavDestinations
import br.com.wgc.ds_templates.screens.register.user.viewmodel.BaseRegisterUserTemplateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterUserViewModel @Inject constructor() : BaseRegisterUserTemplateViewModel() {
    private val _navigationEvent = Channel<AuthNavDestinations.RegisterUserScreen>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    override fun onRegisterClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.RegisterUserScreen.RegisterAddress)
        }
    }

    override fun onLoginClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.RegisterUserScreen.Login)
        }
    }

    override fun onTermsClick() {
        // Fallback seguro: pode abrir modal ou tela de termos no futuro
    }

    override fun onBackClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.RegisterUserScreen.Login)
        }
    }
}