package br.com.gds.message.chat

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

@HiltViewModel
class WgcChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WgcChatUiState())
    val uiState: StateFlow<WgcChatUiState> = _uiState.asStateFlow()

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

    fun onInputTextChanged(newText: String) {
        _uiState.update { it.copy(inputText = newText) }
    }

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

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
