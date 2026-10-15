package br.com.wgc.subscriptions.prorata

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [SubscriptionProrataCalculator] validando cálculo de créditos proporcionais e valor líquido.
 */
class SubscriptionProrataCalculatorTest {

    private lateinit var calculator: SubscriptionProrataCalculator

    @Before
    fun setup() {
        calculator = SubscriptionProrataCalculator()
    }

    @Test
    fun `calculateProrata mid-cycle accurately credits 50 percent of previous plan`() {
        val dayMs = 24 * 60 * 60 * 1000L
        val cycleStart = 0L
        val cycleEnd = 30 * dayMs // 30 days total
        val midCycle = 15 * dayMs // 15 days remaining

        // Current plan: R$ 60,00 (R$ 2,00/dia). New plan: R$ 100,00
        val result = calculator.calculateProrata(
            currentPlanPrice = 60.00,
            newPlanPrice = 100.00,
            cycleStartMillis = cycleStart,
            cycleEndMillis = cycleEnd,
            changeDateMillis = midCycle
        )

        assertEquals(30, result.totalDaysInCycle)
        assertEquals(15, result.daysRemainingInCycle)
        assertEquals(30.00, result.unusedCreditAmount, 0.05) // 15 * 2.00 = 30.00
        assertEquals(70.00, result.netAmountToPayNow, 0.05)   // 100.00 - 30.00 = 70.00
        assertTrue(result.isUpgrade)
    }

    @Test
    fun `calculateProrata at cycle start credits almost 100 percent of previous plan`() {
        val dayMs = 24 * 60 * 60 * 1000L
        val cycleStart = 0L
        val cycleEnd = 30 * dayMs

        val result = calculator.calculateProrata(
            currentPlanPrice = 50.00,
            newPlanPrice = 80.00,
            cycleStartMillis = cycleStart,
            cycleEndMillis = cycleEnd,
            changeDateMillis = 0L
        )

        assertEquals(30, result.daysRemainingInCycle)
        assertEquals(50.00, result.unusedCreditAmount, 0.05)
        assertEquals(30.00, result.netAmountToPayNow, 0.05) // 80 - 50 = 30
    }
}
