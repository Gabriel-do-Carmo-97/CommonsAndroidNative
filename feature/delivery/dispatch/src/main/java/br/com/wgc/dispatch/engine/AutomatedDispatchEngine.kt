package br.com.wgc.dispatch.engine

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Modalidade de veículo utilizado pelo condutor parceiro.
 */
enum class VehicleType(val defaultMaxCapacityKg: Double) {
    /** Bicicleta convencional ou elétrica (pedidos leves até 5kg). */
    BICYCLE(5.0),
    /** Motocicleta com baú padrão (pedidos até 20kg). */
    MOTORCYCLE(20.0),
    /** Carro de passeio ou utilitário leve (pedidos até 80kg). */
    CAR(80.0),
    /** Furgão ou van de carga (pedidos pesados até 500kg). */
    VAN(500.0)
}

/**
 * Perfil operacional e telemétrico do entregador disponível para alocação.
 *
 * @property id Identificador exclusivo do motorista.
 * @property name Nome comercial do condutor.
 * @property latitude Latitude atual obtida via GPS.
 * @property longitude Longitude atual obtida via GPS.
 * @property vehicleType Categoria do veículo ([VehicleType]).
 * @property maxCapacityKg Capacidade máxima de carga do baú em quilogramas.
 * @property isAvailable Sinaliza se o condutor está livre para aceitar novas corridas.
 * @property rating Avaliação média do motorista (escala 0.0 a 5.0).
 */
data class DriverProfile(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val vehicleType: VehicleType,
    val maxCapacityKg: Double = vehicleType.defaultMaxCapacityKg,
    val isAvailable: Boolean = true,
    val rating: Double = 5.0
)

/**
 * Requisitos logísticos do pedido a ser despachado pela loja parceira.
 *
 * @property orderId Identificador único do pedido.
 * @property pickupLat Latitude da loja para coleta.
 * @property pickupLng Longitude da loja para coleta.
 * @property deliveryLat Latitude de destino da entrega.
 * @property deliveryLng Longitude de destino da entrega.
 * @property weightKg Peso total do pacote em quilogramas.
 */
data class PackageRequirements(
    val orderId: String,
    val pickupLat: Double,
    val pickupLng: Double,
    val deliveryLat: Double,
    val deliveryLng: Double,
    val weightKg: Double
)

/**
 * Resultado do emparelhamento inteligente entre motorista e solicitação de despacho.
 *
 * @property driver Perfil do motorista selecionado.
 * @property distanceToPickupMeters Distância em metros entre a posição atual do motorista e a loja de coleta.
 * @property matchScore Pontuação calculada de aderência (quanto maior, melhor a recomendação).
 */
data class DispatchMatch(
    val driver: DriverProfile,
    val distanceToPickupMeters: Double,
    val matchScore: Double
)

/**
 * Motor corporativo de Despacho Automatizado e Roteamento Inteligente de Entregadores.
 *
 * Executa algoritmo de matching ponderado considerando:
 * 1. Proximidade física da loja de coleta (menor tempo de espera).
 * 2. Capacidade volumétrica e de carga do veículo (compatibilidade com peso do pacote).
 * 3. Disponibilidade operacional e histórico de reputação (rating) do entregador.
 *
 * ### 📌 Quando Usar:
 * Utilize no painel do lojista ou orquestrador de logística (:feature:delivery:dispatch) para alocar
 * automaticamente os melhores entregadores assim que um pedido entra no status "Pronto para Retirada".
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val engine = AutomatedDispatchEngine()
 * val pkg = PackageRequirements("ord-10", -23.55, -46.63, -23.56, -46.64, weightKg = 8.0)
 * val drivers = listOf(driverMoto, driverBike, driverCar)
 *
 * val bestMatches = engine.findOptimalDrivers(pkg, drivers)
 * val topDriver = bestMatches.firstOrNull()?.driver
 * ```
 */
@Singleton
class AutomatedDispatchEngine @Inject constructor() {

    companion object {
        private const val EARTH_RADIUS_METERS = 6371000.0
    }

    /**
     * Encontra e classifica os motoristas mais recomendados para atender à remessa.
     *
     * @param pkg Requisitos de peso e localização do pacote.
     * @param candidates Lista de motoristas cadastrados.
     * @param maxResults Limite máximo de motoristas no ranking (padrão: 5).
     * @return Lista ordenada de [DispatchMatch] decrescente por [DispatchMatch.matchScore].
     */
    fun findOptimalDrivers(
        pkg: PackageRequirements,
        candidates: List<DriverProfile>,
        maxResults: Int = 5
    ): List<DispatchMatch> {
        return candidates
            .filter { it.isAvailable }
            .filter { it.maxCapacityKg >= pkg.weightKg }
            .map { driver ->
                val distance = calculateDistanceMeters(
                    lat1 = driver.latitude,
                    lon1 = driver.longitude,
                    lat2 = pkg.pickupLat,
                    lon2 = pkg.pickupLng
                )

                // Score ponderado: proximidade inversamente proporcional + fator de reputação (rating)
                val proximityWeight = 10000.0 / (distance + 100.0)
                val ratingMultiplier = driver.rating.coerceIn(1.0, 5.0) / 5.0
                val finalScore = proximityWeight * ratingMultiplier

                DispatchMatch(
                    driver = driver,
                    distanceToPickupMeters = distance,
                    matchScore = finalScore
                )
            }
            .sortedByDescending { it.matchScore }
            .take(maxResults)
    }

    private fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
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
