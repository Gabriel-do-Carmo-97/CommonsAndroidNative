package br.com.wgc.ai_assistant.screen

import app.cash.turbine.test
import br.com.wgc.ai_assistant.engine.AiAssistantStreamingEngine
import br.com.wgc.ai_assistant.engine.AiStreamEvent
import br.com.wgc.ai_assistant.intent.AiIntentActionParser
import br.com.wgc.ai_assistant.intent.AiIntentType
import br.com.wgc.ai_assistant.intent.AiParsedAction
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AiAssistantViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val streamingEngine = mockk<AiAssistantStreamingEngine>()
    private val intentParser = mockk<AiIntentActionParser>()
    private lateinit var viewModel: AiAssistantViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AiAssistantViewModel(streamingEngine, intentParser)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state contains empty messages and default title`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Módulo de Assistente de IA WGC", state.title)
            assertEquals(0, state.messages.size)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `sendPrompt parses intent and receives streaming tokens`() = runTest {
        val parsedAction = AiParsedAction(
            intentType = AiIntentType.REORDER_LAST_PURCHASE,
            confidence = 0.95,
            targetNavigationRoute = "wgc://checkout/cart/reorder",
            userFriendlyResponse = "Recompra iniciada"
        )
        every { intentParser.parseUserInput("repetir pedido") } returns parsedAction
        every { streamingEngine.streamCompletion("repetir pedido") } returns flowOf(
            AiStreamEvent.Token("Claro, "),
            AiStreamEvent.Token("repetindo!"),
            AiStreamEvent.Completed("Claro, repetindo!")
        )

        viewModel.uiState.test {
            awaitItem() // initial

            viewModel.sendPrompt("repetir pedido")

            // Wait for final state after stream completion
            val finalState = expectMostRecentItem()
            assertEquals(2, finalState.messages.size)
            assertEquals("repetir pedido", finalState.messages[0].text)
            assertEquals("Claro, repetindo!", finalState.messages[1].text)
            assertEquals(AiIntentType.REORDER_LAST_PURCHASE, finalState.triggeredAction?.intentType)
            assertFalse(finalState.isLoading)
        }
    }
}
