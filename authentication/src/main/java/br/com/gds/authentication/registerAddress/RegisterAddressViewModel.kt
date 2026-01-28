package br.com.gds.authentication.registerAddress

import br.com.gds.authentication.navigation.AuthNavDestinations
import br.com.wgc.ds_templates.screens.register.address.viewmodel.BaseRegisterAddressScreenTemplateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class RegisterAddressViewModel @Inject constructor(): BaseRegisterAddressScreenTemplateViewModel() {
    private val _navigationEvent = Channel<AuthNavDestinations.RegisterAddress>()
    val navigationEvent = _navigationEvent.receiveAsFlow()
    override fun onRegisterClick() {
        TODO("Not yet implemented")
    }

    override fun onBackClick() {
        TODO("Not yet implemented")
    }

    override fun onCepSearch(cep: String) {
        TODO("Not yet implemented")
    }
}