package br.com.wgc.factory.bundles.subscriptions.basic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SubscriptionsBasicBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("subscriptions", SubscriptionsBasicBundle.DOMAIN)
        assertEquals("basic", SubscriptionsBasicBundle.TIER)
        assertTrue(SubscriptionsBasicBundle.features.isNotEmpty())
        assertTrue(SubscriptionsBasicBundle.features.contains("subscriptions"))
    }

    @Test
    fun testBundleInitialization() {
        SubscriptionsBasicBundle.initialize(null)
    }
}
