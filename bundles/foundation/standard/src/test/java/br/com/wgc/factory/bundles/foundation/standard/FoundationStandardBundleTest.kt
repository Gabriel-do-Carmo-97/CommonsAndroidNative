package br.com.wgc.factory.bundles.foundation.standard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FoundationStandardBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("foundation", FoundationStandardBundle.DOMAIN)
        assertEquals("standard", FoundationStandardBundle.TIER)
        assertTrue(FoundationStandardBundle.features.isNotEmpty())
        assertTrue(FoundationStandardBundle.features.contains("settings"))
    }

    @Test
    fun testBundleInitialization() {
        FoundationStandardBundle.initialize(null)
    }
}
