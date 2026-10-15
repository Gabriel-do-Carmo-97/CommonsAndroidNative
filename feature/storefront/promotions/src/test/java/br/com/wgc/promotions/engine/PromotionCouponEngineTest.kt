package br.com.wgc.promotions.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para o motor de cupons e promoções [PromotionCouponEngine].
 */
class PromotionCouponEngineTest {

    private lateinit var engine: PromotionCouponEngine

    @Before
    fun setup() {
        engine = PromotionCouponEngine()
    }

    @Test
    fun `applyCoupon with WGC10 calculates 10 percent discount correctly`() {
        val result = engine.applyCoupon(couponCode = "WGC10", subtotal = 100.0, shippingFee = 10.0)

        assertTrue(result is CouponValidationResult.Valid)
        val valid = result as CouponValidationResult.Valid
        assertEquals(10.0, valid.discountAmount, 0.01)
        assertEquals(100.0, valid.finalTotal, 0.01) // 100 - 10 + 10 shipping
        assertFalse(valid.isFreeShippingGranted)
    }

    @Test
    fun `applyCoupon with PRIMEIRACOMPRA gives 20 BRL off above minimum order`() {
        val result = engine.applyCoupon(couponCode = "primeiracompra", subtotal = 80.0, shippingFee = 8.0)

        assertTrue(result is CouponValidationResult.Valid)
        val valid = result as CouponValidationResult.Valid
        assertEquals(20.0, valid.discountAmount, 0.01)
        assertEquals(68.0, valid.finalTotal, 0.01) // 80 - 20 + 8 shipping
    }

    @Test
    fun `applyCoupon with FRETEGRATIS zeros shipping fee`() {
        val result = engine.applyCoupon(couponCode = "FRETEGRATIS", subtotal = 50.0, shippingFee = 15.0)

        assertTrue(result is CouponValidationResult.Valid)
        val valid = result as CouponValidationResult.Valid
        assertEquals(0.0, valid.discountAmount, 0.01)
        assertTrue(valid.isFreeShippingGranted)
        assertEquals(50.0, valid.finalTotal, 0.01)
    }

    @Test
    fun `applyCoupon below minOrderValue returns Invalid with friendly reason`() {
        val result = engine.applyCoupon(couponCode = "WGC10", subtotal = 20.0, shippingFee = 5.0)

        assertTrue(result is CouponValidationResult.Invalid)
        val invalid = result as CouponValidationResult.Invalid
        assertTrue(invalid.reason.contains("Valor mínimo"))
    }

    @Test
    fun `applyCoupon with expired coupon returns Invalid`() {
        val expired = PromotionCoupon(
            code = "EXPIRADO",
            discountType = DiscountType.FIXED_AMOUNT,
            discountValue = 10.0,
            expiresAt = 1000L
        )
        engine.registerCoupon(expired)

        val result = engine.applyCoupon(couponCode = "EXPIRADO", subtotal = 50.0, shippingFee = 5.0, currentTimeMillis = 2000L)
        assertTrue(result is CouponValidationResult.Invalid)
    }
}
