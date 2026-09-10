package br.com.wgc.stores.screen

import app.cash.turbine.test
import br.wgc.omnibackend.core.repository.FirestoreRepository
import br.wgc.omnibackend.core.utils.DataResult
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StoresViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val firestoreRepository = mockk<FirestoreRepository>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads default stores on relaxed or failure repository`() = runTest {
        val viewModel = StoresViewModel(firestoreRepository)

        viewModel.uiState.test {
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)

            testScheduler.runCurrent()

            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
            assertTrue(loadedState.stores.isNotEmpty())
        }
    }

    @Test
    fun `search query filters stores list`() = runTest {
        val viewModel = StoresViewModel(firestoreRepository)
        testScheduler.runCurrent()

        viewModel.onSearchQueryChanged("Paulista")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.filteredStores.size)
            assertTrue(state.filteredStores.first().name.contains("Paulista"))
        }
    }

    @Test
    fun `toggle only open filter excludes closed stores`() = runTest {
        val viewModel = StoresViewModel(firestoreRepository)
        testScheduler.runCurrent()

        viewModel.onToggleOnlyOpen(true)

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.onlyOpenFilter)
            assertTrue(state.filteredStores.all { it.isOpen })
        }
    }

    @Test
    fun `selectStore updates selectedStore in uiState`() = runTest {
        val viewModel = StoresViewModel(firestoreRepository)
        testScheduler.runCurrent()

        val target = StoresViewModel.defaultStores[0]
        viewModel.selectStore(target)

        viewModel.uiState.test {
            val state = awaitItem()
            assertNotNull(state.selectedStore)
            assertEquals(target.id, state.selectedStore?.id)
        }
    }
}
