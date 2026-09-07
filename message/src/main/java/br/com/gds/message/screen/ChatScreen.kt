package br.com.gds.message.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.gds.message.chat.WgcChatScreen

/**
 * High-level Chat Screen providing real-time conversation.
 * Delegates directly to [WgcChatScreen] connected with OmniBackend and DS Templates.
 */
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    conversationId: String = "suporte_geral_001",
    currentUserId: String = "demo_user",
    recipientName: String = "Atendimento Omni WGC",
    onBackClick: () -> Unit = {}
) {
    WgcChatScreen(
        conversationId = conversationId,
        currentUserId = currentUserId,
        recipientName = recipientName,
        onBackClick = onBackClick,
        modifier = modifier
    )
}
