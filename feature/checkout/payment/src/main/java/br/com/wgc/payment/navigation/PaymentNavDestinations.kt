package br.com.wgc.payment.navigation

/**
 * Destinos de navegação seguros para o fluxo de Pagamento e Checkout.
 */
sealed interface PaymentNavDestinations {

    /**
     * Rota de fechamento e confirmação de pagamento do pedido.
     */
    data object Checkout : PaymentNavDestinations

    /**
     * Rota de histórico e extrato de transações de pagamento do cliente.
     *
     * @param userId Identificador único do usuário associado ao extrato.
     * @property userId Identificador único do usuário associado ao extrato.
     */
    data class PaymentHistory(val userId: String) : PaymentNavDestinations
}