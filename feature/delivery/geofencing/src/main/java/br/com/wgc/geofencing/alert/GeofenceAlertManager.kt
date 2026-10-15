package br.com.wgc.geofencing.alert

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Tipos de transição em cercas virtuais geográficas.
 */
enum class GeofenceTransitionType {
    /** Entidade cruzou o perímetro e adentrou a área cercada. */
    ENTERED,
    /** Entidade cruzou o perímetro e saiu da área cercada. */
    EXITED,
    /** Entidade permaneceu dentro da área pelo tempo limite estabelecido. */
    DWELL
}

/**
 * Alvo georreferenciado monitorado pelo sistema de geofencing.
 *
 * @property id Identificador exclusivo da cerca ou pedido.
 * @property latitude Latitude central do ponto de entrega.
 * @property longitude Longitude central do ponto de entrega.
 * @property radiusMeters Raio de alcance em metros (padrão de 500 metros para alerta de proximidade).
 * @property destinationLabel Rótulo amigável do destino (ex: "Casa do Cliente").
 */
data class GeofenceTarget(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Double = 500.0,
    val destinationLabel: String = "Destino"
)

/**
 * Evento disparado quando ocorre uma transição no perímetro da cerca virtual.
 *
 * @property targetId Identificador da cerca envolvida.
 * @property distanceMeters Distância calculada em metros até o centro do destino.
 * @property transitionType Tipo de transição detectada ([GeofenceTransitionType]).
 * @property timestamp Carimbo de data/hora do evento em milissegundos.
 * @property alertMessage Mensagem formatada recomendada para notificação push/local.
 */
data class GeofenceTransitionEvent(
    val targetId: String,
    val distanceMeters: Double,
    val transitionType: GeofenceTransitionType,
    val timestamp: Long,
    val alertMessage: String
)

/**
 * Gerenciador corporativo de Cercamento Virtual (Geofencing) e Alertas de Proximidade.
 *
 * Monitora as coordenadas GPS emitidas pelo entregador em relação ao endereço de entrega do cliente,
 * disparando eventos de alerta e notificações de aproximação quando o veículo penetra o raio de 500 metros.
 *
 * ### 📌 Quando Usar:
 * Utilize no serviço de rastreamento em segundo plano ou no ViewModel de entrega (:feature:delivery:geofencing)
 * para notificar o destinatário de que seu pedido está chegando na portaria/portão.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val alertManager = GeofenceAlertManager()
 *
 * // 1. Registrar a residência do cliente com cerca de 500m:
 * alertManager.registerTarget(GeofenceTarget("ord-1", -23.5505, -46.6333, radiusMeters = 500.0))
 *
 * // 2. A cada telemetria recebida da moto:
 * val events = alertManager.evaluateCurrentLocation(-23.5510, -46.6335)
 * events.forEach { event ->
 *     if (event.transitionType == GeofenceTransitionType.ENTERED) {
 *         notificacaoHelper.dispararPush(event.alertMessage)
 *     }
 * }
 * ```
 */
@Singleton
class GeofenceAlertManager @Inject constructor() {

    companion object {
        private const val EARTH_RADIUS_METERS = 6371000.0
    }

    private val targets = mutableMapOf<String, GeofenceTarget>()
    private val activeInsideTargets = mutableSetOf<String>()

    /**
     * Registra uma nova cerca virtual para monitoramento ativo.
     *
     * @param target Parâmetros geográficos do destino e raio.
     */
    fun registerTarget(target: GeofenceTarget) {
        targets[target.id] = target
    }

    /**
     * Remove uma cerca virtual após conclusão da entrega ou cancelamento.
     *
     * @param targetId Identificador da cerca a ser desativada.
     */
    fun removeTarget(targetId: String) {
        targets.remove(targetId)
        activeInsideTargets.remove(targetId)
    }

    /**
     * Limpa todas as cercas monitoradas e reseta estados de presença.
     */
    fun clearAllTargets() {
        targets.clear()
        activeInsideTargets.clear()
    }

    /**
     * Retorna a lista de todas as cercas registradas.
     *
     * @return Lista de [GeofenceTarget] ativos.
     */
    fun getRegisteredTargets(): List<GeofenceTarget> = targets.values.toList()

    /**
     * Avalia as coordenadas geográficas atuais do entregador contra todas as cercas cadastradas.
     *
     * Dispara eventos [GeofenceTransitionType.ENTERED] apenas na primeira vez que a entidade cruza para
     * dentro do perímetro (evitando repetição de notificações enquanto ela continuar lá dentro) e
     * [GeofenceTransitionType.EXITED] caso venha a se afastar.
     *
     * @param currentLat Latitude atualizada do condutor.
     * @param currentLng Longitude atualizada do condutor.
     * @param timestamp Carimbo de hora da leitura GPS em milissegundos (padrão: [System.currentTimeMillis]).
     * @return Lista de [GeofenceTransitionEvent] disparados nesta verificação.
     */
    fun evaluateCurrentLocation(
        currentLat: Double,
        currentLng: Double,
        timestamp: Long = System.currentTimeMillis()
    ): List<GeofenceTransitionEvent> {
        val triggeredEvents = mutableListOf<GeofenceTransitionEvent>()

        for (target in targets.values) {
            val distance = calculateDistanceMeters(
                lat1 = currentLat,
                lon1 = currentLng,
                lat2 = target.latitude,
                lon2 = target.longitude
            )

            val isInsideNow = distance <= target.radiusMeters
            val wasInsideBefore = activeInsideTargets.contains(target.id)

            if (isInsideNow && !wasInsideBefore) {
                // Transição de Entrada (ENTERED)
                activeInsideTargets.add(target.id)
                val roundedDistance = distance.toInt()
                triggeredEvents.add(
                    GeofenceTransitionEvent(
                        targetId = target.id,
                        distanceMeters = distance,
                        transitionType = GeofenceTransitionType.ENTERED,
                        timestamp = timestamp,
                        alertMessage = "Seu entregador está se aproximando! Distância aproximada: ${roundedDistance}m de ${target.destinationLabel}."
                    )
                )
            } else if (!isInsideNow && wasInsideBefore) {
                // Transição de Saída (EXITED)
                activeInsideTargets.remove(target.id)
                triggeredEvents.add(
                    GeofenceTransitionEvent(
                        targetId = target.id,
                        distanceMeters = distance,
                        transitionType = GeofenceTransitionType.EXITED,
                        timestamp = timestamp,
                        alertMessage = "O entregador se afastou da área de ${target.destinationLabel}."
                    )
                )
            }
        }

        return triggeredEvents
    }

    /**
     * Calcula a distância geodésica em metros entre duas coordenadas utilizando a fórmula de Haversine.
     *
     * @param lat1 Latitude do primeiro ponto.
     * @param lon1 Longitude do primeiro ponto.
     * @param lat2 Latitude do segundo ponto.
     * @param lon2 Longitude do segundo ponto.
     * @return Distância em metros.
     */
    fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2.0) * sin(dLat / 2.0) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(dLon / 2.0) * sin(dLon / 2.0)

        val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))
        return EARTH_RADIUS_METERS * c
    }
}
