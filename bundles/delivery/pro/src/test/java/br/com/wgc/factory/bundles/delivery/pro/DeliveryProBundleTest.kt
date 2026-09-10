package br.com.wgc.factory.bundles.delivery.pro

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeliveryProBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("delivery", DeliveryProBundle.DOMAIN)
        assertEquals("pro", DeliveryProBundle.TIER)
        assertTrue(DeliveryProBundle.features.isNotEmpty())
        assertTrue(DeliveryProBundle.features.contains("order-tracking"))
    }

    @Test
    fun testBundleInitialization() {
        DeliveryProBundle.initialize(null)
    }
}
