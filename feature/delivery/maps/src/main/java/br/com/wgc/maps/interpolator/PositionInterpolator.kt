package br.com.wgc.maps.interpolator

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Ponto geográfico de coordenadas com latitude e longitude em graus decimais.
 *
 * @property latitude Latitude geográfica.
 * @property longitude Longitude geográfica.
 */
data class LatLngPoint(
    val latitude: Double,
    val longitude: Double
)

/**
 * Interpolador matemático e calculador de rumo (Bearing) para animação contínua de veículos no mapa.
 *
 * Elimina os saltos bruscos ("teletransporte") entre atualizações discretas de telemetria GPS,
 * gerando pontos intermediários contínuos através de interpolação linear/geodésica e calculando
 * a rotação azimutal exata do ícone do veículo em direção ao destino.
 *
 * ### 📌 Quando Usar:
 * Utilize no ViewModel ou renderizador de mapa em tempo real (:feature:delivery:maps) sempre que uma
 * nova coordenada GPS do entregador for recebida pelo stream WebSocket ou Firestore.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val interpolator = PositionInterpolator()
 * val pStart = LatLngPoint(-23.5505, -46.6333)
 * val pEnd = LatLngPoint(-23.5510, -46.6340)
 *
 * // Interpolar posição intermediária (ex: 50% da animação):
 * val midPoint = interpolator.interpolate(pStart, pEnd, fraction = 0.5f)
 *
 * // Calcular rumo (bearing em graus) para orientar o ícone da moto/carro:
 * val bearing = interpolator.computeBearing(pStart, pEnd)
 * ```
 */
@Singleton
class PositionInterpolator @Inject constructor() {

    companion object {
        private const val EARTH_RADIUS_METERS = 6371000.0
    }

    /**
     * Interpola linearmente entre duas coordenadas geográficas com base em uma fração normalizada [0.0, 1.0].
     *
     * @param start Coordenada inicial de partida.
     * @param end Coordenada final de destino.
     * @param fraction Progresso da animação entre 0.0 (início) e 1.0 (fim).
     * @return [LatLngPoint] intermediário resultante.
     */
    fun interpolate(start: LatLngPoint, end: LatLngPoint, fraction: Float): LatLngPoint {
        val clampedFraction = fraction.coerceIn(0.0f, 1.0f).toDouble()
        val lat = (end.latitude - start.latitude) * clampedFraction + start.latitude
        val lng = (end.longitude - start.longitude) * clampedFraction + start.longitude
        return LatLngPoint(latitude = lat, longitude = lng)
    }

    /**
     * Calcula o azimute/rumo geodésico (bearing) entre dois pontos na Terra em graus [0.0, 360.0).
     *
     * @param start Ponto geográfico de origem.
     * @param end Ponto geográfico de destino.
     * @return Ângulo em graus onde 0° = Norte, 90° = Leste, 180° = Sul, 270° = Oeste.
     */
    fun computeBearing(start: LatLngPoint, end: LatLngPoint): Float {
        val lat1 = Math.toRadians(start.latitude)
        val lat2 = Math.toRadians(end.latitude)
        val dLng = Math.toRadians(end.longitude - start.longitude)

        val y = sin(dLng) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLng)

        val initialBearing = Math.toDegrees(atan2(y, x))
        val normalizedBearing = (initialBearing + 360.0) % 360.0
        return normalizedBearing.toFloat()
    }

    /**
     * Interpola suavemente a rotação do veículo utilizando o caminho de menor arco (Shortest Path Arc).
     *
     * Evita giros indesejados de 360° ao transicionar entre quadrantes próximos de 0° e 360° (ex: de 350° para 10°).
     *
     * @param startAngle Ângulo de rotação inicial em graus.
     * @param endAngle Ângulo de rotação desejado em graus.
     * @param fraction Progresso da interpolação angular [0.0, 1.0].
     * @return Ângulo interpolado normalizado entre 0.0 e 360.0 graus.
     */
    fun interpolateRotation(startAngle: Float, endAngle: Float, fraction: Float): Float {
        val clampedFraction = fraction.coerceIn(0.0f, 1.0f)
        val diff = ((endAngle - startAngle + 180.0f) % 360.0f + 360.0f) % 360.0f - 180.0f
        val result = startAngle + diff * clampedFraction
        return (result % 360.0f + 360.0f) % 360.0f
    }

    /**
     * Calcula a distância geodésica em metros entre dois pontos utilizando a fórmula de Haversine.
     *
     * @param p1 Primeiro ponto geográfico.
     * @param p2 Segundo ponto geográfico.
     * @return Distância em metros.
     */
    fun computeDistanceMeters(p1: LatLngPoint, p2: LatLngPoint): Double {
        val lat1Rad = Math.toRadians(p1.latitude)
        val lat2Rad = Math.toRadians(p2.latitude)
        val dLat = Math.toRadians(p2.latitude - p1.latitude)
        val dLng = Math.toRadians(p2.longitude - p1.longitude)

        val a = sin(dLat / 2.0) * sin(dLat / 2.0) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(dLng / 2.0) * sin(dLng / 2.0)

        val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))
        return EARTH_RADIUS_METERS * c
    }
}
