package br.com.wgc.payment.navigation

/**
 * Contrato seguro e tipado de destinos de navegação para o ecossistema de Pagamentos, Carteira e Checkout.
 *
 * Garante que transições entre a carteira do usuário, checkout de pedidos, confirmações
 * e extrato histórico ocorram de maneira segura e desacoplada dos controladores de navegação Compose.
 *
 * ### 📌 Quando Usar:
 * Utilize para emitir intenções de navegação a partir do [PaymentViewModel] ou para registrar
 * destinos de tela dentro do grafo central de rotas (`NavHost`).
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * when (destination) {
 *     is PaymentNavDestinations.Checkout -> navController.navigate("checkout")
 *     is PaymentNavDestinations.PaymentHistory -> navController.navigate("history/${destination.userId}")
 * }
 * ```
 */
sealed interface PaymentNavDestinations {

    /**
     * Rota de fechamento e confirmação de pagamento do pedido atual.
     *
     * ### 📌 Quando Usar:
     * Disparada quando o cliente confirma os métodos de pagamento (Pix, Cartão) e autoriza a transação.
     */
    data object Checkout : PaymentNavDestinations

    /**
     * Rota de histórico e extrato de transações financeiras e pagamentos do cliente.
     *
     * ### 📌 Quando Usar:
     * Acionada quando o usuário solicita a visualização detalhada de transações anteriores.
     *
     * ### 🛠️ Modo de Usar:
     * ```kotlin
     * val route = PaymentNavDestinations.PaymentHistory(userId = "usr_9981")
     * ```
     *
     * @property userId Identificador único do usuário cujo extrato deve ser carregado.
     */
    data class PaymentHistory(val userId: String) : PaymentNavDestinations
}