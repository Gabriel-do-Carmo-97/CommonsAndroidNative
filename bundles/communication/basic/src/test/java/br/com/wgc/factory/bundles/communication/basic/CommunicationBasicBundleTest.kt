package br.com.wgc.factory.bundles.communication.basic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CommunicationBasicBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("communication", CommunicationBasicBundle.DOMAIN)
        assertEquals("basic", CommunicationBasicBundle.TIER)
        assertTrue(CommunicationBasicBundle.features.isNotEmpty())
        assertTrue(CommunicationBasicBundle.features.contains("message"))
    }

    @Test
    fun testBundleInitialization() {
        CommunicationBasicBundle.initialize(null)
    }
}
