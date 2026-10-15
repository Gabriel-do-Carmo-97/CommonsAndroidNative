package br.com.wgc.offline_maps.cache

import br.com.wgc.core.sharedPreferences.KeyValueStorage
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

/**
 * Representação de um tile de mapa vetorial/raster preservado em cache local.
 *
 * @property tileId Identificador composto do tile (ex: "15/18721/12304").
 * @property zoomLevel Nível de zoom cartográfico.
 * @property x Índice X do tile na projeção Web Mercator.
 * @property y Índice Y do tile na projeção Web Mercator.
 * @property payloadBytesSize Tamanho em bytes do arquivo do tile no armazenamento persistente.
 * @property cachedAt Carimbo de data/hora de salvamento em milissegundos.
 */
data class OfflineMapTile(
    val tileId: String,
    val zoomLevel: Int,
    val x: Int,
    val y: Int,
    val payloadBytesSize: Int,
    val cachedAt: Long = System.currentTimeMillis()
)

/**
 * Trajeto e rota de navegação completa preservada para consulta e orientação curva-a-curva sem sinal de celular.
 *
 * @property routeId Identificador único da rota calculada.
 * @property originLat Latitude do ponto de partida.
 * @property originLng Longitude do ponto de partida.
 * @property destinationLat Latitude do ponto de chegada.
 * @property destinationLng Longitude do ponto de chegada.
 * @property waypoints Lista de pares (latitude, longitude) compondo a polyline navegável.
 * @property distanceMeters Distância viária total do percurso em metros.
 * @property durationSeconds Duração estimada da viagem em segundos.
 * @property cachedAt Carimbo de gravação em milissegundos.
 */
data class CachedRoute(
    val routeId: String,
    val originLat: Double,
    val originLng: Double,
    val destinationLat: Double,
    val destinationLng: Double,
    val waypoints: List<Pair<Double, Double>>,
    val distanceMeters: Double,
    val durationSeconds: Int,
    val cachedAt: Long = System.currentTimeMillis()
)

/**
 * Estatísticas volumétricas do cache de mapas offline.
 *
 * @property totalTiles Quantidade de tiles armazenados localmente.
 * @property totalRoutes Quantidade de rotas completas preservadas.
 * @property totalSizeBytes Tamanho consolidado ocupado no armazenamento do dispositivo em bytes.
 */
data class OfflineMapCacheStats(
    val totalTiles: Int,
    val totalRoutes: Int,
    val totalSizeBytes: Long
)

/**
 * Gerenciador corporativo de Cache Offline de Tiles e Rotas Cartográficas de Entrega.
 *
 * Armazena localmente tiles vetoriais e polylines de rotas recentes, permitindo que motoristas e entregadores
 * mantenham a navegação ativa mesmo ao entrar em garagens subterrâneas, túneis ou áreas rurais sem conectividade 4G/5G.
 *
 * ### 📌 Quando Usar:
 * Utilize na inicialização do serviço de entrega ou no [br.com.wgc.offline_maps.screen.OfflineMapsViewModel]
 * para pré-carregar a rota antes da partida e consultar trajetos cacheados em modo offline.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val cacheManager = OfflineMapCacheManager(storage)
 *
 * // 1. Salvar rota completa com pontos de navegação:
 * cacheManager.cacheRoute(
 *     CachedRoute(
 *         routeId = "route-123",
 *         originLat = -23.55, originLng = -46.63,
 *         destinationLat = -23.56, destinationLng = -46.65,
 *         waypoints = listOf(-23.55 to -46.63, -23.56 to -46.65),
 *         distanceMeters = 3200.0,
 *         durationSeconds = 480
 *     )
 * )
 *
 * // 2. Recuperar rota offline por proximidade de partida e destino:
 * val offlineRoute = cacheManager.findMatchingRoute(-23.5501, -46.6301, -23.5601, -46.6501)
 * ```
 *
 * @param storage Implementação de persistência [KeyValueStorage] para serialização dos metadados.
 */
