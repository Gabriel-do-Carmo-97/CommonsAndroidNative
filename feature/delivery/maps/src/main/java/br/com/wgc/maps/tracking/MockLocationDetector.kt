package br.com.wgc.maps.tracking

import br.com.wgc.core.location.geofence.KalmanLocationFilter
import br.wgc.omnibackend.core.model.database.geo.LocationRequest
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import javax.inject.Inject

/**
 * Resultado da auditoria de integridade e verificação anti-fraude de uma coordenada GPS.
 *
 * @property isValid Indica se a coordenada é fidedigna e apta para exibição no mapa.
 * @property isMockDetected Flag que indica detecção de simulação de GPS ou movimento anômalo.
 * @property reason Motivo descritivo em caso de descarte da coordenada.
 * @property filteredLatitude Latitude suavizada após passagem pelo filtro de Kalman.
 * @property filteredLongitude Longitude suavizada após passagem pelo filtro de Kalman.
 */
data class LocationAuditResult(
    val isValid: Boolean,
    val isMockDetected: Boolean,
    val reason: String = "OK",
    val filteredLatitude: Double = 0.0,
    val filteredLongitude: Double = 0.0
)

/**
 * Detector anti-fraude e validador de precisão geográfica para o ecossistema de delivery.
 *
 * Utiliza o [KalmanLocationFilter] do CoreAndroidNative 1.5.1 para eliminar ruídos de alta frequência
 * e oscilações bruscas (jitter) de sinal de GPS, além de auditar anomalias de velocidade instantânea
 * superiores a 220 km/h (teletransporte / Mock Location via mock providers ou emuladores).
 *
 * ### 📌 Quando Usar:
 * Utilize antes de atualizar a rota ou marcador do entregador no mapa em tempo real
 * para evitar fraudes de posicionamento e teletransporte de entregas.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val detector = MockLocationDetector()
 * val audit = detector.auditAndFilter(locationRequest)
 * if (audit.isValid) {
 *     atualizarMapa(audit.filteredLatitude, audit.filteredLongitude)
 * } else {
 *     registrarAlertaFraude(audit.reason)
 * }
 * ```
 *
 * @param kalmanFilter Filtro estocástico de Kalman para suavização linear de coordenadas.
 */
class MockLocationDetector(
    private val kalmanFilter: KalmanLocationFilter
) {
    @Inject
    constructor() : this(KalmanLocationFilter())

    private var previousLocation: LocationRequest? = null

    companion object {
        /** Limite máximo de acurácia aceitável em metros (descarte de coordenadas com incerteza excessiva). */
        const val MAX_ACCURACY_THRESHOLD_METERS = 50.0f

        /** Velocidade máxima fisicamente plausível para entregadores em km/h. */
        const val MAX_PHYSICALLY_PLAUSIBLE_SPEED_KMH = 220.0

        /** Raio da Terra em quilômetros para cálculo de distância geodésica pela fórmula de Haversine. */
        private const val EARTH_RADIUS_KM = 6371.0
    }

    /**
     * Audita a integridade da leitura geográfica recebida e aplica o filtro de Kalman.
     *
     * ### 📌 Quando Usar:
     * Invocado a cada nova emissão recebida do stream de rastreamento do entregador.
     *
     * @param request Leitura de localização emitida pelo sensor ou recebida do OmniBackend.
     * @param accuracy Acurácia estimada em metros informada pelo GPS (padrão: 10.0m).
     * @return [LocationAuditResult] com o status de validação, detecção de mock e coordenadas filtradas.
     */
    fun auditAndFilter(
        request: LocationRequest,
        accuracy: Float = 10.0f
    ): LocationAuditResult {
        // Validação 1: Incerteza do GPS excessiva (> 50 metros)
        if (accuracy > MAX_ACCURACY_THRESHOLD_METERS) {
            return LocationAuditResult(
                isValid = false,
                isMockDetected = false,
                reason = "Acurácia insuficiente (${accuracy}m > ${MAX_ACCURACY_THRESHOLD_METERS}m)"
            )
        }

        val lastLoc = previousLocation
        if (lastLoc != null) {
            val deltaSeconds = ((request.timestamp - lastLoc.timestamp) / 1000.0).coerceAtLeast(0.001)
            val distanceKm = calculateHaversineDistanceKm(
                lastLoc.latitude, lastLoc.longitude,
                request.latitude, request.longitude
            )
            val speedKmh = (distanceKm / (deltaSeconds / 3600.0))

            // Validação 2: Detecção de salto impossível no espaço-tempo (Mock Location / GPS Spoofing)
            if (speedKmh > MAX_PHYSICALLY_PLAUSIBLE_SPEED_KMH) {
                return LocationAuditResult(
                    isValid = false,
                    isMockDetected = true,
                    reason = "Detecção de Mock Location: Velocidade anômala (${speedKmh.toInt()} km/h)"
                )
            }
        }

        previousLocation = request

        // Validação 3: Aplicação do filtro de Kalman estocástico do CoreAndroidNative
        val filtered = kalmanFilter.filter(
            request.latitude,
            request.longitude,
            accuracy,
            request.timestamp
        )

        return LocationAuditResult(
            isValid = true,
            isMockDetected = false,
            reason = "Coordenada válida e auditada",
            filteredLatitude = filtered.latitude,
            filteredLongitude = filtered.longitude
        )
    }

    /**
     * Reinicializa o histórico do detector e o filtro de Kalman.
     *
     * ### 📌 Quando Usar:
     * Invocado ao iniciar uma nova rota de entrega ou alternar de entregador.
     */
    fun reset() {
        previousLocation = null
        kalmanFilter.reset()
    }

    /**
     * Calcula a distância geodésica em quilômetros entre dois pontos pelo método Haversine.
     *
     * @param lat1 Latitude do primeiro ponto.
     * @param lon1 Longitude do primeiro ponto.
     * @param lat2 Latitude do segundo ponto.
     * @param lon2 Longitude do segundo ponto.
     * @return Distância em quilômetros.
     */
    private fun calculateHaversineDistanceKm(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }
}
