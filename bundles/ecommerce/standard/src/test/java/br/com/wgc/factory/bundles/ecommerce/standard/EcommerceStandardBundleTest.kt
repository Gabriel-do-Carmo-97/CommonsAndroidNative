package br.com.wgc.factory.bundles.ecommerce.standard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EcommerceStandardBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("ecommerce", EcommerceStandardBundle.DOMAIN)
        assertEquals("standard", EcommerceStandardBundle.TIER)
        assertTrue(EcommerceStandardBundle.features.isNotEmpty())
        assertTrue(EcommerceStandardBundle.features.contains("catalog"))
    }

    @Test
    fun testBundleInitialization() {
        EcommerceStandardBundle.initialize(null)
    }
}
