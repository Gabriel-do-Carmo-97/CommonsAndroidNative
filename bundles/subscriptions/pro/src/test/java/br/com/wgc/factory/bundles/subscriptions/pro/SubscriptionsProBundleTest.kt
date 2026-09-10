package br.com.wgc.factory.bundles.subscriptions.pro

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SubscriptionsProBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("subscriptions", SubscriptionsProBundle.DOMAIN)
        assertEquals("pro", SubscriptionsProBundle.TIER)
        assertTrue(SubscriptionsProBundle.features.isNotEmpty())
        assertTrue(SubscriptionsProBundle.features.contains("subscriptions"))
    }

    @Test
    fun testBundleInitialization() {
        SubscriptionsProBundle.initialize(null)
    }
}
