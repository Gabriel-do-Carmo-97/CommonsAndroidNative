package br.com.wgc.factory.bundles.communication.standard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CommunicationStandardBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("communication", CommunicationStandardBundle.DOMAIN)
        assertEquals("standard", CommunicationStandardBundle.TIER)
        assertTrue(CommunicationStandardBundle.features.isNotEmpty())
        assertTrue(CommunicationStandardBundle.features.contains("message"))
    }

    @Test
    fun testBundleInitialization() {
        CommunicationStandardBundle.initialize(null)
    }
}
