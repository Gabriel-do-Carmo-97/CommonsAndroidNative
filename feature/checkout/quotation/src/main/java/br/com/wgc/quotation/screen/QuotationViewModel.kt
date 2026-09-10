package br.com.wgc.quotation.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class QuotationItem(
    val id: String,
    val title: String,
    val quantity: Int,
    val unitPrice: Double
) {
    val total: Double get() = quantity * unitPrice
}

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

@HiltViewModel
class QuotationViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(QuotationUiState())
    val uiState: StateFlow<QuotationUiState> = _uiState.asStateFlow()

    fun updateClientName(name: String) {
        _uiState.update { it.copy(clientName = name) }
    }

    fun addItem(title: String, unitPrice: Double) {
        if (title.isBlank() || unitPrice <= 0) return
        _uiState.update { current ->
            val newItem = QuotationItem(System.currentTimeMillis().toString(), title, 1, unitPrice)
            val newItems = current.items + newItem
            recalculate(current.copy(items = newItems))
        }
    }

    fun removeItem(id: String) {
        _uiState.update { current ->
            val newItems = current.items.filterNot { it.id == id }
            recalculate(current.copy(items = newItems))
        }
    }

    fun setDiscount(percent: Int) {
        _uiState.update { current ->
            recalculate(current.copy(discountPercent = percent.coerceIn(0, 100)))
        }
    }

    fun generateQuotation() {
        _uiState.update { it.copy(isGenerated = true) }
    }

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
