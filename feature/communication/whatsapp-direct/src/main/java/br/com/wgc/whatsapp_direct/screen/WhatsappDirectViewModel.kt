package br.com.wgc.whatsapp_direct.screen

import androidx.lifecycle.ViewModel
import br.com.wgc.whatsapp_direct.helper.WhatsappDirectHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Estado imutável da tela de pré-visualização e despacho direto para o WhatsApp.
 *
 * @property title Título do cabeçalho da tela.
 * @property recipientPhone Número de telefone comercial do WhatsApp de destino.
 * @property customerName Nome informado pelo comprador.
 * @property storeName Nome da loja de destino.
 * @property itemsSummary Resumo em linhas dos itens selecionados.
 * @property totalAmount Total financeiro formatado do pedido.
 * @property deliveryAddress Endereço residencial ou comercial de entrega.
 * @property paymentMethod Método de pagamento escolhido.
 * @property notes Instruções ou observações extras ao lojista.
 * @property formattedMessage Texto final formatado pronto para envio.
 */
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

/**
 * ViewModel responsável pela montagem reativa da mensagem estruturada para envio via WhatsApp.
 */
@HiltViewModel
class WhatsappDirectViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(WhatsappDirectUiState())

    /**
     * Fluxo reativo do estado da mensagem do WhatsApp.
     */
    val uiState: StateFlow<WhatsappDirectUiState> = _uiState.asStateFlow()

    init {
        updateMessage()
    }

    /**
     * Atualiza o telefone do destinatário do pedido.
     *
     * @param phone String com o número telefônico.
     */
    fun updateRecipientPhone(phone: String) {
        _uiState.update { it.copy(recipientPhone = phone) }
    }

    /**
     * Altera o nome do cliente associado ao pedido.
     *
     * @param name Nome do comprador.
     */
    fun updateCustomerName(name: String) {
        _uiState.update { it.copy(customerName = name) }
        updateMessage()
    }

    /**
     * Atualiza o campo de observações adicionais do pedido.
     *
     * @param notes Texto de instrução ou observação.
     */
    fun updateNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
        updateMessage()
    }

    /**
     * Regenera a mensagem completa com base nos atributos do estado atual.
     */
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