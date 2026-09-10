package br.com.wgc.payment.screen

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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
class PaymentViewModelTest {

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
    fun `initial uiState should contain user name, balance and transactions`() = runTest {
        val viewModel = PaymentViewModel()
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Gabriel do Carmo", state.userName)
            assertTrue(state.balance.contains("R$"))
            assertTrue(state.isBalanceVisible)
            assertTrue(state.transactions.isNotEmpty())
        }
    }

    @Test
    fun `onToggleBalanceVisibility should toggle balance visibility`() = runTest {
        val viewModel = PaymentViewModel()

        viewModel.onToggleBalanceVisibility()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isBalanceVisible)
        }
    }
}
