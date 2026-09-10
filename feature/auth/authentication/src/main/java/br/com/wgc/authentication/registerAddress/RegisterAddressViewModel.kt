package br.com.wgc.authentication.registerAddress

import androidx.lifecycle.viewModelScope
import br.com.wgc.authentication.navigation.AuthNavDestinations
import br.com.wgc.core.formatters.formatCep
import br.com.wgc.core.validators.isValidCep
import br.com.wgc.ds_templates.screens.register.address.viewmodel.BaseRegisterAddressScreenTemplateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsÃ¡vel pela validaÃ§Ã£o e cadastro de endereÃ§os do usuÃ¡rio.
 */
@HiltViewModel
class RegisterAddressViewModel @Inject constructor() : BaseRegisterAddressScreenTemplateViewModel() {

    private val _navigationEvent = Channel<AuthNavDestinations.RegisterAddress>(Channel.BUFFERED)

    /** Fluxo de eventos de navegaÃ§Ã£o para transiÃ§Ãµes de tela. */
    val navigationEvent = _navigationEvent.receiveAsFlow()

    /** Finaliza o registro do endereÃ§o e prossegue no fluxo. */
    override fun onRegisterClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.RegisterAddress.RegisterUser)
        }
    }

    /** Retorna Ã  etapa anterior de cadastro. */
    override fun onBackClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.RegisterAddress.RegisterUser)
        }
    }

    /**
     * Valida e formata o CEP digitado pelo usuÃ¡rio.
     *
     * @param cep SequÃªncia numÃ©rica do cÃ³digo de endereÃ§amento postal.
     */
    override fun onCepSearch(cep: String) {
        if (!cep.isValidCep()) {
            updateState {
                it.copy(
                    cepError = "CEP invÃ¡lido (deve conter 8 dÃ­gitos numÃ©ricos)",
                    isCepLoading = false
                )
            }
            return
        }

        updateState {
            it.copy(
                cepError = null,
                isCepLoading = false,
                address = it.address.copy(cep = cep.formatCep())
            )
        }
    }
}