package br.com.wgc.message.navigation

/**
 * Destinos e rotas seguras para navegaÃ§Ã£o no mÃ³dulo de mensagens e atendimento.
 */
sealed interface MessageNavDestinations {

    /**
     * Rota para a lista de canais de conversas ativas do usuÃ¡rio.
     */
    data object ChatList : MessageNavDestinations

    /**
     * Rota para uma conversa especÃ­fica.
     *
     * @param chatId Identificador Ãºnico da conversa.
     * @property chatId Identificador Ãºnico da conversa.
     */
    data class ChatConversation(val chatId: String) : MessageNavDestinations
}