package br.com.wgc.factory.bundles.delivery.basic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeliveryBasicBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("delivery", DeliveryBasicBundle.DOMAIN)
        assertEquals("basic", DeliveryBasicBundle.TIER)
        assertTrue(DeliveryBasicBundle.features.isNotEmpty())
        assertTrue(DeliveryBasicBundle.features.contains("order-tracking"))
    }

    @Test
    fun testBundleInitialization() {
        DeliveryBasicBundle.initialize(null)
    }
}
