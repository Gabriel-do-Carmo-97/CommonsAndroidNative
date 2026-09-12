package br.com.wgc.quotation.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Representa um item individual em uma proposta comercial de orçamento.
 *
 * @param id Identificador único do item.
 * @param title Descrição ou título do produto/serviço orçado.
 * @param quantity Quantidade de unidades cotadas.
 * @param unitPrice Preço unitário por unidade do item.
 * @property id Identificador único do item.
 * @property title Descrição ou título do produto/serviço orçado.
 * @property quantity Quantidade de unidades cotadas.
 * @property unitPrice Preço unitário por unidade do item.
 * @property total Preço total calculado pela multiplicação da quantidade pelo preço unitário.
 */
data class QuotationItem(
    val id: String,
    val title: String,
    val quantity: Int,
    val unitPrice: Double
) {
    val total: Double get() = quantity * unitPrice
}

/**
 * Estado imutável da tela de simulação e geração de orçamentos.
 *
 * @property title Título do cabeçalho da proposta.
 * @property clientName Nome do cliente destinatário do orçamento.
 * @property items Lista de itens e serviços inclusos na cotação.
 * @property discountPercent Percentual de desconto concedido (0 a 100).
 * @property subtotal Soma dos valores brutos dos itens sem aplicação de desconto.
 * @property discountAmount Valor monetário deduzido correspondente ao desconto.
 * @property total Valor líquido final a ser pago pelo cliente.
 * @property isGenerated Sinalizador booleano indicando se o orçamento final foi emitido.
 */
data class QuotationUiState(
    val title: String = "Simulador & Gerador de Orçamentos",
    val clientName: String = "Cliente VIP WGC",
    val items: List<QuotationItem> = listOf(
        QuotationItem("1", "Mão de Obra Especializada", 1, 350.0),
        QuotationItem("2", "Kit de Peças e Componentes", 2, 120.0),
        QuotationItem("3", "Taxa de Deslocamento Técnico", 1, 50.0)
    ),
    val discountPercent: Int = 10,
    val subtotal: Double = 640.0,
    val discountAmount: Double = 64.0,
    val total: Double = 576.0,
    val isGenerated: Boolean = false
)

/**
 * ViewModel responsável pela lógica de orçamentos rápidos e geração de mensagens de cotação.
 *
 * Gerencia a lista dinâmica de itens, aplicação reativa de descontos
 * e formatação de texto otimizada para envio via WhatsApp ou mensageiros.
 */
@HiltViewModel
class QuotationViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(QuotationUiState())

    /**
     * Fluxo observável com o estado do orçamento em tempo real.
     */
    val uiState: StateFlow<QuotationUiState> = _uiState.asStateFlow()

    /**
     * Atualiza o nome do cliente associado à proposta comercial.
     *
     * @param name Nome ou razão social do cliente.
     */
    fun updateClientName(name: String) {
        _uiState.update { it.copy(clientName = name) }
    }

    /**
     * Adiciona um novo item ou serviço ao orçamento recalculando subtotal e total.
     *
     * @param title Descrição detalhada do item.
     * @param unitPrice Preço unitário do produto ou serviço.
     */
    fun addItem(title: String, unitPrice: Double) {
        if (title.isBlank() || unitPrice <= 0) return
        _uiState.update { current ->
            val newItem = QuotationItem(System.currentTimeMillis().toString(), title, 1, unitPrice)
            val newItems = current.items + newItem
            recalculate(current.copy(items = newItems))
        }
    }

    /**
     * Remove um item da cotação através do seu identificador único.
     *
     * @param id Identificador do item a ser excluído.
     */
    fun removeItem(id: String) {
        _uiState.update { current ->
            val newItems = current.items.filterNot { it.id == id }
            recalculate(current.copy(items = newItems))
        }
    }

    /**
     * Define o percentual de desconto a ser aplicado sobre o valor bruto do orçamento.
     *
     * @param percent Percentual inteiro de desconto (limitado entre 0 e 100).
     */
    fun setDiscount(percent: Int) {
        _uiState.update { current ->
            recalculate(current.copy(discountPercent = percent.coerceIn(0, 100)))
        }
    }

    /**
     * Marca o orçamento como formalmente gerado e aprovado para despacho.
     */
    fun generateQuotation() {
        _uiState.update { it.copy(isGenerated = true) }
    }

    /**
     * Gera o texto padronizado com markdown do WhatsApp com o detalhamento completo do orçamento.
     *
     * @return String formatada contendo cabeçalho, itens listados, subtotal, descontos e total.
     */
    fun generateWhatsAppText(): String {
        val state = _uiState.value
        val itemsText = state.items.joinToString("\n") { "• ${it.quantity}x ${it.title} - R$ ${String.format("%.2f", it.total)}" }
        return """
            📋 *ORÇAMENTO PERSONALIZADO WGC*
            Cliente: *${state.clientName}*
            
            *Itens Solicitados:*
            $itemsText
            
            Subtotal: R$ ${String.format("%.2f", state.subtotal)}
            Desconto (${state.discountPercent}%): -R$ ${String.format("%.2f", state.discountAmount)}
            *Total: R$ ${String.format("%.2f", state.total)}*
            
            _Orçamento válido por 7 dias._
        """.trimIndent()
    }

    /**
     * Recalcula os totais e descontos do orçamento com base na lista de itens e percentual configurado.
     *
     * @param state Estado atual do orçamento a ser recalculado.
     * @return Novo estado [QuotationUiState] com totais devidamente computados.
     */
    private fun recalculate(state: QuotationUiState): QuotationUiState {
        val subtotal = state.items.sumOf { it.total }
        val discountAmount = subtotal * (state.discountPercent / 100.0)
        val total = (subtotal - discountAmount).coerceAtLeast(0.0)
        return state.copy(
            subtotal = subtotal,
            discountAmount = discountAmount,
            total = total
        )
    }
}