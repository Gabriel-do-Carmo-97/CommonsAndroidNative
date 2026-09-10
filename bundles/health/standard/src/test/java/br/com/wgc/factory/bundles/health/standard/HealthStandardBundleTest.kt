package br.com.wgc.factory.bundles.health.standard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HealthStandardBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("health", HealthStandardBundle.DOMAIN)
        assertEquals("standard", HealthStandardBundle.TIER)
        assertTrue(HealthStandardBundle.features.isNotEmpty())
        assertTrue(HealthStandardBundle.features.contains("scheduling"))
    }

    @Test
    fun testBundleInitialization() {
        HealthStandardBundle.initialize(null)
    }
}
