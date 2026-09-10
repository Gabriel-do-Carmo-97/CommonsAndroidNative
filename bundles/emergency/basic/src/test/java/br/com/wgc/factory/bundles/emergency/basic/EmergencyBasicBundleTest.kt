package br.com.wgc.factory.bundles.emergency.basic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EmergencyBasicBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("emergency", EmergencyBasicBundle.DOMAIN)
        assertEquals("basic", EmergencyBasicBundle.TIER)
        assertTrue(EmergencyBasicBundle.features.isNotEmpty())
        assertTrue(EmergencyBasicBundle.features.contains("emergency"))
    }

    @Test
    fun testBundleInitialization() {
        EmergencyBasicBundle.initialize(null)
    }
}
