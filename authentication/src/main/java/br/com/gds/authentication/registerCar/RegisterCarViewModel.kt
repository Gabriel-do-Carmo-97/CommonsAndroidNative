package br.com.gds.authentication.registerCar

import br.com.gds.authentication.navigation.AuthNavDestinations
import br.com.wgc.ds_templates.screens.register.car.viewmodel.BaseRegisterCarScreenTemplateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class RegisterCarViewModel @Inject constructor(): BaseRegisterCarScreenTemplateViewModel(){
    private val _navigationEvent = Channel<AuthNavDestinations.RegisterCar>()
    val navigationEvent = _navigationEvent.receiveAsFlow()
    override fun onRegisterClick() {
        TODO("Not yet implemented")
    }

    override fun onBackClick() {
        TODO("Not yet implemented")
    }
}