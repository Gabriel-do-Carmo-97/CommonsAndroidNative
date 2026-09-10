package br.com.wgc.order_tracking.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Fases sequenciais do ciclo de vida de uma entrega.
 *
 * @property stepIndex Ãndice numÃ©rico ordinal do estÃ¡gio.
 * @property label RÃ³tulo amigÃ¡vel exibido na timeline.
 * @property description DescriÃ§Ã£o detalhada sobre o estÃ¡gio atual.
 */
enum class OrderStatus(val stepIndex: Int, val label: String, val description: String) {
    /** Pedido aceito e registrado pela loja parceira. */
    CONFIRMED(1, "Pedido Confirmado", "O estabelecimento recebeu e confirmou seu pedido."),
    /** Produtos em fase de separaÃ§Ã£o e cocÃ§Ã£o/embalagem. */
    PREPARING(2, "Em PreparaÃ§Ã£o", "Seus itens estÃ£o sendo preparados com carinho."),
    /** Pedido em trÃ¢nsito com o entregador parceiro. */
    OUT_FOR_DELIVERY(3, "Saiu para Entrega", "O entregador parceiro jÃ¡ estÃ¡ a caminho com seu pedido."),
    /** Entrega concluÃ­da com Ãªxito no endereÃ§o do cliente. */
    DELIVERED(4, "Entregue", "Pedido entregue com sucesso! Bom apetite.")
}

/**
 * Estado imutÃ¡vel da tela de acompanhamento e rastreio do pedido.
 *
 * @property title TÃ­tulo do cabeÃ§alho de rastreamento.
 * @property orderId CÃ³digo identificador do pedido (ex: `#WGC-89421`).
 * @property status EstÃ¡gio atual do pedido na mÃ¡quina de estados.
 * @property estimatedMinutesRemaining Estimativa de minutos restantes para conclusÃ£o da entrega.
 * @property storeName Nome do estabelecimento expedidor.
 * @property driverName Nome e veÃ­culo do entregador responsÃ¡vel.
 * @property deliveryAddress EndereÃ§o fÃ­sico completo de entrega.
 * @property itemsSummary Resumo textual simplificado dos itens comprados.
 * @property totalAmount Valor total pago no pedido formatado em moeda corrente.
 * @property isLoading Sinalizador de atualizaÃ§Ã£o em segundo plano.
 */
data class OrderTrackingUiState(
    val title: String = "Acompanhamento do Pedido",
    val orderId: String = "#WGC-89421",
    val status: OrderStatus = OrderStatus.PREPARING,
    val estimatedMinutesRemaining: Int = 25,
    val storeName: String = "WGC Gourmet & Delivery",
    val driverName: String = "Lucas Silva (Moto Honda CB)",
    val deliveryAddress: String = "Av. Paulista, 1578 - Apto 82, SÃ£o Paulo - SP",
    val itemsSummary: String = "1x Pizza Especial Pepperoni, 1x Refrigerante Lata 350ml",
    val totalAmount: String = "R$ 66,40",
    val isLoading: Boolean = false
)

/**
 * ViewModel responsÃ¡vel pela evoluÃ§Ã£o temporal do status do pedido e cÃ¡lculos de previsÃ£o de entrega.
 */
@HiltViewModel
class OrderTrackingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(OrderTrackingUiState())

    /**
     * Fluxo de estado contendo as informaÃ§Ãµes completas do rastreamento do pedido.
     */
    val uiState: StateFlow<OrderTrackingUiState> = _uiState.asStateFlow()

    /**
     * AvanÃ§a linearmente o status do pedido para o prÃ³ximo estÃ¡gio na mÃ¡quina de estados.
     */
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

    /**
     * Define pontualmente um novo estÃ¡gio para o pedido.
     *
     * @param newStatus Novo estÃ¡gio [OrderStatus] a ser atribuÃ­do.
     */
    fun setStatus(newStatus: OrderStatus) {
        _uiState.update { it.copy(status = newStatus) }
    }
}