package br.com.wgc.factory.bundles.finance.standard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FinanceStandardBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("finance", FinanceStandardBundle.DOMAIN)
        assertEquals("standard", FinanceStandardBundle.TIER)
        assertTrue(FinanceStandardBundle.features.isNotEmpty())
        assertTrue(FinanceStandardBundle.features.contains("payment"))
    }

    @Test
    fun testBundleInitialization() {
        FinanceStandardBundle.initialize(null)
    }
}
