package br.com.wgc.factory.bundles.foundation.basic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FoundationBasicBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("foundation", FoundationBasicBundle.DOMAIN)
        assertEquals("basic", FoundationBasicBundle.TIER)
        assertTrue(FoundationBasicBundle.features.isNotEmpty())
        assertTrue(FoundationBasicBundle.features.contains("settings"))
    }

    @Test
    fun testBundleInitialization() {
        FoundationBasicBundle.initialize(null)
    }
}
