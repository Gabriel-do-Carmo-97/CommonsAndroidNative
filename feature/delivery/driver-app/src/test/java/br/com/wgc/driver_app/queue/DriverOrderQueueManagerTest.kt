package br.com.wgc.driver_app.queue

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [DriverOrderQueueManager] validando aceite, recusa e expiração de despachos.
 */
class DriverOrderQueueManagerTest {

    private lateinit var queueManager: DriverOrderQueueManager

    @Before
    fun setup() {
        queueManager = DriverOrderQueueManager()
    }

    @Test
    fun `enqueueOffer adds offer and acceptOffer marks it ACCEPTED before timeout`() {
        val t0 = 1000000L
        val offer = DispatchOffer(
            id = "off-1",
            orderId = "ord-1",
            pickupAddress = "Loja",
            deliveryAddress = "Cliente",
            distanceKm = 3.0,
            estimatedEarnings = "R$ 15,00",
            timeoutSeconds = 30,
            createdAtMillis = t0
        )

        queueManager.enqueueOffer(offer)
        assertEquals(1, queueManager.getPendingOffers().size)

        // Accept at t0 + 10s (inside 30s window)
        val success = queueManager.acceptOffer("off-1", currentTimeMillis = t0 + 10000L)
        assertTrue(success)

        val updatedOffer = queueManager.offers.value.first { it.id == "off-1" }
        assertEquals(OfferStatus.ACCEPTED, updatedOffer.status)
        assertTrue(queueManager.getPendingOffers().isEmpty())
    }

    @Test
    fun `acceptOffer fails and expires when timeout has elapsed`() {
        val t0 = 1000000L
        val offer = DispatchOffer(
            id = "off-timeout",
            orderId = "ord-2",
            pickupAddress = "Loja",
            deliveryAddress = "Cliente",
            distanceKm = 1.0,
            estimatedEarnings = "R$ 10,00",
            timeoutSeconds = 30,
            createdAtMillis = t0
        )

        queueManager.enqueueOffer(offer)

        // Attempt accept at t0 + 35s (past 30s timeout)
        val success = queueManager.acceptOffer("off-timeout", currentTimeMillis = t0 + 35000L)
        assertFalse(success)

        val updatedOffer = queueManager.offers.value.first { it.id == "off-timeout" }
        assertEquals(OfferStatus.EXPIRED, updatedOffer.status)
    }

    @Test
    fun `rejectOffer transitions status to REJECTED`() {
        val offer = DispatchOffer(
            id = "off-reject",
            orderId = "ord-3",
            pickupAddress = "Restaurante",
            deliveryAddress = "Destino",
            distanceKm = 5.0,
            estimatedEarnings = "R$ 22,00"
        )

        queueManager.enqueueOffer(offer)
        val success = queueManager.rejectOffer("off-reject")
        assertTrue(success)

        val updatedOffer = queueManager.offers.value.first { it.id == "off-reject" }
        assertEquals(OfferStatus.REJECTED, updatedOffer.status)
    }

    @Test
    fun `checkAndExpireOffers identifies expired offers`() {
        val t0 = 1000000L
        val offer = DispatchOffer(
            id = "off-check",
            orderId = "ord-4",
            pickupAddress = "A",
            deliveryAddress = "B",
            distanceKm = 2.0,
            estimatedEarnings = "R$ 12,00",
            timeoutSeconds = 30,
            createdAtMillis = t0
        )

        queueManager.enqueueOffer(offer)

        val expired = queueManager.checkAndExpireOffers(currentTimeMillis = t0 + 31000L)
        assertEquals(1, expired.size)
        assertEquals("off-check", expired[0].id)
        assertEquals(OfferStatus.EXPIRED, expired[0].status)
    }
}
