package br.com.wgc.factory.bundles.finance.pro

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FinanceProBundleTest {

    @Test
    fun testBundleMetadata() {
        assertEquals("finance", FinanceProBundle.DOMAIN)
        assertEquals("pro", FinanceProBundle.TIER)
        assertTrue(FinanceProBundle.features.isNotEmpty())
        assertTrue(FinanceProBundle.features.contains("payment"))
    }

    @Test
    fun testBundleInitialization() {
        FinanceProBundle.initialize(null)
    }
}
