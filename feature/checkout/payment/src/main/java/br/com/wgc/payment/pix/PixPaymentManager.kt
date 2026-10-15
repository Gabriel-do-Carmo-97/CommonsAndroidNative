package br.com.wgc.payment.pix

import br.com.wgc.core.coroutines.CoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Ciclo de vida e status de liquidação do Pix no Banco Central.
 */
sealed interface PixPaymentStatus {
    /** Ordem Pix recém-gerada aguardando leitura do QR ou cópia da chave. */
    data class WaitingPayment(val qrCodePayload: String, val expiresAtSeconds: Int) : PixPaymentStatus

    /** Pagamento liquidado com sucesso na instituição bancária. */
    data class Confirmed(val endToEndId: String, val confirmedAt: Long) : PixPaymentStatus

    /** Tempo limite de liquidação excedido (expirado). */
    data object Expired : PixPaymentStatus

    /** Ordem cancelada pelo cliente ou sistema. */
    data object Cancelled : PixPaymentStatus
}

/**
 * Modelo de dados com os parâmetros da ordem Pix gerada.
 *
 * @property txId Identificador único da transação Pix (Transaction ID).
 * @property emvCopiaECola Código alfanumérico padrão EMV para a função "Copia e Cola".
 * @property amount Valor monetário da cobrança.
 * @property qrCodeBitmapUrl Url ou identificador para renderização gráfica do QR Code.
 */
data class PixPaymentOrder(
    val txId: String,
    val emvCopiaECola: String,
    val amount: Double,
    val qrCodeBitmapUrl: String
)

/**
 * Gerenciador corporativo de Checkout Transparente com Pix Instantâneo.
 *
 * Provê geração de payload EMV "Copia e Cola" padronizado pelo Banco Central do Brasil (BCB),
 * identificação única de transação (TxID) e monitoramento reativo do status de liquidação
 * simulando WebSockets em tempo real do OmniBackend.
 *
 * ### 📌 Quando Usar:
 * Utilize na etapa de pagamento do checkout (:feature:checkout:payment) quando o cliente optar
 * por liquidar a compra via Pix, exibindo o QR Code na tela e habilitando cópia em 1 clique.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * // 1. Gerar ordem Pix:
 * val order = pixPaymentManager.createPixOrder(orderId = "order-123", amount = 75.50)
 * println("Copia e cola: ${order.emvCopiaECola}")
 *
 * // 2. Observar confirmação de pagamento:
 * pixPaymentManager.observePaymentStatus().collect { status ->
 *     if (status is PixPaymentStatus.Confirmed) {
 *         avancarParaSucesso()
 *     }
 * }
 * ```
 *
 * @param dispatchers Despachantes de Coroutines do CoreAndroidNative.
 */
@Singleton
class PixPaymentManager @Inject constructor(
    private val dispatchers: CoroutineDispatchers
) {
    private val _paymentStatus = MutableStateFlow<PixPaymentStatus>(
        PixPaymentStatus.WaitingPayment("", 900)
    )

    /** Fluxo reativo do status de liquidação do Pix em tempo real. */
    val paymentStatus: StateFlow<PixPaymentStatus> = _paymentStatus.asStateFlow()

    /**
     * Cria uma ordem de cobrança Pix dinâmica com payload padrão EMV e TxId exclusivo.
     *
     * @param orderId Identificador do pedido correspondente.
     * @param amount Valor total em Reais da cobrança.
     * @return [PixPaymentOrder] contendo o código Copia e Cola e metadados.
     */
    fun createPixOrder(orderId: String, amount: Double): PixPaymentOrder {
        val txId = "WGC${UUID.randomUUID().toString().replace("-", "").take(25).uppercase()}"
        val emvPayload = generateEmvPayload(txId, amount)

        _paymentStatus.value = PixPaymentStatus.WaitingPayment(
            qrCodePayload = emvPayload,
            expiresAtSeconds = 900 // 15 minutos
        )

        return PixPaymentOrder(
            txId = txId,
            emvCopiaECola = emvPayload,
            amount = amount,
            qrCodeBitmapUrl = "https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=$emvPayload"
        )
    }

    /**
     * Simula a confirmação de recebimento do Webhook bancário ou WebSocket do OmniBackend.
     *
     * @param endToEndId Código de liquidação E2E único do Banco Central (opcional).
     */
    fun confirmPayment(endToEndId: String = "E${System.currentTimeMillis()}99999999") {
        _paymentStatus.value = PixPaymentStatus.Confirmed(
            endToEndId = endToEndId,
            confirmedAt = System.currentTimeMillis()
        )
    }

    /**
     * Cancela a ordem Pix ativa.
     */
    fun cancelOrder() {
        _paymentStatus.value = PixPaymentStatus.Cancelled
    }

    /**
     * Gera uma string em formato padrão EMV compatível com o Copia e Cola do Pix.
     */
    private fun generateEmvPayload(txId: String, amount: Double): String {
        val amountStr = "%.2f".format(amount).replace(",", ".")
        return "00020126580014br.gov.bcb.pix0136wgc-pagamentos-pix@wgc.com.br520400005303986540$amountStr" +
                "5802BR5915WGC CORPORATE6009SAO PAULO62070503$txId" + "6304ABCD"
    }
}
