package br.com.wgc.quotation.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Representa um item individual em uma proposta comercial de orÃ§amento.
 *
 * @param id Identificador Ãºnico do item.
 * @param title DescriÃ§Ã£o ou tÃ­tulo do produto/serviÃ§o orÃ§ado.
 * @param quantity Quantidade de unidades cotadas.
 * @param unitPrice PreÃ§o unitÃ¡rio por unidade do item.
 * @property id Identificador Ãºnico do item.
 * @property title DescriÃ§Ã£o ou tÃ­tulo do produto/serviÃ§o orÃ§ado.
 * @property quantity Quantidade de unidades cotadas.
 * @property unitPrice PreÃ§o unitÃ¡rio por unidade do item.
 * @property total PreÃ§o total calculado pela multiplicaÃ§Ã£o da quantidade pelo preÃ§o unitÃ¡rio.
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
 * Estado imutÃ¡vel da tela de simulaÃ§Ã£o e geraÃ§Ã£o de orÃ§amentos.
 *
 * @property title TÃ­tulo do cabeÃ§alho da proposta.
 * @property clientName Nome do cliente destinatÃ¡rio do orÃ§amento.
 * @property items Lista de itens e serviÃ§os inclusos na cotaÃ§Ã£o.
 * @property discountPercent Percentual de desconto concedido (0 a 100).
 * @property subtotal Soma dos valores brutos dos itens sem aplicaÃ§Ã£o de desconto.
 * @property discountAmount Valor monetÃ¡rio deduzido correspondente ao desconto.
 * @property total Valor lÃ­quido final a ser pago pelo cliente.
 * @property isGenerated Sinalizador booleano indicando se o orÃ§amento final foi emitido.
 */
data class QuotationUiState(
    val title: String = "Simulador & Gerador de OrÃ§amentos",
    val clientName: String = "Cliente VIP WGC",
    val items: List<QuotationItem> = listOf(
        QuotationItem("1", "MÃ£o de Obra Especializada", 1, 350.0),
        QuotationItem("2", "Kit de PeÃ§as e Componentes", 2, 120.0),
        QuotationItem("3", "Taxa de Deslocamento TÃ©cnico", 1, 50.0)
    ),
    val discountPercent: Int = 10,
    val subtotal: Double = 640.0,
    val discountAmount: Double = 64.0,
    val total: Double = 576.0,
    val isGenerated: Boolean = false
)

/**
 * ViewModel responsÃ¡vel pela lÃ³gica de orÃ§amentos rÃ¡pidos e geraÃ§Ã£o de mensagens de cotaÃ§Ã£o.
 *
 * Gerencia a lista dinÃ¢mica de itens, aplicaÃ§Ã£o reativa de descontos
 * e formataÃ§Ã£o de texto otimizada para envio via WhatsApp ou mensageiros.
 */
@HiltViewModel
class QuotationViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(QuotationUiState())

    /**
     * Fluxo observÃ¡vel com o estado do orÃ§amento em tempo real.
     */
    val uiState: StateFlow<QuotationUiState> = _uiState.asStateFlow()

    /**
     * Atualiza o nome do cliente associado Ã  proposta comercial.
     *
     * @param name Nome ou razÃ£o social do cliente.
     */
    fun updateClientName(name: String) {
        _uiState.update { it.copy(clientName = name) }
    }

    /**
     * Adiciona um novo item ou serviÃ§o ao orÃ§amento recalculando subtotal e total.
     *
     * @param title DescriÃ§Ã£o detalhada do item.
     * @param unitPrice PreÃ§o unitÃ¡rio do produto ou serviÃ§o.
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
     * Remove um item da cotaÃ§Ã£o atravÃ©s do seu identificador Ãºnico.
     *
     * @param id Identificador do item a ser excluÃ­do.
     */
    fun removeItem(id: String) {
        _uiState.update { current ->
            val newItems = current.items.filterNot { it.id == id }
            recalculate(current.copy(items = newItems))
        }
    }

    /**
     * Define o percentual de desconto a ser aplicado sobre o valor bruto do orÃ§amento.
     *
     * @param percent Percentual inteiro de desconto (limitado entre 0 e 100).
     */
    fun setDiscount(percent: Int) {
        _uiState.update { current ->
            recalculate(current.copy(discountPercent = percent.coerceIn(0, 100)))
        }
    }

    /**
     * Marca o orÃ§amento como formalmente gerado e aprovado para despacho.
     */
    fun generateQuotation() {
        _uiState.update { it.copy(isGenerated = true) }
    }

    /**
     * Gera o texto padronizado com markdown do WhatsApp com o detalhamento completo do orÃ§amento.
     *
     * @return String formatada contendo cabeÃ§alho, itens listados, subtotal, descontos e total.
     */
    fun generateWhatsAppText(): String {
        val state = _uiState.value
        val itemsText = state.items.joinToString("\n") { "â€¢ ${it.quantity}x ${it.title} - R$ ${String.format("%.2f", it.total)}" }
        return """
            ðŸ“‹ *ORÃ‡AMENTO PERSONALIZADO WGC*
            Cliente: *${state.clientName}*
            
            *Itens Solicitados:*
            $itemsText
            
            Subtotal: R$ ${String.format("%.2f", state.subtotal)}
            Desconto (${state.discountPercent}%): -R$ ${String.format("%.2f", state.discountAmount)}
            *Total: R$ ${String.format("%.2f", state.total)}*
            
            _OrÃ§amento vÃ¡lido por 7 dias._
        """.trimIndent()
    }

    /**
     * Recalcula os totais e descontos do orÃ§amento com base na lista de itens e percentual configurado.
     *
     * @param state Estado atual do orÃ§amento a ser recalculado.
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