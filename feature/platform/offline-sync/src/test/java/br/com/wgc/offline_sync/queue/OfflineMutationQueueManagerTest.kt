package br.com.wgc.offline_sync.queue

import br.com.wgc.core.sharedPreferences.InMemoryKeyValueStorage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [OfflineMutationQueueManager] validando enfileiramento,
 * remoção após sucesso, contadores de retry e backoff exponencial.
 */
class OfflineMutationQueueManagerTest {

    private lateinit var storage: InMemoryKeyValueStorage
    private lateinit var queueManager: OfflineMutationQueueManager

    @Before
    fun setup() {
        storage = InMemoryKeyValueStorage()
        queueManager = OfflineMutationQueueManager(storage)
    }

    @Test
    fun `enqueueMutation adds pending mutation and markCompleted removes it`() {
        val record = queueManager.enqueueMutation(
            entityType = "cart",
            entityId = "item-10",
            operation = MutationOperationType.CREATE,
            payloadJson = """{"quantity": 2}"""
        )

        val pending = queueManager.getPendingMutations()
        assertEquals(1, pending.size)
        assertEquals(record.mutationId, pending[0].mutationId)

        queueManager.markCompleted(record.mutationId)
        assertTrue(queueManager.getPendingMutations().isEmpty())
    }

    @Test
    fun `recordRetryFailure increments retry count and marks FAILED at threshold`() {
        val record = queueManager.enqueueMutation(
            entityType = "order",
            entityId = "ord-1",
            operation = MutationOperationType.UPDATE,
            payloadJson = "{}"
        )

        // Retry 1 to 4: remains PENDING
        repeat(4) {
            queueManager.recordRetryFailure(record.mutationId)
        }
        assertEquals(1, queueManager.getPendingMutations().size)

        // 5th retry: transitions to FAILED
        val failedRecord = queueManager.recordRetryFailure(record.mutationId)
        assertEquals(5, failedRecord?.retryCount)
        assertEquals(MutationStatus.FAILED, failedRecord?.status)
        assertTrue(queueManager.getPendingMutations().isEmpty()) // No longer in pending list
    }

    @Test
    fun `calculateExponentialBackoffDelayMs scales powers of 2 clamped to max`() {
        assertEquals(0L, queueManager.calculateExponentialBackoffDelayMs(0))
        assertEquals(5000L, queueManager.calculateExponentialBackoffDelayMs(1))  // 5000 * 2^0 = 5000
        assertEquals(10000L, queueManager.calculateExponentialBackoffDelayMs(2)) // 5000 * 2^1 = 10000
        assertEquals(20000L, queueManager.calculateExponentialBackoffDelayMs(3)) // 5000 * 2^2 = 20000

        // Clamped at max 300_000ms
        val highRetry = queueManager.calculateExponentialBackoffDelayMs(10, maxDelayMs = 60000L)
        assertEquals(60000L, highRetry)
    }
}
