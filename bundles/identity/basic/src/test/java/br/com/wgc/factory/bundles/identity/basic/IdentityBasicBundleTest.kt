package br.com.wgc.factory.bundles.identity.basic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IdentityBasicBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("identity", IdentityBasicBundle.DOMAIN)
        assertEquals("basic", IdentityBasicBundle.TIER)
        assertTrue(IdentityBasicBundle.features.isNotEmpty())
        assertTrue(IdentityBasicBundle.features.contains("authentication"))
    }

    @Test
    fun testBundleInitialization() {
        IdentityBasicBundle.initialize(null)
    }
}
