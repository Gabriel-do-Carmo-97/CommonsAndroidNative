package br.com.wgc.scheduling.lock

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [SchedulingConcurrencyLockManager] validando exclusividade temporária,
 * renovação por mesmo usuário, conflitos e expiração de locks de agendamento.
 */
class SchedulingConcurrencyLockManagerTest {

    private lateinit var lockManager: SchedulingConcurrencyLockManager

    @Before
    fun setup() {
        lockManager = SchedulingConcurrencyLockManager()
    }

    @Test
    fun `acquireSlotLock reserves slot exclusively for 10 minutes`() {
        val t0 = 1000000L
        val outcome = lockManager.acquireSlotLock(
            professionalId = "p1",
            serviceId = "s1",
            slotIsoDateTime = "2026-10-25T14:00:00",
            userId = "user-alpha",
            currentTimeMillis = t0,
            holdMinutes = 10
        )

        assertTrue(outcome is ReservationOutcome.Acquired)
        val acquired = outcome as ReservationOutcome.Acquired
        assertEquals(10, acquired.holdMinutesRemaining)
        assertEquals(t0 + (10 * 60 * 1000L), acquired.reservation.expiresAtMillis)
        assertEquals(SlotReservationStatus.RESERVED_ACTIVE, acquired.reservation.status)
    }

    @Test
    fun `acquireSlotLock reports Conflict when another user attempts to lock active slot`() {
        val t0 = 1000000L
        lockManager.acquireSlotLock(
            professionalId = "p1",
            serviceId = "s1",
            slotIsoDateTime = "2026-10-25T14:00:00",
            userId = "user-alpha",
            currentTimeMillis = t0
        )

        // User beta attempts to reserve same slot 2 minutes later
        val outcomeBeta = lockManager.acquireSlotLock(
            professionalId = "p1",
            serviceId = "s1",
            slotIsoDateTime = "2026-10-25T14:00:00",
            userId = "user-beta",
            currentTimeMillis = t0 + (2 * 60 * 1000L)
        )

        assertTrue(outcomeBeta is ReservationOutcome.Conflict)
    }

    @Test
    fun `confirmReservation permanently secures slot`() {
        val outcome = lockManager.acquireSlotLock(
            professionalId = "p2",
            serviceId = "s2",
            slotIsoDateTime = "2026-10-26T10:00:00",
            userId = "user-gamma"
        )
        val resId = (outcome as ReservationOutcome.Acquired).reservation.reservationId

        val success = lockManager.confirmReservation(resId)
        assertTrue(success)

        // Attempting to reserve after confirmation is permanently blocked
        val blockedOutcome = lockManager.acquireSlotLock(
            professionalId = "p2",
            serviceId = "s2",
            slotIsoDateTime = "2026-10-26T10:00:00",
            userId = "user-delta"
        )
        assertTrue(blockedOutcome is ReservationOutcome.Conflict)
    }

    @Test
    fun `slot becomes available after hold timeout expires`() {
        val t0 = 1000000L
        lockManager.acquireSlotLock(
            professionalId = "p3",
            serviceId = "s3",
            slotIsoDateTime = "2026-10-27T16:00:00",
            userId = "user-1",
            currentTimeMillis = t0,
            holdMinutes = 10
        )

        // 11 minutes later, user-2 requests same slot
        val outcomeUser2 = lockManager.acquireSlotLock(
            professionalId = "p3",
            serviceId = "s3",
            slotIsoDateTime = "2026-10-27T16:00:00",
            userId = "user-2",
            currentTimeMillis = t0 + (11 * 60 * 1000L)
        )

        assertTrue(outcomeUser2 is ReservationOutcome.Acquired)
        assertEquals("user-2", (outcomeUser2 as ReservationOutcome.Acquired).reservation.reservedByUserId)
    }
}
