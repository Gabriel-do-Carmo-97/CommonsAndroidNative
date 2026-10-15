package br.com.wgc.payment.pix

import br.com.wgc.core.coroutines.CoroutineDispatchers
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [PixPaymentManager] validando geração de payload EMV e fluxo de liquidação.
 */
class PixPaymentManagerTest {

    private val testDispatchers = object : CoroutineDispatchers {
        override val main: kotlinx.coroutines.CoroutineDispatcher = Dispatchers.Unconfined
        override val io: kotlinx.coroutines.CoroutineDispatcher = Dispatchers.Unconfined
        override val default: kotlinx.coroutines.CoroutineDispatcher = Dispatchers.Unconfined
        override val unconfined: kotlinx.coroutines.CoroutineDispatcher = Dispatchers.Unconfined
    }

    private lateinit var pixManager: PixPaymentManager

    @Before
    fun setup() {
        pixManager = PixPaymentManager(testDispatchers)
    }

    @Test
    fun `createPixOrder generates valid EMV payload and sets WaitingPayment status`() {
        val order = pixManager.createPixOrder(orderId = "order-99", amount = 85.00)

        assertTrue(order.txId.startsWith("WGC"))
        assertTrue(order.emvCopiaECola.contains("br.gov.bcb.pix"))
        assertEquals(85.00, order.amount, 0.01)

        val status = pixManager.paymentStatus.value
        assertTrue(status is PixPaymentStatus.WaitingPayment)
    }

    @Test
    fun `confirmPayment transitions status to Confirmed with valid E2E code`() {
        pixManager.createPixOrder(orderId = "order-100", amount = 42.00)
        pixManager.confirmPayment(endToEndId = "E1234567890")

        val status = pixManager.paymentStatus.value
        assertTrue(status is PixPaymentStatus.Confirmed)
        val confirmed = status as PixPaymentStatus.Confirmed
        assertEquals("E1234567890", confirmed.endToEndId)
    }

    @Test
    fun `cancelOrder sets Cancelled status`() {
        pixManager.createPixOrder(orderId = "order-101", amount = 10.00)
        pixManager.cancelOrder()

        val status = pixManager.paymentStatus.value
        assertTrue(status is PixPaymentStatus.Cancelled)
    }
}
