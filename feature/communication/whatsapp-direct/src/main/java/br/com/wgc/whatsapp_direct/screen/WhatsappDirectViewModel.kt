package br.com.wgc.whatsapp_direct.screen

import androidx.lifecycle.ViewModel
import br.com.wgc.whatsapp_direct.helper.WhatsappDirectHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class WhatsappDirectUiState(
    val title: String = "Envio Direto para WhatsApp",
    val recipientPhone: String = "(11) 99999-8888",
    val customerName: String = "Gabriel Carmo",
    val storeName: String = "WGC Express & Delivery",
    val itemsSummary: String = "• 1x Combo Burger Artesanal + Batata R$ 42,00\n• 1x Suco Natural de Laranja R$ 9,00",
    val totalAmount: String = "R$ 51,00",
    val deliveryAddress: String = "Rua Oscar Freire, 900 - Jardins, SP",
    val paymentMethod: String = "Pix na Entrega",
    val notes: String = "Sem cebola no burger por favor",
    val formattedMessage: String = ""
)

@HiltViewModel
class WhatsappDirectViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(WhatsappDirectUiState())
    val uiState: StateFlow<WhatsappDirectUiState> = _uiState.asStateFlow()

    init {
        updateMessage()
    }

    fun updateRecipientPhone(phone: String) {
        _uiState.update { it.copy(recipientPhone = phone) }
    }

    fun updateCustomerName(name: String) {
        _uiState.update { it.copy(customerName = name) }
        updateMessage()
    }

    fun updateNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
        updateMessage()
    }

    private fun updateMessage() {
        _uiState.update { current ->
            val formatted = WhatsappDirectHelper.formatDeliveryOrderMessage(
                storeName = current.storeName,
                customerName = current.customerName,
                itemsSummary = current.itemsSummary,
                totalAmount = current.totalAmount,
                deliveryAddress = current.deliveryAddress,
                paymentMethod = current.paymentMethod,
                notes = current.notes
            )
            current.copy(formattedMessage = formatted)
        }
    }
}
