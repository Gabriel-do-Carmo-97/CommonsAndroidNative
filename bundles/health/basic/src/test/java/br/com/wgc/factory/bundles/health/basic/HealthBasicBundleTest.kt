package br.com.wgc.factory.bundles.health.basic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HealthBasicBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("health", HealthBasicBundle.DOMAIN)
        assertEquals("basic", HealthBasicBundle.TIER)
        assertTrue(HealthBasicBundle.features.isNotEmpty())
        assertTrue(HealthBasicBundle.features.contains("scheduling"))
    }

    @Test
    fun testBundleInitialization() {
        HealthBasicBundle.initialize(null)
    }
}
