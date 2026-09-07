package br.com.gds.order_tracking.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

enum class OrderStatus(val stepIndex: Int, val label: String, val description: String) {
    CONFIRMED(1, "Pedido Confirmado", "O estabelecimento recebeu e confirmou seu pedido."),
    PREPARING(2, "Em Preparação", "Seus itens estão sendo preparados com carinho."),
    OUT_FOR_DELIVERY(3, "Saiu para Entrega", "O entregador parceiro já está a caminho com seu pedido."),
    DELIVERED(4, "Entregue", "Pedido entregue com sucesso! Bom apetite.")
}

data class OrderTrackingUiState(
    val title: String = "Acompanhamento do Pedido",
    val orderId: String = "#WGC-89421",
    val status: OrderStatus = OrderStatus.PREPARING,
    val estimatedMinutesRemaining: Int = 25,
    val storeName: String = "WGC Gourmet & Delivery",
    val driverName: String = "Lucas Silva (Moto Honda CB)",
    val deliveryAddress: String = "Av. Paulista, 1578 - Apto 82, São Paulo - SP",
    val itemsSummary: String = "1x Pizza Especial Pepperoni, 1x Refrigerante Lata 350ml",
    val totalAmount: String = "R$ 66,40",
    val isLoading: Boolean = false
)

@HiltViewModel
class OrderTrackingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(OrderTrackingUiState())
    val uiState: StateFlow<OrderTrackingUiState> = _uiState.asStateFlow()

    fun advanceStatus() {
        _uiState.update { current ->
            val nextStatus = when (current.status) {
                OrderStatus.CONFIRMED -> OrderStatus.PREPARING
                OrderStatus.PREPARING -> OrderStatus.OUT_FOR_DELIVERY
                OrderStatus.OUT_FOR_DELIVERY -> OrderStatus.DELIVERED
                OrderStatus.DELIVERED -> OrderStatus.DELIVERED
            }
            val nextEta = when (nextStatus) {
                OrderStatus.CONFIRMED -> 35
                OrderStatus.PREPARING -> 20
                OrderStatus.OUT_FOR_DELIVERY -> 10
                OrderStatus.DELIVERED -> 0
            }
            current.copy(status = nextStatus, estimatedMinutesRemaining = nextEta)
        }
    }

    fun setStatus(newStatus: OrderStatus) {
        _uiState.update { it.copy(status = newStatus) }
    }
}
