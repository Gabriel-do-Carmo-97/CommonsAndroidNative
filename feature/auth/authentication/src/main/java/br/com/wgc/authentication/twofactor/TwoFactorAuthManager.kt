package br.com.wgc.authentication.twofactor

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.random.Random

/**
 * Canal de mensageria selecionado para despacho do token de verificação em duas etapas.
 */
enum class TwoFactorDeliveryChannel(val label: String) {
    /** Envio via SMS tradicional para o número de telefone celular cadastrado. */
    SMS("SMS"),
    /** Envio instantâneo via mensagem direta no WhatsApp oficial do usuário. */
    WHATSAPP("WhatsApp"),
    /** Envio via mensagem de correio eletrônico corporativo/pessoal. */
    EMAIL("E-mail")
}

/**
 * Desafio de verificação de dois fatores gerado para autenticação de login ou transação crítica.
 *
 * @property challengeId Identificador único do desafio de segurança (UUID).
 * @property userId Identificador do usuário solicitante.
 * @property channel Canal de entrega ([TwoFactorDeliveryChannel]).
 * @property destinationMasked Destino ofuscado por privacidade (ex: "(11) *****-4321" ou "g***@gmail.com").
 * @property expectedCode Código numérico de 6 dígitos gerado.
 * @property expiresAtMillis Carimbo de expiração do código em milissegundos (5 minutos).
 * @property remainingAttempts Quantidade de tentativas incorretas restantes antes do bloqueio do desafio.
 * @property nextResendAllowedAtMillis Carimbo a partir do qual um novo envio é permitido (cooldown de 60s).
 */
data class OtpChallenge(
    val challengeId: String,
    val userId: String,
    val channel: TwoFactorDeliveryChannel,
    val destinationMasked: String,
    val expectedCode: String,
    val expiresAtMillis: Long,
    val remainingAttempts: Int = 3,
    val nextResendAllowedAtMillis: Long
)

/**
 * Resultado da tentativa de validação do código OTP de 6 dígitos submetido pelo usuário.
 */
sealed interface OtpVerificationOutcome {
    /** Código conferido com sucesso; autenticação 2FA concluída. */
    data class Success(val challengeId: String, val verifiedAt: Long) : OtpVerificationOutcome
    /** Código incorreto com informação de tentativas restantes. */
    data class InvalidCode(val attemptsRemaining: Int) : OtpVerificationOutcome
    /** Prazo de validade do código expirado (requer novo envio). */
    data object Expired : OtpVerificationOutcome
    /** Limite de tentativas consecutivas incorretas esgotado; desafio invalidado. */
    data object MaxAttemptsExceeded : OtpVerificationOutcome
}

/**
 * Resultado do pedido de reenvio de código OTP com controle de taxa (rate limiting).
 */
sealed interface OtpResendOutcome {
    /** Novo desafio emitido e despachado. */
    data class Resent(val newChallenge: OtpChallenge) : OtpResendOutcome
    /** Cooldown temporizado ativo; o usuário deve aguardar antes de solicitar novamente. */
    data class CooldownActive(val remainingSeconds: Int) : OtpResendOutcome
    /** Desafio original não localizado. */
    data object ChallengeNotFound : OtpResendOutcome
}

/**
 * Gerenciador corporativo de Autenticação em Duas Etapas (2FA / OTP) de 6 Dígitos.
 *
 * Orquestra o ciclo de vida completo de tokens temporizados (TOTP/HOTP), incluindo geração de
 * códigos numéricos de alta entropia, ofuscação de destino para conformidade LGPD, controle de
 * intervalo de reenvio (cooldown de 60 segundos) e limite estrito de 3 tentativas incorretas.
 *
 * ### 📌 Quando Usar:
 * Utilize no fluxo de autenticação e login (:feature:auth:authentication) para login com duplo fator,
 * autorização de troca de senha ou confirmação de transferências financeiras de alto valor.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val twoFactorManager = TwoFactorAuthManager()
 *
 * // 1. Solicitar desafio OTP via WhatsApp:
 * val challenge = twoFactorManager.requestChallenge(
 *     userId = "usr-12",
 *     channel = TwoFactorDeliveryChannel.WHATSAPP,
 *     rawDestination = "11987654321"
 * )
 *
 * // 2. Validar código digitado pelo usuário:
 * val outcome = twoFactorManager.verifyOtp(challenge.challengeId, codeEntered = "849201")
 * ```
 */
@Singleton
class TwoFactorAuthManager @Inject constructor() {

    companion object {
        const val OTP_LENGTH = 6
        const val EXPIRATION_DURATION_MS = 5 * 60 * 1000L // 5 minutos
        const val RESEND_COOLDOWN_MS = 60 * 1000L // 60 segundos
        const val MAX_ATTEMPTS = 3
    }

    private val challenges = mutableMapOf<String, OtpChallenge>()

