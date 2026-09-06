package br.com.gds.authentication.registerAddress

import androidx.lifecycle.viewModelScope
import br.com.gds.authentication.navigation.AuthNavDestinations
import br.com.wgc.ds_templates.screens.register.address.viewmodel.BaseRegisterAddressScreenTemplateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterAddressViewModel @Inject constructor() : BaseRegisterAddressScreenTemplateViewModel() {
    private val _navigationEvent = Channel<AuthNavDestinations.RegisterAddress>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    override fun onRegisterClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.RegisterAddress.RegisterUser)
        }
    }

    override fun onBackClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.RegisterAddress.RegisterUser)
        }
    }

    override fun onCepSearch(cep: String) {
        // Fallback seguro: busca de CEP a ser integrada com serviço de CEP
    }
}