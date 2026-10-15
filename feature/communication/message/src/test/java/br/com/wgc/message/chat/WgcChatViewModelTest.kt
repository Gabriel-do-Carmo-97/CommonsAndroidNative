package br.com.wgc.message.chat

import app.cash.turbine.test
import br.wgc.omnibackend.core.model.database.message.MessageRequest
import br.wgc.omnibackend.core.model.database.message.MessageStatus
import br.wgc.omnibackend.core.repository.realtime.MessageRepository
import br.wgc.omnibackend.core.utils.DataResult
import io.mockk.coEvery
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Suíte de testes unitários para [WgcChatViewModel].
 *
 * Valida a inicialização da conversa, recepção de mensagens reativas do [MessageRepository],
 * alteração de texto em digitação e envio bem-sucedido de mensagens.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WgcChatViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val messageRepository = mockk<MessageRepository>()

    private val sampleMessages = listOf(
        MessageRequest(
            messageId = "msg_1",
            senderId = "driver_99",
            content = "Estou chegando no portão!",
            timestamp = 1700000000L,
            status = MessageStatus.DELIVERED
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { messageRepository.getMessages(any()) } returns flowOf(
            DataResult.Success(sampleMessages)
        )
        coEvery { messageRepository.sendMessage(any(), any()) } returns DataResult.Success(Unit)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initChat loads messages from stream and updates recipient name`() = runTest {
        val viewModel = WgcChatViewModel(messageRepository)

        viewModel.initChat(
            conversationId = "conv_123",
            currentUserId = "user_456",
            recipientName = "Entregador Parceiro"
        )

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("conv_123", state.conversationId)
            assertEquals("user_456", state.currentUserId)
            assertEquals("Entregador Parceiro", state.recipientName)
            assertEquals(1, state.messages.size)
            assertEquals("Estou chegando no portão!", state.messages.first().content)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `onInputTextChanged updates inputText in state`() = runTest {
        val viewModel = WgcChatViewModel(messageRepository)

        viewModel.onInputTextChanged("Obrigado, já estou descendo!")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Obrigado, já estou descendo!", state.inputText)
        }
    }

    @Test
    fun `sendMessage transmits message via repository and resets inputText`() = runTest {
        val viewModel = WgcChatViewModel(messageRepository)

        viewModel.initChat(
            conversationId = "conv_123",
            currentUserId = "user_456"
        )

        viewModel.onInputTextChanged("Estou no portão.")
        viewModel.sendMessage()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("", state.inputText)
            assertFalse(state.isSending)
        }
    }
}
