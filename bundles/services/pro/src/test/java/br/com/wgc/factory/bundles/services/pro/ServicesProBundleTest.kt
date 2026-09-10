package br.com.wgc.factory.bundles.services.pro

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ServicesProBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("services", ServicesProBundle.DOMAIN)
        assertEquals("pro", ServicesProBundle.TIER)
        assertTrue(ServicesProBundle.features.isNotEmpty())
        assertTrue(ServicesProBundle.features.contains("scheduling"))
    }

    @Test
    fun testBundleInitialization() {
        ServicesProBundle.initialize(null)
    }
}
