package br.com.wgc.testing.fakes

/**
 * Formas de pagamento suportadas nos testes de integração do checkout.
 */
enum class FakePaymentMethod {
    /** Pagamento instantâneo via PIX com QR Code. */
    PIX,
    /** Cartão de crédito nacional ou internacional. */
    CREDIT_CARD,
    /** Boleto bancário com código de barras. */
    BANK_SLIP
}

/**
 * Resultado simulado de uma transação financeira no fluxo de checkout.
 *
 * @property transactionId Identificador da transação gerada.
 * @property isSuccess Indica se a transação foi aprovada pelo gateway simulado.
 * @property errorMessage Mensagem de erro caso a transação tenha falhado.
 */
data class FakePaymentResult(
    val transactionId: String,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

/**
 * Repositório simulado de processamento de pagamentos para testes de checkout.
 *
 * Permite configurar respostas intencionais de sucesso ou falha para validar estados de erro na UI.
 */
class FakePaymentRepository {
    private var shouldFailNext = false
    private var nextErrorMessage: String? = null

    /**
     * Força o próximo processamento de pagamento a falhar com a mensagem especificada.
     *
     * @param message Mensagem de erro descritiva da falha induzida.
     */
    fun induceFailure(message: String = "Transação negada pela operadora de cartão") {
        shouldFailNext = true
        nextErrorMessage = message
    }

    /**
     * Restaura o comportamento padrão onde os pagamentos são aprovados com sucesso.
     */
    fun reset() {
        shouldFailNext = false
        nextErrorMessage = null
    }

    /**
     * Simula o processamento de uma transação financeira.
     *
     * @param amount Valor total em reais da transação.
     * @param method Forma de pagamento selecionada.
     * @return [FakePaymentResult] contendo o status de aprovação ou falha.
     */
    suspend fun processPayment(amount: Double, method: FakePaymentMethod): FakePaymentResult {
        if (shouldFailNext) {
            val error = nextErrorMessage ?: "Falha genérica no gateway de pagamento"
            reset()
            return FakePaymentResult(
                transactionId = "tx_err_${System.currentTimeMillis()}",
                isSuccess = false,
                errorMessage = error
            )
        }
        return FakePaymentResult(
            transactionId = "tx_ok_${System.currentTimeMillis()}",
            isSuccess = true
        )
    }
}
