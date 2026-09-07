package br.com.gds.search.screen

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state contains categories and full results`() = runTest {
        val viewModel = SearchViewModel()
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Todos", state.selectedCategory)
            assertTrue(state.categories.contains("Pizzas"))
            assertTrue(state.results.isNotEmpty())
        }
    }

    @Test
    fun `onCategorySelect filters results by selected category`() = runTest {
        val viewModel = SearchViewModel()
        viewModel.onCategorySelect("Pizzas")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Pizzas", state.selectedCategory)
            assertTrue(state.results.all { it.startsWith("Pizza") })
        }
    }

    @Test
    fun `onSearchQueryChange filters results after debounce`() = runTest {
        val viewModel = SearchViewModel()
        viewModel.onSearchQueryChange("Burger")

        // Before debounce, results are not yet filtered
        advanceTimeBy(350)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Burger", state.searchQuery)
            assertTrue(state.results.any { it.contains("Burger") })
            assertTrue(state.results.none { it.contains("Pudim") })
        }
    }
}
