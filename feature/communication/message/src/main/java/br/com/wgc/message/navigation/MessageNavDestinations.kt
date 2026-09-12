package br.com.wgc.message.navigation

/**
 * Destinos e rotas seguras para navegação no módulo de mensagens e atendimento.
 */
sealed interface MessageNavDestinations {

    /**
     * Rota para a lista de canais de conversas ativas do usuário.
     */
    data object ChatList : MessageNavDestinations

    /**
     * Rota para uma conversa específica.
     *
     * @param chatId Identificador único da conversa.
     * @property chatId Identificador único da conversa.
     */
    data class ChatConversation(val chatId: String) : MessageNavDestinations
}