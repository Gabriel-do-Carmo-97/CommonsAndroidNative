package br.com.wgc.offline_sync.screen

import app.cash.turbine.test
import br.com.wgc.core.sharedPreferences.InMemoryKeyValueStorage
import br.com.wgc.offline_sync.queue.MutationOperationType
import br.com.wgc.offline_sync.queue.OfflineMutationQueueManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineSyncViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var storage: InMemoryKeyValueStorage
    private lateinit var queueManager: OfflineMutationQueueManager
    private lateinit var viewModel: OfflineSyncViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        storage = InMemoryKeyValueStorage()
        queueManager = OfflineMutationQueueManager(storage)
        viewModel = OfflineSyncViewModel(queueManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state contains title and zero pending count`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Módulo de Sincronização Offline WGC", state.title)
            assertEquals(0, state.pendingCount)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `triggerSync with queued mutations drains the queue and updates timestamp`() = runTest {
        queueManager.enqueueMutation(
            entityType = "order",
            entityId = "ord_1",
            operation = MutationOperationType.CREATE,
            payloadJson = "{\"total\": 100}"
        )
        viewModel.refreshQueueStatus()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.pendingCount)

            viewModel.triggerSync()

            val synced = awaitItem()
            assertEquals(0, synced.pendingCount)
            assertFalse(synced.isLoading)
            assertTrue(synced.lastSyncTimestamp > 0L)
        }
    }
}
