package br.com.wgc.payment.navigation

/**
 * Destinos de navegaÃ§Ã£o seguros para o fluxo de Pagamento e Checkout.
 */
sealed interface PaymentNavDestinations {

    /**
     * Rota de fechamento e confirmaÃ§Ã£o de pagamento do pedido.
     */
    data object Checkout : PaymentNavDestinations

    /**
     * Rota de histÃ³rico e extrato de transaÃ§Ãµes de pagamento do cliente.
     *
     * @param userId Identificador Ãºnico do usuÃ¡rio associado ao extrato.
     * @property userId Identificador Ãºnico do usuÃ¡rio associado ao extrato.
     */
    data class PaymentHistory(val userId: String) : PaymentNavDestinations
}