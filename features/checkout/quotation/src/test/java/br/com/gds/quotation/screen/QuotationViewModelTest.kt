package br.com.gds.quotation.screen

import app.cash.turbine.test
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
class QuotationViewModelTest {

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
    fun `initial state calculates subtotal and total accurately`() = runTest {
        val viewModel = QuotationViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(640.0, state.subtotal, 0.01)
            assertEquals(64.0, state.discountAmount, 0.01)
            assertEquals(576.0, state.total, 0.01)
        }
    }

    @Test
    fun `addItem recalculates total with discount applied`() = runTest {
        val viewModel = QuotationViewModel()

        viewModel.addItem("Garantia Estendida 12 Meses", 100.0)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(4, state.items.size)
            assertEquals(740.0, state.subtotal, 0.01)
            assertEquals(74.0, state.discountAmount, 0.01)
            assertEquals(666.0, state.total, 0.01)
        }
    }

    @Test
    fun `generateWhatsAppText outputs formatted quote with client and totals`() = runTest {
        val viewModel = QuotationViewModel()

        val text = viewModel.generateWhatsAppText()
        assertTrue(text.contains("ORÇAMENTO PERSONALIZADO WGC"))
        assertTrue(text.contains("Cliente VIP WGC"))
        assertTrue(text.contains("Total: R$ 576,00") || text.contains("Total: R$ 576.00"))
    }
}
