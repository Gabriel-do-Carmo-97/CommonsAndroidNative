package br.com.wgc.factory.bundles.realestate.standard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RealEstateStandardBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("real-estate", RealEstateStandardBundle.DOMAIN)
        assertEquals("standard", RealEstateStandardBundle.TIER)
        assertTrue(RealEstateStandardBundle.features.isNotEmpty())
        assertTrue(RealEstateStandardBundle.features.contains("catalog"))
    }

    @Test
    fun testBundleInitialization() {
        RealEstateStandardBundle.initialize(null)
    }
}
