package br.com.wgc.factory.bundles.foundation.pro

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FoundationProBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("foundation", FoundationProBundle.DOMAIN)
        assertEquals("pro", FoundationProBundle.TIER)
        assertTrue(FoundationProBundle.features.isNotEmpty())
        assertTrue(FoundationProBundle.features.contains("settings"))
    }

    @Test
    fun testBundleInitialization() {
        FoundationProBundle.initialize(null)
    }
}
