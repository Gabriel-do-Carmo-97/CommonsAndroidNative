package br.com.wgc.whatsapp_direct.screen

import app.cash.turbine.test
import br.com.wgc.whatsapp_direct.helper.WhatsappDirectHelper
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
class WhatsappDirectViewModelTest {

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
    fun `initial state formats default order message`() = runTest {
        val viewModel = WhatsappDirectViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.formattedMessage.contains("NOVO PEDIDO"))
            assertTrue(state.formattedMessage.contains("Gabriel Carmo"))
            assertTrue(state.formattedMessage.contains("R$ 51,00"))
        }
    }

    @Test
    fun `updating customer name and notes re-formats message`() = runTest {
        val viewModel = WhatsappDirectViewModel()

        viewModel.updateCustomerName("Mariana Souza")
        viewModel.updateNotes("Entregar na portaria")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Mariana Souza", state.customerName)
            assertTrue(state.formattedMessage.contains("Mariana Souza"))
            assertTrue(state.formattedMessage.contains("Entregar na portaria"))
        }
    }

    @Test
    fun `cleanPhoneNumber prepends country code 55 when missing`() {
        val cleaned = WhatsappDirectHelper.cleanPhoneNumber("(11) 99999-8888")
        assertEquals("5511999998888", cleaned)
    }
}
