package br.com.wgc.factory.bundles.events.basic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EventsBasicBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("events", EventsBasicBundle.DOMAIN)
        assertEquals("basic", EventsBasicBundle.TIER)
        assertTrue(EventsBasicBundle.features.isNotEmpty())
        assertTrue(EventsBasicBundle.features.contains("catalog"))
    }

    @Test
    fun testBundleInitialization() {
        EventsBasicBundle.initialize(null)
    }
}
