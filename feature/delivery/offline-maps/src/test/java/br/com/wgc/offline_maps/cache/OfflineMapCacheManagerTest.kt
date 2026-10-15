package br.com.wgc.offline_maps.cache

import br.com.wgc.core.sharedPreferences.InMemoryKeyValueStorage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [OfflineMapCacheManager] validando persistência de tiles,
 * recuperação de rotas e métricas estatísticas.
 */
class OfflineMapCacheManagerTest {

    private lateinit var storage: InMemoryKeyValueStorage
    private lateinit var cacheManager: OfflineMapCacheManager

    @Before
    fun setup() {
        storage = InMemoryKeyValueStorage()
        cacheManager = OfflineMapCacheManager(storage)
    }

    @Test
    fun `cacheTile correctly stores tile and reports isTileCached true`() {
        val tile = OfflineMapTile(
            tileId = "14/8200/5400",
            zoomLevel = 14,
            x = 8200,
            y = 5400,
            payloadBytesSize = 12048
        )

        assertFalse(cacheManager.isTileCached(14, 8200, 5400))
        cacheManager.cacheTile(tile)
        assertTrue(cacheManager.isTileCached(14, 8200, 5400))
    }

    @Test
    fun `cacheRoute and findMatchingRoute retrieves route within tolerance`() {
        val route = CachedRoute(
            routeId = "r-1",
            originLat = -23.5500,
            originLng = -46.6300,
            destinationLat = -23.5600,
            destinationLng = -46.6400,
            waypoints = listOf(-23.5500 to -46.6300, -23.5600 to -46.6400),
            distanceMeters = 2500.0,
            durationSeconds = 300
        )

        cacheManager.cacheRoute(route)

        // Coordinates very close to origin & destination
        val matched = cacheManager.findMatchingRoute(
            originLat = -23.5502,
            originLng = -46.6301,
            destLat = -23.5601,
            destLng = -46.6399,
            toleranceDegrees = 0.001
        )

        assertNotNull(matched)
        assertEquals("r-1", matched?.routeId)
        assertEquals(2, matched?.waypoints?.size)

        // Far away coordinates should return null
        val notMatched = cacheManager.findMatchingRoute(
            originLat = -23.8000,
            originLng = -46.8000,
            destLat = -23.5600,
            destLng = -46.6400
        )
        assertNull(notMatched)
    }

    @Test
    fun `evictExpiredTiles removes tiles older than maxAge`() {
        val oldTile = OfflineMapTile(
            tileId = "10/1/1",
            zoomLevel = 10,
            x = 1,
            y = 1,
            payloadBytesSize = 500,
            cachedAt = System.currentTimeMillis() - 100000L
        )
        val freshTile = OfflineMapTile(
            tileId = "10/2/2",
            zoomLevel = 10,
            x = 2,
            y = 2,
            payloadBytesSize = 500,
            cachedAt = System.currentTimeMillis()
        )

        cacheManager.cacheTile(oldTile)
        cacheManager.cacheTile(freshTile)

        assertEquals(2, cacheManager.getCacheStats().totalTiles)

        cacheManager.evictExpiredTiles(maxAgeMillis = 50000L)

        val stats = cacheManager.getCacheStats()
        assertEquals(1, stats.totalTiles)
        assertFalse(cacheManager.isTileCached(10, 1, 1))
        assertTrue(cacheManager.isTileCached(10, 2, 2))
    }
}
