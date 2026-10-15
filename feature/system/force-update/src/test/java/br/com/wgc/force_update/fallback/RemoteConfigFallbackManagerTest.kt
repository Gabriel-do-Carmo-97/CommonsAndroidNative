package br.com.wgc.force_update.fallback

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [RemoteConfigFallbackManager].
 */
class RemoteConfigFallbackManagerTest {

    private lateinit var fallbackManager: RemoteConfigFallbackManager

    @Before
    fun setup() {
        fallbackManager = RemoteConfigFallbackManager()
    }

    @Test
    fun `parseFallbackJson com json nulo deve retornar configuracao padrao`() {
        val config = fallbackManager.parseFallbackJson(null)

        assertEquals(1L, config.minRequiredAppVersion)
        assertFalse(config.forceUpdateEnabled)
        assertEquals(15, config.offlineSyncIntervalMinutes)
        assertFalse(config.isMaintenanceMode)
    }

    @Test
    fun `parseFallbackJson com json valido deve extrair parametros corretamente`() {
        val customJson = """
        {
          "min_required_app_version": 25,
          "force_update_enabled": true,
          "store_url": "https://custom.play.google.com",
          "offline_sync_interval_minutes": 30,
          "maintenance_mode": true
        }
        """

        val config = fallbackManager.parseFallbackJson(customJson)

        assertEquals(25L, config.minRequiredAppVersion)
        assertTrue(config.forceUpdateEnabled)
        assertEquals("https://custom.play.google.com", config.storeUrl)
        assertEquals(30, config.offlineSyncIntervalMinutes)
        assertTrue(config.isMaintenanceMode)
    }

    @Test
    fun `parseFallbackJson com json corrompido deve retornar fallback seguro`() {
        val malformedJson = "{ min_required_app_version: invalid }"

        val config = fallbackManager.parseFallbackJson(malformedJson)

        assertEquals(1L, config.minRequiredAppVersion)
        assertFalse(config.forceUpdateEnabled)
    }
}
