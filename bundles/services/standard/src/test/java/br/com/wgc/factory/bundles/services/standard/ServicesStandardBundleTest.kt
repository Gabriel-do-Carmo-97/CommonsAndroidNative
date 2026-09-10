package br.com.wgc.factory.bundles.services.standard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ServicesStandardBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("services", ServicesStandardBundle.DOMAIN)
        assertEquals("standard", ServicesStandardBundle.TIER)
        assertTrue(ServicesStandardBundle.features.isNotEmpty())
        assertTrue(ServicesStandardBundle.features.contains("scheduling"))
    }

    @Test
    fun testBundleInitialization() {
        ServicesStandardBundle.initialize(null)
    }
}
