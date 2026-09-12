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
 * @property stepIndex Índice numérico ordinal do estágio.
 * @property label Rótulo amigável exibido na timeline.
 * @property description Descrição detalhada sobre o estágio atual.
 */
enum class OrderStatus(val stepIndex: Int, val label: String, val description: String) {
    /** Pedido aceito e registrado pela loja parceira. */
    CONFIRMED(1, "Pedido Confirmado", "O estabelecimento recebeu e confirmou seu pedido."),
    /** Produtos em fase de separação e cocção/embalagem. */
    PREPARING(2, "Em Preparação", "Seus itens estão sendo preparados com carinho."),
    /** Pedido em trânsito com o entregador parceiro. */
    OUT_FOR_DELIVERY(3, "Saiu para Entrega", "O entregador parceiro já está a caminho com seu pedido."),
    /** Entrega concluída com êxito no endereço do cliente. */
    DELIVERED(4, "Entregue", "Pedido entregue com sucesso! Bom apetite.")
}

/**
 * Estado imutável da tela de acompanhamento e rastreio do pedido.
 *
 * @property title Título do cabeçalho de rastreamento.
 * @property orderId Código identificador do pedido (ex: `#WGC-89421`).
 * @property status Estágio atual do pedido na máquina de estados.
 * @property estimatedMinutesRemaining Estimativa de minutos restantes para conclusão da entrega.
 * @property storeName Nome do estabelecimento expedidor.
 * @property driverName Nome e veículo do entregador responsável.
 * @property deliveryAddress Endereço físico completo de entrega.
 * @property itemsSummary Resumo textual simplificado dos itens comprados.
 * @property totalAmount Valor total pago no pedido formatado em moeda corrente.
 * @property isLoading Sinalizador de atualização em segundo plano.
 */
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

/**
 * ViewModel responsável pela evolução temporal do status do pedido e cálculos de previsão de entrega.
 */
@HiltViewModel
class OrderTrackingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(OrderTrackingUiState())

    /**
     * Fluxo de estado contendo as informações completas do rastreamento do pedido.
     */
    val uiState: StateFlow<OrderTrackingUiState> = _uiState.asStateFlow()

    /**
     * Avança linearmente o status do pedido para o próximo estágio na máquina de estados.
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
     * Define pontualmente um novo estágio para o pedido.
     *
     * @param newStatus Novo estágio [OrderStatus] a ser atribuído.
     */
    fun setStatus(newStatus: OrderStatus) {
        _uiState.update { it.copy(status = newStatus) }
    }
}