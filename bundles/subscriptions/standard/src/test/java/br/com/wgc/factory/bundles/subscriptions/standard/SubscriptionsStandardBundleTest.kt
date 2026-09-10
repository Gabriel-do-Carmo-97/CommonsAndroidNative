package br.com.wgc.factory.bundles.subscriptions.standard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SubscriptionsStandardBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("subscriptions", SubscriptionsStandardBundle.DOMAIN)
        assertEquals("standard", SubscriptionsStandardBundle.TIER)
        assertTrue(SubscriptionsStandardBundle.features.isNotEmpty())
        assertTrue(SubscriptionsStandardBundle.features.contains("subscriptions"))
    }

    @Test
    fun testBundleInitialization() {
        SubscriptionsStandardBundle.initialize(null)
    }
}
