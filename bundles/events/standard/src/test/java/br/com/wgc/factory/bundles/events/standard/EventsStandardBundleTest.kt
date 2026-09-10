package br.com.wgc.factory.bundles.events.standard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EventsStandardBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("events", EventsStandardBundle.DOMAIN)
        assertEquals("standard", EventsStandardBundle.TIER)
        assertTrue(EventsStandardBundle.features.isNotEmpty())
        assertTrue(EventsStandardBundle.features.contains("catalog"))
    }

    @Test
    fun testBundleInitialization() {
        EventsStandardBundle.initialize(null)
    }
}
