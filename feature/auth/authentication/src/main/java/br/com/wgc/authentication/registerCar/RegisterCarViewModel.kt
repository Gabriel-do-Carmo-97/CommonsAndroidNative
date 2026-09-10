package br.com.wgc.authentication.registerCar

import androidx.lifecycle.viewModelScope
import br.com.wgc.authentication.navigation.AuthNavDestinations
import br.com.wgc.ds_templates.screens.register.car.viewmodel.BaseRegisterCarScreenTemplateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterCarViewModel @Inject constructor() : BaseRegisterCarScreenTemplateViewModel() {
    private val _navigationEvent = Channel<AuthNavDestinations.RegisterCar>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    override fun onRegisterClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.RegisterCar.RegisterUser)
        }
    }

    override fun onBackClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.RegisterCar.RegisterAddress)
        }
    }
}