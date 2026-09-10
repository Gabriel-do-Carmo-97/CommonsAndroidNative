package br.com.wgc.factory.bundles.realestate.pro

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RealEstateProBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("real-estate", RealEstateProBundle.DOMAIN)
        assertEquals("pro", RealEstateProBundle.TIER)
        assertTrue(RealEstateProBundle.features.isNotEmpty())
        assertTrue(RealEstateProBundle.features.contains("catalog"))
    }

    @Test
    fun testBundleInitialization() {
        RealEstateProBundle.initialize(null)
    }
}
