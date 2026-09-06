package br.com.gds.message.navigation

sealed interface MessageNavDestinations {
    data object ChatList : MessageNavDestinations
    data class ChatConversation(val chatId: String) : MessageNavDestinations
}
