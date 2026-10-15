package br.com.wgc.payment.success

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Testes unitários para o modelo [CompletedOrderSummary].
 */
class CompletedOrderSummaryTest {

    @Test
    fun `completedOrderSummary instantiates correctly with all fields`() {
        val summary = CompletedOrderSummary(
            orderId = "order_1001",
            protocolNumber = "NF-WGC-2026-001",
            totalAmount = "R$ 119,90",
            paymentMethodModal = "Pix Instantâneo",
            estimatedDeliveryTime = "40 minutos"
        )

        assertNotNull(summary)
        assertEquals("order_1001", summary.orderId)
        assertEquals("NF-WGC-2026-001", summary.protocolNumber)
        assertEquals("R$ 119,90", summary.totalAmount)
        assertEquals("Pix Instantâneo", summary.paymentMethodModal)
        assertEquals("40 minutos", summary.estimatedDeliveryTime)
    }
}
