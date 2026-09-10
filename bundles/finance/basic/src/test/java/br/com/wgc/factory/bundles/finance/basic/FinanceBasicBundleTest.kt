package br.com.wgc.factory.bundles.finance.basic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FinanceBasicBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("finance", FinanceBasicBundle.DOMAIN)
        assertEquals("basic", FinanceBasicBundle.TIER)
        assertTrue(FinanceBasicBundle.features.isNotEmpty())
        assertTrue(FinanceBasicBundle.features.contains("payment"))
    }

    @Test
    fun testBundleInitialization() {
        FinanceBasicBundle.initialize(null)
    }
}
