package br.com.wgc.message.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.wgc.omnibackend.core.model.database.message.MessageRequest
import br.wgc.omnibackend.core.model.database.message.MessageStatus
import br.wgc.omnibackend.core.repository.realtime.MessageRepository
import br.wgc.omnibackend.core.utils.DataResult
import br.wgc.omnibackend.core.utils.message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * Estado imutÃ¡vel da tela de chat e troca de mensagens.
 *
 * @property conversationId Identificador Ãºnico da sala de conversa.
 * @property currentUserId Identificador Ãºnico do autor das novas mensagens.
 * @property recipientName Nome amigÃ¡vel do destinatÃ¡rio da conversa.
 * @property messages HistÃ³rico cronolÃ³gico das mensagens trocadas.
 * @property inputText Texto em digitaÃ§Ã£o na caixa de entrada.
 * @property isSending Sinalizador de transmissÃ£o de mensagem em andamento.
 * @property isLoading Sinalizador de sincronizaÃ§Ã£o inicial do histÃ³rico.
 * @property errorMessage Mensagem de falha capturada durante a troca de mensagens.
 */
data class WgcChatUiState(
    val conversationId: String = "",
    val currentUserId: String = "",
    val recipientName: String = "Suporte / Atendimento",
    val messages: List<MessageRequest> = emptyList(),
    val inputText: String = "",
    val isSending: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel corporativo responsÃ¡vel pela sincronizaÃ§Ã£o de mensagens em tempo real via [MessageRepository].
 *
 * @param messageRepository RepositÃ³rio do OmniBackend encarregado de canais de WebSocket ou streams de chat.
 */
@HiltViewModel
class WgcChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WgcChatUiState())

    /**
     * Fluxo de estado observÃ¡vel com as informaÃ§Ãµes completas da sessÃ£o de chat.
     */
    val uiState: StateFlow<WgcChatUiState> = _uiState.asStateFlow()

    /**
     * Inicializa a escuta reativa da conversa especificada e carrega o histÃ³rico de mensagens.
     *
     * @param conversationId Identificador do canal de conversa.
     * @param currentUserId Identificador do usuÃ¡rio conectado.
     * @param recipientName Nome de exibiÃ§Ã£o do destinatÃ¡rio.
     */
    fun initChat(
        conversationId: String,
        currentUserId: String,
        recipientName: String = "Suporte / Atendimento"
    ) {
        _uiState.update {
            it.copy(
                conversationId = conversationId,
                currentUserId = currentUserId,
                recipientName = recipientName,
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            messageRepository.getMessages(conversationId).collect { result ->
                when (result) {
                    is DataResult.Success -> {
                        _uiState.update {
                            it.copy(
                                messages = result.data,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    }
                    is DataResult.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.error.message
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Atualiza o valor do texto em digitaÃ§Ã£o no campo de mensagem.
     *
     * @param newText Novo conteÃºdo textual.
     */
    fun onInputTextChanged(newText: String) {
        _uiState.update { it.copy(inputText = newText) }
    }

    /**
     * Empacota e envia a mensagem atual atravÃ©s do [MessageRepository].
     */
    fun sendMessage() {
        val currentState = _uiState.value
        val text = currentState.inputText.trim()
        if (text.isEmpty() || currentState.conversationId.isEmpty() || currentState.isSending) {
            return
        }

        val message = MessageRequest(
            messageId = UUID.randomUUID().toString(),
            senderId = currentState.currentUserId,
            content = text,
            timestamp = System.currentTimeMillis(),
            status = MessageStatus.SENDING
        )

        _uiState.update { it.copy(isSending = true, errorMessage = null) }

        viewModelScope.launch {
            val result = messageRepository.sendMessage(currentState.conversationId, message)
            when (result) {
                is DataResult.Success -> {
                    _uiState.update {
                        it.copy(
                            inputText = "",
                            isSending = false
                        )
                    }
                }
                is DataResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isSending = false,
                            errorMessage = result.error.message
                        )
                    }
                }
            }
        }
    }

    /**
     * Limpa a mensagem de erro pendente apÃ³s sua exibiÃ§Ã£o na interface.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}