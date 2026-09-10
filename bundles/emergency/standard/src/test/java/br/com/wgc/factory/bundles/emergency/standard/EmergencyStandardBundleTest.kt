package br.com.wgc.factory.bundles.emergency.standard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EmergencyStandardBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("emergency", EmergencyStandardBundle.DOMAIN)
        assertEquals("standard", EmergencyStandardBundle.TIER)
        assertTrue(EmergencyStandardBundle.features.isNotEmpty())
        assertTrue(EmergencyStandardBundle.features.contains("emergency"))
    }

    @Test
    fun testBundleInitialization() {
        EmergencyStandardBundle.initialize(null)
    }
}
