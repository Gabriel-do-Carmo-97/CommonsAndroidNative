package br.com.wgc.factory.bundles.health.pro

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HealthProBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("health", HealthProBundle.DOMAIN)
        assertEquals("pro", HealthProBundle.TIER)
        assertTrue(HealthProBundle.features.isNotEmpty())
        assertTrue(HealthProBundle.features.contains("scheduling"))
    }

    @Test
    fun testBundleInitialization() {
        HealthProBundle.initialize(null)
    }
}
