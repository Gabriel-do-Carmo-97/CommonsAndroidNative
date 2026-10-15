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

import br.com.wgc.payment.navigation.PaymentNavDestinations
import kotlinx.coroutines.test.UnconfinedTestDispatcher

/**
 * Suíte de testes unitários para o [PaymentViewModel].
 *
 * Valida o estado financeiro da conta, alternância de visibilidade do saldo
 * e emissão de eventos de navegação para fluxos de Pix, transferências e histórico.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PaymentViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

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

    @Test
    fun `onPixClick should emit Checkout destination`() = runTest {
        val viewModel = PaymentViewModel()

        viewModel.navigationEvent.test {
            viewModel.onPixClick()
            val event = awaitItem()
            assertTrue(event is PaymentNavDestinations.Checkout)
        }
    }

    @Test
    fun `onViewHistoryClick should emit PaymentHistory destination with correct userId`() = runTest {
        val viewModel = PaymentViewModel()
        val userId = "user_42"

        viewModel.navigationEvent.test {
            viewModel.onViewHistoryClick(userId)
            val event = awaitItem()
            assertTrue(event is PaymentNavDestinations.PaymentHistory)
            assertEquals(userId, (event as PaymentNavDestinations.PaymentHistory).userId)
        }
    }
}
