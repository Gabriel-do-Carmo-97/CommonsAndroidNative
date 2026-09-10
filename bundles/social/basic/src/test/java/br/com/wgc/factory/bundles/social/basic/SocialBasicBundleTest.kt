package br.com.wgc.factory.bundles.social.basic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SocialBasicBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("social", SocialBasicBundle.DOMAIN)
        assertEquals("basic", SocialBasicBundle.TIER)
        assertTrue(SocialBasicBundle.features.isNotEmpty())
        assertTrue(SocialBasicBundle.features.contains("profile"))
    }

    @Test
    fun testBundleInitialization() {
        SocialBasicBundle.initialize(null)
    }
}
