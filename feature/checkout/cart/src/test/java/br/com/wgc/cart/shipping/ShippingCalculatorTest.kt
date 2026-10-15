package br.com.wgc.cart.shipping

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [ShippingCalculator] validando cálculo de frete por faixas de CEP.
 */
class ShippingCalculatorTest {

    private lateinit var calculator: ShippingCalculator

    @Before
    fun setup() {
        calculator = ShippingCalculator()
    }

    @Test
    fun `calculateShipping for SP Capital returns express delivery fee 7_90`() {
        // CEP Av. Paulista: 01310-100
        val estimate = calculator.calculateShipping("01310-100")

        assertEquals(7.90, estimate.fee, 0.01)
        assertTrue(estimate.isExpress)
        assertTrue(estimate.estimatedDeliveryTime.contains("minutos"))
    }

    @Test
    fun `calculateShipping for SP Interior returns 14_90`() {
        // CEP Campinas: 13010-000
        val estimate = calculator.calculateShipping("13010-000")

        assertEquals(14.90, estimate.fee, 0.01)
        assertFalse(estimate.isExpress)
        assertEquals("1 a 2 dias úteis", estimate.estimatedDeliveryTime)
    }

    @Test
    fun `calculateShipping for invalid length returns fallback rate`() {
        val estimate = calculator.calculateShipping("123")

        assertEquals(15.00, estimate.fee, 0.01)
        assertFalse(estimate.isExpress)
    }
}