@Singleton
class OfflineMapCacheManager @Inject constructor(
    private val storage: KeyValueStorage
) {
    companion object {
        private const val KEY_TILES = "wgc_offline_map_tiles"
        private const val KEY_ROUTES = "wgc_offline_map_routes"
        private const val DELIMITER_RECORD = "###"
        private const val DELIMITER_FIELD = "@@@"
        private const val DELIMITER_POINT = ":::"
    }

    private val cachedTiles = mutableMapOf<String, OfflineMapTile>()
    private val cachedRoutes = mutableListOf<CachedRoute>()

    init {
        loadFromStorage()
    }

    /**
     * Armazena um tile cartográfico no cache local em memória e persistência.
     *
     * @param tile Objeto [OfflineMapTile] a ser preservado.
     */
    fun cacheTile(tile: OfflineMapTile) {
        cachedTiles[tile.tileId] = tile
        persistTiles()
    }

    /**
     * Verifica se um tile específico já se encontra disponível no cache local.
     *
     * @param zoom Nível de zoom.
     * @param x Coordenada X.
     * @param y Coordenada Y.
     * @return true se o tile estiver disponível offline.
     */
    fun isTileCached(zoom: Int, x: Int, y: Int): Boolean {
        val tileId = "$zoom/$x/$y"
        return cachedTiles.containsKey(tileId)
    }

    /**
     * Salva uma rota calculada com polyline e metadados no repositório offline.
     *
     * @param route Dados completos da rota calculada.
     */
    fun cacheRoute(route: CachedRoute) {
        cachedRoutes.removeAll { it.routeId == route.routeId }
        cachedRoutes.add(0, route)
        persistRoutes()
    }

    /**
     * Localiza uma rota em cache cujos pontos de origem e destino estejam dentro da tolerância geográfica informada.
     *
     * @param originLat Latitude da partida.
     * @param originLng Longitude da partida.
     * @param destLat Latitude do destino.
     * @param destLng Longitude do destino.
     * @param toleranceDegrees Tolerância máxima em graus decimais (padrão ~100 metros: 0.001°).
     * @return [CachedRoute] correspondente ou null se nenhuma rota offline for compatível.
     */
    fun findMatchingRoute(
        originLat: Double,
        originLng: Double,
        destLat: Double,
        destLng: Double,
        toleranceDegrees: Double = 0.001
    ): CachedRoute? {
        return cachedRoutes.find { route ->
            abs(route.originLat - originLat) <= toleranceDegrees &&
            abs(route.originLng - originLng) <= toleranceDegrees &&
            abs(route.destinationLat - destLat) <= toleranceDegrees &&
            abs(route.destinationLng - destLng) <= toleranceDegrees
        }
    }

    /**
     * Obtém as métricas de volume e uso de espaço do cache offline.
     *
     * @return [OfflineMapCacheStats] consolidado.
     */
    fun getCacheStats(): OfflineMapCacheStats {
        val totalTilesBytes = cachedTiles.values.sumOf { it.payloadBytesSize.toLong() }
        val totalRoutesBytes = cachedRoutes.size * 2048L // Estimativa média por rota com waypoints
        return OfflineMapCacheStats(
            totalTiles = cachedTiles.size,
            totalRoutes = cachedRoutes.size,
            totalSizeBytes = totalTilesBytes + totalRoutesBytes
        )
    }

    /**
     * Remove tiles armazenados há mais tempo do que a vida útil máxima permitida (TTL).
     *
     * @param maxAgeMillis Tempo limite em milissegundos antes da expiração.
     */
    fun evictExpiredTiles(maxAgeMillis: Long) {
        val now = System.currentTimeMillis()
        val expiredIds = cachedTiles.values
            .filter { (now - it.cachedAt) > maxAgeMillis }
            .map { it.tileId }

        expiredIds.forEach { cachedTiles.remove(it) }
        if (expiredIds.isNotEmpty()) {
            persistTiles()
        }
    }

    /**
     * Limpa integralmente todos os tiles e rotas cacheados.
     */
    fun clearCache() {
        cachedTiles.clear()
        cachedRoutes.clear()
        storage.remove(KEY_TILES)
        storage.remove(KEY_ROUTES)
    }

    private fun persistTiles() {
        val serialized = cachedTiles.values.joinToString(DELIMITER_RECORD) { tile ->
            "${tile.tileId}$DELIMITER_FIELD${tile.zoomLevel}$DELIMITER_FIELD${tile.x}$DELIMITER_FIELD${tile.y}$DELIMITER_FIELD${tile.payloadBytesSize}$DELIMITER_FIELD${tile.cachedAt}"
        }
        storage.saveString(KEY_TILES, serialized)
    }

    private fun persistRoutes() {
        val serialized = cachedRoutes.joinToString(DELIMITER_RECORD) { route ->
            val waypointsStr = route.waypoints.joinToString(",") { "${it.first}$DELIMITER_POINT${it.second}" }
            "${route.routeId}$DELIMITER_FIELD${route.originLat}$DELIMITER_FIELD${route.originLng}$DELIMITER_FIELD${route.destinationLat}$DELIMITER_FIELD${route.destinationLng}$DELIMITER_FIELD${route.distanceMeters}$DELIMITER_FIELD${route.durationSeconds}$DELIMITER_FIELD${route.cachedAt}$DELIMITER_FIELD$waypointsStr"
        }
        storage.saveString(KEY_ROUTES, serialized)
    }

    private fun loadFromStorage() {
        val rawTiles = storage.getString(KEY_TILES, "") ?: ""
        if (rawTiles.isNotBlank()) {
            rawTiles.split(DELIMITER_RECORD).forEach { record ->
                val f = record.split(DELIMITER_FIELD)
                if (f.size >= 6) {
                    val tile = OfflineMapTile(
                        tileId = f[0],
                        zoomLevel = f[1].toIntOrNull() ?: 0,
                        x = f[2].toIntOrNull() ?: 0,
                        y = f[3].toIntOrNull() ?: 0,
                        payloadBytesSize = f[4].toIntOrNull() ?: 0,
                        cachedAt = f[5].toLongOrNull() ?: 0L
                    )
                    cachedTiles[tile.tileId] = tile
                }
            }
        }

        val rawRoutes = storage.getString(KEY_ROUTES, "") ?: ""
        if (rawRoutes.isNotBlank()) {
            rawRoutes.split(DELIMITER_RECORD).forEach { record ->
                val f = record.split(DELIMITER_FIELD)
                if (f.size >= 9) {
                    val waypoints = f[8].split(",").mapNotNull { p ->
                        val coords = p.split(DELIMITER_POINT)
                        if (coords.size >= 2) {
                            val lat = coords[0].toDoubleOrNull()
                            val lng = coords[1].toDoubleOrNull()
                            if (lat != null && lng != null) lat to lng else null
                        } else null
                    }

                    val route = CachedRoute(
                        routeId = f[0],
                        originLat = f[1].toDoubleOrNull() ?: 0.0,
                        originLng = f[2].toDoubleOrNull() ?: 0.0,
                        destinationLat = f[3].toDoubleOrNull() ?: 0.0,
                        destinationLng = f[4].toDoubleOrNull() ?: 0.0,
                        distanceMeters = f[5].toDoubleOrNull() ?: 0.0,
                        durationSeconds = f[6].toIntOrNull() ?: 0,
                        cachedAt = f[7].toLongOrNull() ?: 0L,
                        waypoints = waypoints
                    )
                    cachedRoutes.add(route)
                }
            }
        }
    }
}
