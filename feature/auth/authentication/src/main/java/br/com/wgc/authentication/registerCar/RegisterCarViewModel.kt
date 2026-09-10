package br.com.wgc.authentication.registerCar

import androidx.lifecycle.viewModelScope
import br.com.wgc.authentication.navigation.AuthNavDestinations
import br.com.wgc.ds_templates.screens.register.car.viewmodel.BaseRegisterCarScreenTemplateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel de gestÃ£o do cadastro veicular para parceiros de entrega ou mobilidade.
 */
@HiltViewModel
class RegisterCarViewModel @Inject constructor() : BaseRegisterCarScreenTemplateViewModel() {
    private val _navigationEvent = Channel<AuthNavDestinations.RegisterCar>(Channel.BUFFERED)

    /** Fluxo de eventos de navegaÃ§Ã£o para transiÃ§Ãµes do fluxo. */
    val navigationEvent = _navigationEvent.receiveAsFlow()

    /** Finaliza a etapa de cadastro do veÃ­culo. */
    override fun onRegisterClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.RegisterCar.RegisterUser)
        }
    }

    /** Retorna para a etapa de endereÃ§o. */
    override fun onBackClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.RegisterCar.RegisterAddress)
        }
    }
}