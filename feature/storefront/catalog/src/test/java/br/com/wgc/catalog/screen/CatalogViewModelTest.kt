package br.com.wgc.catalog.screen

import app.cash.turbine.test
import br.wgc.omnibackend.core.repository.FirestoreRepository
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModelTest {
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
    fun `initial state contains categories and featured products`() = runTest {
        val viewModel = CatalogViewModel(firestoreRepository)
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Todos", state.selectedCategory)
            assertTrue(state.categories.contains("Pizzas") || state.categories.contains("Destaques"))
            assertTrue(state.featuredProducts.isNotEmpty())
        }
    }

    @Test
    fun `selectCategory updates selected category in state`() = runTest {
        val viewModel = CatalogViewModel(firestoreRepository)
        viewModel.selectCategory("Bebidas")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Bebidas", state.selectedCategory)
        }
    }
}