    /**
     * Cria e registra um novo desafio de autenticação em duas etapas.
     *
     * @param userId Identificador do titular da conta.
     * @param channel Canal de entrega do código.
     * @param rawDestination Número telefônico ou e-mail bruto.
     * @param currentTimeMillis Carimbo de hora da solicitação em milissegundos.
     * @return [OtpChallenge] configurado com expiração e código de 6 dígitos.
     */
    fun requestChallenge(
        userId: String,
        channel: TwoFactorDeliveryChannel,
        rawDestination: String,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): OtpChallenge {
        val code = generateSixDigitCode()
        val masked = maskDestination(rawDestination, channel)
        val challenge = OtpChallenge(
            challengeId = UUID.randomUUID().toString(),
            userId = userId,
            channel = channel,
            destinationMasked = masked,
            expectedCode = code,
            expiresAtMillis = currentTimeMillis + EXPIRATION_DURATION_MS,
            remainingAttempts = MAX_ATTEMPTS,
            nextResendAllowedAtMillis = currentTimeMillis + RESEND_COOLDOWN_MS
        )

        challenges[challenge.challengeId] = challenge
        return challenge
    }

    /**
     * Valida o código numérico submetido contra o desafio cadastrado.
     *
     * @param challengeId Identificador do desafio.
     * @param codeEntered Código de 6 dígitos digitado pelo usuário.
     * @param currentTimeMillis Carimbo de hora da validação.
     * @return [OtpVerificationOutcome] correspondente à resolução da tentativa.
     */
    fun verifyOtp(
        challengeId: String,
        codeEntered: String,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): OtpVerificationOutcome {
        val challenge = challenges[challengeId] ?: return OtpVerificationOutcome.Expired

        if (challenge.remainingAttempts <= 0) {
            challenges.remove(challengeId)
            return OtpVerificationOutcome.MaxAttemptsExceeded
        }

        if (currentTimeMillis > challenge.expiresAtMillis) {
            challenges.remove(challengeId)
            return OtpVerificationOutcome.Expired
        }

        val sanitizedEntered = codeEntered.trim()
        if (sanitizedEntered == challenge.expectedCode) {
            challenges.remove(challengeId)
            return OtpVerificationOutcome.Success(
                challengeId = challengeId,
                verifiedAt = currentTimeMillis
            )
        }

        val updatedAttempts = challenge.remainingAttempts - 1
        if (updatedAttempts <= 0) {
            challenges.remove(challengeId)
            return OtpVerificationOutcome.MaxAttemptsExceeded
        }

        challenges[challengeId] = challenge.copy(remainingAttempts = updatedAttempts)
        return OtpVerificationOutcome.InvalidCode(attemptsRemaining = updatedAttempts)
    }

    /**
     * Solicita o reenvio temporizado do código OTP respeitando a janela de cooldown de 60 segundos.
     *
     * @param challengeId Identificador do desafio original.
     * @param currentTimeMillis Carimbo de hora do pedido de reenvio.
     * @return [OtpResendOutcome] indicando sucesso ou tempo de espera restante.
     */
    fun resendOtp(
        challengeId: String,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): OtpResendOutcome {
        val existing = challenges[challengeId] ?: return OtpResendOutcome.ChallengeNotFound

        if (currentTimeMillis < existing.nextResendAllowedAtMillis) {
            val remainingSecs = max(1, ((existing.nextResendAllowedAtMillis - currentTimeMillis) / 1000L).toInt())
            return OtpResendOutcome.CooldownActive(remainingSeconds = remainingSecs)
        }

        // Gera novo código e renova as janelas de expiração
        val newCode = generateSixDigitCode()
        val newChallenge = existing.copy(
            expectedCode = newCode,
            expiresAtMillis = currentTimeMillis + EXPIRATION_DURATION_MS,
            remainingAttempts = MAX_ATTEMPTS,
            nextResendAllowedAtMillis = currentTimeMillis + RESEND_COOLDOWN_MS
        )

        challenges[challengeId] = newChallenge
        return OtpResendOutcome.Resent(newChallenge = newChallenge)
    }

    private fun generateSixDigitCode(): String {
        val number = Random.nextInt(100000, 999999)
        return number.toString()
    }

    private fun maskDestination(destination: String, channel: TwoFactorDeliveryChannel): String {
        return when (channel) {
            TwoFactorDeliveryChannel.SMS, TwoFactorDeliveryChannel.WHATSAPP -> {
                val digits = destination.filter { it.isDigit() }
                if (digits.length >= 8) {
                    val lastFour = digits.takeLast(4)
                    "(**) *****-$lastFour"
                } else {
                    "*****"
                }
            }
            TwoFactorDeliveryChannel.EMAIL -> {
                val parts = destination.split("@")
                if (parts.size == 2 && parts[0].isNotEmpty()) {
                    val firstChar = parts[0].first()
                    "$firstChar***@${parts[1]}"
                } else {
                    "***@***"
                }
            }
        }
    }
}
