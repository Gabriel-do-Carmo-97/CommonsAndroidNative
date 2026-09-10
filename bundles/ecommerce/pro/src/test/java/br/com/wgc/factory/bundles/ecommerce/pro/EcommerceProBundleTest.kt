package br.com.wgc.factory.bundles.ecommerce.pro

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EcommerceProBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("ecommerce", EcommerceProBundle.DOMAIN)
        assertEquals("pro", EcommerceProBundle.TIER)
        assertTrue(EcommerceProBundle.features.isNotEmpty())
        assertTrue(EcommerceProBundle.features.contains("catalog"))
    }

    @Test
    fun testBundleInitialization() {
        EcommerceProBundle.initialize(null)
    }
}
