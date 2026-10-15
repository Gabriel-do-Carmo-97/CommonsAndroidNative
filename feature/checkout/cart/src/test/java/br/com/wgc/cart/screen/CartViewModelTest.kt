package br.com.wgc.cart.screen

import app.cash.turbine.test
import br.com.wgc.design_system.templates.screens.cart.CartItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

import br.com.wgc.core.coroutines.CoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val dispatchers = object : CoroutineDispatchers {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
        override val unconfined: CoroutineDispatcher = testDispatcher
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has default items and calculated total`() = runTest {
        val viewModel = CartViewModel(dispatchers)
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(3, state.items.size)
            assertFalse(state.isLoading)
            assertTrue(state.total.contains("R$"))
        }
    }

    @Test
    fun `addItem adds new item and updates total`() = runTest {
        val viewModel = CartViewModel(dispatchers)
        val newItem = CartItem(id = "99", title = "Sobremesa Pudim", price = "R$ 15,00")

        viewModel.addItem(newItem)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(4, state.items.size)
            assertTrue(state.items.any { it.id == "99" })
        }
    }

    @Test
    fun `removeItem removes item from list`() = runTest {
        val viewModel = CartViewModel(dispatchers)

        viewModel.removeItem("1")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(2, state.items.size)
            assertFalse(state.items.any { it.id == "1" })
        }
    }

    @Test
    fun `applyDeliveryFee updates total with shipping fee`() = runTest {
        val viewModel = CartViewModel(dispatchers)
        val initialTotal = viewModel.uiState.value.total

        viewModel.applyDeliveryFee("01310-100")

        val newTotal = viewModel.uiState.value.total
        assertTrue(newTotal != initialTotal)
    }

    @Test
    fun `onCheckoutClick sets loading and restores after completion`() = runTest {
        val viewModel = CartViewModel(dispatchers)
        viewModel.onCheckoutClick()

        testScheduler.runCurrent()
        assertTrue(viewModel.uiState.value.isLoading)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
    }
}
