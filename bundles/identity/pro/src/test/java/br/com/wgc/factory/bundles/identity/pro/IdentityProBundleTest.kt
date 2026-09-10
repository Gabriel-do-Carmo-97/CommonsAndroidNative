package br.com.wgc.factory.bundles.identity.pro

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IdentityProBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("identity", IdentityProBundle.DOMAIN)
        assertEquals("pro", IdentityProBundle.TIER)
        assertTrue(IdentityProBundle.features.isNotEmpty())
        assertTrue(IdentityProBundle.features.contains("authentication"))
    }

    @Test
    fun testBundleInitialization() {
        IdentityProBundle.initialize(null)
    }
}
