package br.com.wgc.factory.bundles.emergency.pro

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EmergencyProBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("emergency", EmergencyProBundle.DOMAIN)
        assertEquals("pro", EmergencyProBundle.TIER)
        assertTrue(EmergencyProBundle.features.isNotEmpty())
        assertTrue(EmergencyProBundle.features.contains("emergency"))
    }

    @Test
    fun testBundleInitialization() {
        EmergencyProBundle.initialize(null)
    }
}
