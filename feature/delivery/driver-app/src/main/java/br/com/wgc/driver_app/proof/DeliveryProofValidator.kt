package br.com.wgc.driver_app.proof

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Modalidade de comprovação exigida para finalização e liquidação da entrega.
 */
enum class DeliveryProofType {
    /** Exige apenas o código numérico OTP de 4 dígitos informado pelo destinatário. */
    OTP_CODE,
    /** Exige foto nítida do pacote entregue no local ou portaria. */
    PHOTO_CONFIRMATION,
    /** Exige dupla validação: código OTP e fotografia do pacote. */
    BOTH
}

/**
 * Dados submetidos pelo motorista ao concluir a entrega no destino.
 *
 * @property orderId Identificador do pedido entregue.
 * @property otpEntered Código OTP informado pelo recebedor (ou null se não aplicável).
 * @property photoFilePath Caminho local da fotografia capturada da entrega.
 * @property recipientName Nome ou identificação da pessoa que recebeu a mercadoria.
 * @property submittedAt Carimbo de data/hora da submissão em milissegundos.
 */
data class DeliveryProofSubmission(
    val orderId: String,
    val otpEntered: String? = null,
    val photoFilePath: String? = null,
    val recipientName: String,
    val submittedAt: Long = System.currentTimeMillis()
)

/**
 * Resultado da auditoria e validação de comprovação de entrega.
 */
sealed interface ProofValidationResult {
    /**
     * Validação bem-sucedida; pedido apto para encerramento e liquidação financeira.
     *
     * @property orderId Identificador do pedido validado.
     * @property validatedAt Carimbo de validação.
     */
    data class Success(val orderId: String, val validatedAt: Long) : ProofValidationResult

    /**
     * Validação rejeitada devido a inconformidade de dados ou fraude.
     *
     * @property reason Motivo claro em linguagem natural explicando o erro.
     * @property failedField Campo específico reprovado (ex: "otp", "photo", "recipient").
     */
    data class Failure(val reason: String, val failedField: String) : ProofValidationResult
}

/**
 * Validador corporativo de Prova de Entrega (POD - Proof of Delivery) via Foto e Código OTP.
 *
 * Audita o encerramento de pedidos no aplicativo do motorista parceiro, garantindo que o código
 * de segurança de 4 dígitos ou a foto do pacote satisfaçam os requisitos operacionais antes da liberação do saldo.
 *
 * ### 📌 Quando Usar:
 * Utilize na tela de encerramento de corrida ([br.com.wgc.driver_app.screen.DriverAppScreen]) antes de
 * submeter o evento `STATUS_DELIVERED` ao OmniBackend.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val validator = DeliveryProofValidator()
 * val submission = DeliveryProofSubmission(
 *     orderId = "ord-99",
 *     otpEntered = "4821",
 *     recipientName = "Dona Maria"
 * )
 *
 * val result = validator.validateProof(
 *     expectedOtp = "4821",
 *     requiredType = DeliveryProofType.OTP_CODE,
 *     submission = submission
 * )
 *
 * when (result) {
 *     is ProofValidationResult.Success -> { finalizarPedido() }
 *     is ProofValidationResult.Failure -> { exibirAlerta(result.reason) }
 * }
 * ```
 */
@Singleton
class DeliveryProofValidator @Inject constructor() {

    private val validPhotoExtensions = setOf("jpg", "jpeg", "png")

    /**
     * Valida os comprovantes de entrega submetidos em conformidade com as exigências da modalidade.
     *
     * @param expectedOtp Código de 4 dígitos emitido na finalização da compra pelo consumidor.
     * @param requiredType Nível de exigência de comprovação ([DeliveryProofType]).
     * @param submission Dados coletados pelo entregador.
     * @return [ProofValidationResult.Success] se validado ou [ProofValidationResult.Failure] com o motivo.
     */
    fun validateProof(
        expectedOtp: String?,
        requiredType: DeliveryProofType,
        submission: DeliveryProofSubmission
    ): ProofValidationResult {
        // 1. Validação de nome do destinatário/recebedor
        if (submission.recipientName.trim().length < 2) {
            return ProofValidationResult.Failure(
                reason = "Informe o nome ou parentesco da pessoa que recebeu o pacote.",
                failedField = "recipient"
            )
        }

        // 2. Validação de OTP (se aplicável)
        if (requiredType == DeliveryProofType.OTP_CODE || requiredType == DeliveryProofType.BOTH) {
            val entered = submission.otpEntered?.trim() ?: ""
            if (entered.length != 4 || !entered.all { it.isDigit() }) {
                return ProofValidationResult.Failure(
                    reason = "O código de confirmação deve conter exatamente 4 dígitos numéricos.",
                    failedField = "otp"
                )
            }
            if (expectedOtp != null && entered != expectedOtp.trim()) {
                return ProofValidationResult.Failure(
                    reason = "Código de confirmação incorreto. Solicite o código de 4 dígitos ao destinatário.",
                    failedField = "otp"
                )
            }
        }

        // 3. Validação de Fotografia (se aplicável)
        if (requiredType == DeliveryProofType.PHOTO_CONFIRMATION || requiredType == DeliveryProofType.BOTH) {
            val path = submission.photoFilePath?.trim() ?: ""
            if (path.isBlank()) {
                return ProofValidationResult.Failure(
                    reason = "É obrigatório anexar uma foto nítida da entrega realizada.",
                    failedField = "photo"
                )
            }
            val ext = path.substringAfterLast(".", "").lowercase()
            if (ext !in validPhotoExtensions) {
                return ProofValidationResult.Failure(
                    reason = "Formato de foto inválido. Os formatos suportados são: JPG e PNG.",
                    failedField = "photo"
                )
            }
        }

        return ProofValidationResult.Success(
            orderId = submission.orderId,
            validatedAt = submission.submittedAt
        )
    }
}
