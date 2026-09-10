package br.com.wgc.factory.bundles.education.pro

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EducationProBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("education", EducationProBundle.DOMAIN)
        assertEquals("pro", EducationProBundle.TIER)
        assertTrue(EducationProBundle.features.isNotEmpty())
        assertTrue(EducationProBundle.features.contains("scheduling"))
    }

    @Test
    fun testBundleInitialization() {
        EducationProBundle.initialize(null)
    }
}
