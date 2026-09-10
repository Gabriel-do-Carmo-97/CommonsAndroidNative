package br.com.wgc.factory.bundles.communication.pro

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CommunicationProBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("communication", CommunicationProBundle.DOMAIN)
        assertEquals("pro", CommunicationProBundle.TIER)
        assertTrue(CommunicationProBundle.features.isNotEmpty())
        assertTrue(CommunicationProBundle.features.contains("message"))
    }

    @Test
    fun testBundleInitialization() {
        CommunicationProBundle.initialize(null)
    }
}
