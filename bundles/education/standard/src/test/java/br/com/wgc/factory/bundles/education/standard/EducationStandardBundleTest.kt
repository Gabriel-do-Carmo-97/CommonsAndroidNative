package br.com.wgc.factory.bundles.education.standard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EducationStandardBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("education", EducationStandardBundle.DOMAIN)
        assertEquals("standard", EducationStandardBundle.TIER)
        assertTrue(EducationStandardBundle.features.isNotEmpty())
        assertTrue(EducationStandardBundle.features.contains("scheduling"))
    }

    @Test
    fun testBundleInitialization() {
        EducationStandardBundle.initialize(null)
    }
}
