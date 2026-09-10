package br.com.wgc.factory.bundles.delivery.standard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeliveryStandardBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("delivery", DeliveryStandardBundle.DOMAIN)
        assertEquals("standard", DeliveryStandardBundle.TIER)
        assertTrue(DeliveryStandardBundle.features.isNotEmpty())
        assertTrue(DeliveryStandardBundle.features.contains("order-tracking"))
    }

    @Test
    fun testBundleInitialization() {
        DeliveryStandardBundle.initialize(null)
    }
}
