package br.com.wgc.subscriptions.catalog

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [SubscriptionPlanCatalogManager] validando matriz de planos, descontos e identificação do plano mais popular.
 */
class SubscriptionPlanCatalogManagerTest {

    private lateinit var catalogManager: SubscriptionPlanCatalogManager

    @Before
    fun setup() {
        catalogManager = SubscriptionPlanCatalogManager()
    }

    @Test
    fun `getAvailablePlans generates 3 tiers with annual plan marked as most popular and highest discount`() {
        val plans = catalogManager.getAvailablePlans(baseMonthlyPrice = 50.0)

        assertEquals(3, plans.size)

        val monthly = plans.first { it.billingCycle == BillingCycle.MONTHLY }
        assertEquals(50.0, monthly.totalBillingPrice, 0.01)
        assertEquals(0, monthly.discountPercentage)
        assertFalse(monthly.isMostPopular)

        val quarterly = plans.first { it.billingCycle == BillingCycle.QUARTERLY }
        // 50 * 3 * 0.9 = 135.0
        assertEquals(135.0, quarterly.totalBillingPrice, 0.01)
        assertEquals(10, quarterly.discountPercentage)

        val annual = plans.first { it.billingCycle == BillingCycle.ANNUAL }
        // 50 * 12 * 0.75 = 450.0
        assertEquals(450.0, annual.totalBillingPrice, 0.01)
        assertEquals(25, annual.discountPercentage)
        assertTrue(annual.isMostPopular)
    }

    @Test
    fun `calculateSavingsPercentage calculates exact percentage`() {
        val savings = catalogManager.calculateSavingsPercentage(
            fullRegularPrice = 600.0,
            discountedPrice = 450.0
        )
        assertEquals(25, savings)

        val zeroSavings = catalogManager.calculateSavingsPercentage(100.0, 100.0)
        assertEquals(0, zeroSavings)
    }
}
