package br.com.wgc.order_tracking.timeline

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/**
 * Fases sequenciais consolidadas na linha do tempo de rastreamento de entregas.
 */
enum class TimelineStage(val stepOrder: Int, val label: String) {
    /** Pedido confirmado e registrado pela cozinha/loja parceira. */
    CONFIRMED(1, "Confirmado"),
    /** Pedido em fase de preparação, separação ou embalagem. */
    PREPARING(2, "Em Preparo"),
    /** Pedido despachado e em trânsito com o condutor parceiro. */
    OUT_FOR_DELIVERY(3, "A Caminho"),
    /** Pedido recebido e validado com sucesso pelo destinatário. */
    DELIVERED(4, "Entregue")
}

/**
 * Registro de um marco/evento específico na linha do tempo do pedido.
 *
 * @property stage Estágio associado a este evento.
 * @property title Título exibido no marco visual.
 * @property description Descrição orientativa para o consumidor.
 * @property timestampMillis Carimbo de data/hora em milissegundos da conclusão (ou null se pendente).
 * @property isCompleted Indica se esta etapa já foi finalizada.
 * @property isCurrent Indica se esta etapa é a que está ocorrendo ativamente no momento.
 */
data class TimelineStepEvent(
    val stage: TimelineStage,
    val title: String,
    val description: String,
    val timestampMillis: Long?,
    val isCompleted: Boolean,
    val isCurrent: Boolean
)

/**
 * Estado consolidado da timeline do pedido com métricas de tempo preditivas.
 *
 * @property orderId Identificador único do pedido rastreado.
 * @property currentStage Estágio atual do pedido.
 * @property steps Lista de 4 etapas ordenadas com seus respectivos status e horários.
 * @property estimatedMinutesRemaining Estimativa preditiva de tempo restante em minutos.
 * @property estimatedDeliveryTimestamp Horário previsto de entrega em milissegundos (Unix epoch).
 */
data class OrderTimelineSnapshot(
    val orderId: String,
    val currentStage: TimelineStage,
    val steps: List<TimelineStepEvent>,
    val estimatedMinutesRemaining: Int,
    val estimatedDeliveryTimestamp: Long
)

/**
 * Gerenciador corporativo da Linha do Tempo Dinâmica e Estimativa Preditiva de Entrega.
 *
 * Atualiza os marcos temporais reais de cada etapa do pedido e calcula previsões de entrega (ETA)
 * adaptativas com base na distância restante, tempo médio de preparo e coeficiente de tráfego urbano.
 *
 * ### 📌 Quando Usar:
 * Injete no [br.com.wgc.order_tracking.screen.OrderTrackingViewModel] para renderizar a timeline
 * dinâmica com carimbos de horas exatos à medida que eventos de status chegam via backend.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val timelineManager = OrderTrackingTimelineManager()
 *
 * // 1. Criar timeline no momento da criação do pedido:
 * var timeline = timelineManager.createInitialTimeline("ORD-999", System.currentTimeMillis())
 *
 * // 2. Avançar para a etapa "Em Preparo":
 * timeline = timelineManager.advanceToStage(timeline, TimelineStage.PREPARING, System.currentTimeMillis())
 *
 * // 3. Calcular ETA preditivo com tráfego:
 * val etaMinutes = timelineManager.calculatePredictiveEta(distanceKm = 4.5, trafficFactor = 1.3)
 * ```
 */
@Singleton
class OrderTrackingTimelineManager @Inject constructor() {

