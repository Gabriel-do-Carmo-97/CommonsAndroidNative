package br.com.wgc.factory.bundles.services.basic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ServicesBasicBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("services", ServicesBasicBundle.DOMAIN)
        assertEquals("basic", ServicesBasicBundle.TIER)
        assertTrue(ServicesBasicBundle.features.isNotEmpty())
        assertTrue(ServicesBasicBundle.features.contains("scheduling"))
    }

    @Test
    fun testBundleInitialization() {
        ServicesBasicBundle.initialize(null)
    }
}
