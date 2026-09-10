package br.com.wgc.factory.bundles.social.pro

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SocialProBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("social", SocialProBundle.DOMAIN)
        assertEquals("pro", SocialProBundle.TIER)
        assertTrue(SocialProBundle.features.isNotEmpty())
        assertTrue(SocialProBundle.features.contains("profile"))
    }

    @Test
    fun testBundleInitialization() {
        SocialProBundle.initialize(null)
    }
}
