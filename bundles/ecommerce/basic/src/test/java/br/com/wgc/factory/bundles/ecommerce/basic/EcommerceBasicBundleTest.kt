package br.com.wgc.factory.bundles.ecommerce.basic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EcommerceBasicBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("ecommerce", EcommerceBasicBundle.DOMAIN)
        assertEquals("basic", EcommerceBasicBundle.TIER)
        assertTrue(EcommerceBasicBundle.features.isNotEmpty())
        assertTrue(EcommerceBasicBundle.features.contains("catalog"))
    }

    @Test
    fun testBundleInitialization() {
        EcommerceBasicBundle.initialize(null)
    }
}
