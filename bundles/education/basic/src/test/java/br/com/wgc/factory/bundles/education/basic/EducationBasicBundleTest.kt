package br.com.wgc.factory.bundles.education.basic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EducationBasicBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("education", EducationBasicBundle.DOMAIN)
        assertEquals("basic", EducationBasicBundle.TIER)
        assertTrue(EducationBasicBundle.features.isNotEmpty())
        assertTrue(EducationBasicBundle.features.contains("scheduling"))
    }

    @Test
    fun testBundleInitialization() {
        EducationBasicBundle.initialize(null)
    }
}
