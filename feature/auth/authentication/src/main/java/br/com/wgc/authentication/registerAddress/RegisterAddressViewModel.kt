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
 * ViewModel responsável pela validação e cadastro de endereços do usuário.
 */
@HiltViewModel
class RegisterAddressViewModel @Inject constructor() : BaseRegisterAddressScreenTemplateViewModel() {

    private val _navigationEvent = Channel<AuthNavDestinations.RegisterAddress>(Channel.BUFFERED)

    /** Fluxo de eventos de navegação para transições de tela. */
    val navigationEvent = _navigationEvent.receiveAsFlow()

    /** Finaliza o registro do endereço e prossegue no fluxo. */
    override fun onRegisterClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.RegisterAddress.RegisterUser)
        }
    }

    /** Retorna à etapa anterior de cadastro. */
    override fun onBackClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.RegisterAddress.RegisterUser)
        }
    }

    /**
     * Valida e formata o CEP digitado pelo usuário.
     *
     * @param cep Sequência numérica do código de endereçamento postal.
     */
    override fun onCepSearch(cep: String) {
        if (!cep.isValidCep()) {
            updateState {
                it.copy(
                    cepError = "CEP inválido (deve conter 8 dígitos numéricos)",
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