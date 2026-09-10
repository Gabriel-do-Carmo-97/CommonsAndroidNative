package br.com.wgc.factory.bundles.events.pro

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EventsProBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("events", EventsProBundle.DOMAIN)
        assertEquals("pro", EventsProBundle.TIER)
        assertTrue(EventsProBundle.features.isNotEmpty())
        assertTrue(EventsProBundle.features.contains("catalog"))
    }

    @Test
    fun testBundleInitialization() {
        EventsProBundle.initialize(null)
    }
}
