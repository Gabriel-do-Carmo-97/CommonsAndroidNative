package br.com.wgc.factory.bundles.realestate.basic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RealEstateBasicBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("real-estate", RealEstateBasicBundle.DOMAIN)
        assertEquals("basic", RealEstateBasicBundle.TIER)
        assertTrue(RealEstateBasicBundle.features.isNotEmpty())
        assertTrue(RealEstateBasicBundle.features.contains("catalog"))
    }

    @Test
    fun testBundleInitialization() {
        RealEstateBasicBundle.initialize(null)
    }
}