    /**
     * Inicializa a timeline completa para um novo pedido criado.
     *
     * @param orderId Identificador único do pedido.
     * @param creationTime Carimbo de hora de confirmação da compra em milissegundos.
     * @param baseEtaMinutes Estimativa inicial padrão de entrega (padrão: 40 minutos).
     * @return [OrderTimelineSnapshot] configurado no estágio inicial [TimelineStage.CONFIRMED].
     */
    fun createInitialTimeline(
        orderId: String,
        creationTime: Long,
        baseEtaMinutes: Int = 40
    ): OrderTimelineSnapshot {
        val steps = listOf(
            TimelineStepEvent(
                stage = TimelineStage.CONFIRMED,
                title = "Pedido Confirmado",
                description = "Recebido pelo estabelecimento",
                timestampMillis = creationTime,
                isCompleted = true,
                isCurrent = true
            ),
            TimelineStepEvent(
                stage = TimelineStage.PREPARING,
                title = "Em Preparação",
                description = "Itens sendo preparados",
                timestampMillis = null,
                isCompleted = false,
                isCurrent = false
            ),
            TimelineStepEvent(
                stage = TimelineStage.OUT_FOR_DELIVERY,
                title = "Saiu para Entrega",
                description = "Entregador a caminho",
                timestampMillis = null,
                isCompleted = false,
                isCurrent = false
            ),
            TimelineStepEvent(
                stage = TimelineStage.DELIVERED,
                title = "Entregue",
                description = "Aproveite seu pedido",
                timestampMillis = null,
                isCompleted = false,
                isCurrent = false
            )
        )

        return OrderTimelineSnapshot(
            orderId = orderId,
            currentStage = TimelineStage.CONFIRMED,
            steps = steps,
            estimatedMinutesRemaining = baseEtaMinutes,
            estimatedDeliveryTimestamp = creationTime + (baseEtaMinutes * 60 * 1000L)
        )
    }

    /**
     * Avança a timeline para um novo estágio de entrega, carimbando o horário e atualizando estados.
     *
     * @param currentSnapshot Snapshot atual da timeline.
     * @param nextStage Novo estágio atingido pela entrega.
     * @param transitionTime Carimbo de data/hora do evento em milissegundos.
     * @return Novo [OrderTimelineSnapshot] com status e tempos atualizados.
     */
    fun advanceToStage(
        currentSnapshot: OrderTimelineSnapshot,
        nextStage: TimelineStage,
        transitionTime: Long
    ): OrderTimelineSnapshot {
        val updatedSteps = currentSnapshot.steps.map { step ->
            when {
                step.stage.stepOrder < nextStage.stepOrder -> {
                    step.copy(
                        isCompleted = true,
                        isCurrent = false,
                        timestampMillis = step.timestampMillis ?: transitionTime
                    )
                }
                step.stage == nextStage -> {
                    step.copy(
                        isCompleted = true,
                        isCurrent = true,
                        timestampMillis = transitionTime
                    )
                }
                else -> {
                    step.copy(
                        isCompleted = false,
                        isCurrent = false,
                        timestampMillis = null
                    )
                }
            }
        }

        val remainingMinutes = when (nextStage) {
            TimelineStage.CONFIRMED -> 35
            TimelineStage.PREPARING -> 20
            TimelineStage.OUT_FOR_DELIVERY -> 10
            TimelineStage.DELIVERED -> 0
        }

        return currentSnapshot.copy(
            currentStage = nextStage,
            steps = updatedSteps,
            estimatedMinutesRemaining = remainingMinutes,
            estimatedDeliveryTimestamp = if (nextStage == TimelineStage.DELIVERED) {
                transitionTime
            } else {
                transitionTime + (remainingMinutes * 60 * 1000L)
            }
        )
    }

    /**
     * Calcula a estimativa preditiva de tempo de entrega (ETA) em minutos.
     *
     * @param distanceKm Distância viária estimada em quilômetros.
     * @param trafficFactor Coeficiente de congestionamento de trânsito (1.0 = livre, 1.5 = tráfego pesado).
     * @param basePrepTimeMinutes Tempo restante estimado de preparação dos itens na loja.
     * @return Minutos estimados totais de entrega.
     */
    fun calculatePredictiveEta(
        distanceKm: Double,
        trafficFactor: Double = 1.0,
        basePrepTimeMinutes: Int = 10
    ): Int {
        val speedKmH = 25.0 / trafficFactor.coerceAtLeast(0.5)
        val travelTimeHours = distanceKm / speedKmH
        val travelTimeMinutes = (travelTimeHours * 60.0).roundToInt()
        return basePrepTimeMinutes + travelTimeMinutes
    }
}
