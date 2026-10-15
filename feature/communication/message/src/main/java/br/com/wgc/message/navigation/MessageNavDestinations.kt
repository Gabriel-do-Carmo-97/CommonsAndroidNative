package br.com.wgc.message.navigation

/**
 * Destinos e rotas fortemente tipadas para o módulo de Comunicação, Mensagens e Atendimento.
 *
 * Provê navegação segura e desacoplada para canais de suporte, conversas diretas com motoristas e chat in-app.
 *
 * ### 📌 Quando Usar:
 * Utilize para rotear o usuário entre a listagem de conversas (`ChatList`) ou para a tela
 * de mensagens com um atendente ou entregador (`ChatConversation`).
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val route = MessageNavDestinations.ChatConversation(chatId = "conv_771")
 * ```
 */
sealed interface MessageNavDestinations {

    /**
     * Rota para a lista de canais de conversas ativas do usuário.
     *
     * ### 📌 Quando Usar:
     * Para abrir a central de mensagens e histórico de conversas do aplicativo.
     */
    data object ChatList : MessageNavDestinations

    /**
     * Rota para uma conversa específica em tempo real.
     *
     * ### 📌 Quando Usar:
     * Disparada ao clicar em uma conversa da listagem ou no botão "Falar com Motorista / Suporte".
     *
     * ### 🛠️ Modo de Usar:
     * ```kotlin
     * val dest = MessageNavDestinations.ChatConversation("support_chat_01")
     * ```
     *
     * @property chatId Identificador único da sala de conversa.
     */
    data class ChatConversation(val chatId: String) : MessageNavDestinations
}