package br.com.wgc.factory.bundles.social.standard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SocialStandardBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("social", SocialStandardBundle.DOMAIN)
        assertEquals("standard", SocialStandardBundle.TIER)
        assertTrue(SocialStandardBundle.features.isNotEmpty())
        assertTrue(SocialStandardBundle.features.contains("profile"))
    }

    @Test
    fun testBundleInitialization() {
        SocialStandardBundle.initialize(null)
    }
}
