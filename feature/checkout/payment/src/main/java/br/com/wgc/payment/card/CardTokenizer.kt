package br.com.wgc.payment.card

import java.security.MessageDigest
import java.time.YearMonth
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Bandeiras de cartão de crédito e débito identificadas pelo emissor.
 */
enum class CardBrand(val displayName: String, val expectedCvvLength: Int) {
    VISA("Visa", 3),
    MASTERCARD("Mastercard", 3),
    ELO("Elo", 3),
    AMEX("American Express", 4),
    HIPERCARD("Hipercard", 3),
    UNKNOWN("Desconhecida", 3)
}

/**
 * Resultado da tokenização segura de cartão em conformidade com PCI-DSS.
 */
sealed interface CardTokenResult {
    /**
     * Tokenização bem-sucedida com PAN mascarado e token gerado.
     *
     * @property token Token criptográfico descartável ou multi-uso.
     * @property brand Bandeira identificada através do BIN.
     * @property maskedPan Número do cartão mascarado (ex: "•••• 4242").
     */
    data class Success(
        val token: String,
        val brand: CardBrand,
        val maskedPan: String
    ) : CardTokenResult

    /**
     * Falha de validação ou dados incorretos no cartão.
     *
     * @property error Mensagem técnica ou de interface explicando o erro.
     */
    data class Failure(val error: String) : CardTokenResult
}

/**
 * Tokenizador corporativo de Cartão de Crédito e Débito com validação client-side PCI-DSS.
 *
 * Implementa validação do algoritmo de Luhn (Módulo 10), detecção de bandeira via BIN (Bank Identification Number),
 * checagem de vigência temporal (MM/AA) e conversão segura para Token criptográfico sem retenção
 * de dados sensíveis em memória volátil ou persistência local.
 *
 * ### 📌 Quando Usar:
 * Utilize na tela de pagamento (:feature:checkout:payment) antes de submeter a requisição de cobrança
 * para os gateways parceiros (Cielo, Stone, Rede, Stripe, Pagar.me).
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val tokenizer = CardTokenizer()
 * val result = tokenizer.tokenize(
 *     rawPan = "4111111111111111",
 *     holderName = "GABRIEL CARMO",
 *     expiryMonth = 12,
 *     expiryYear = 2030,
 *     cvv = "123"
 * )
 *
 * when (result) {
 *     is CardTokenResult.Success -> {
 *         println("Cartão tokenizado: ${result.maskedPan} - Token: ${result.token}")
 *     }
 *     is CardTokenResult.Failure -> {
 *         println("Erro: ${result.error}")
 *     }
 * }
 * ```
 */
@Singleton
class CardTokenizer @Inject constructor() {

    /**
     * Valida os campos do cartão e gera o token de transação segura.
     *
     * @param rawPan Número bruto do cartão (sem espaços ou traços).
     * @param holderName Nome do titular impresso no cartão.
     * @param expiryMonth Mês de vencimento (1 a 12).
     * @param expiryYear Ano de vencimento no formato YYYY (ex: 2028).
     * @param cvv Código de segurança do cartão (CVV/CVC).
     * @return [CardTokenResult] contendo o token ou mensagem de falha.
     */
    fun tokenize(
        rawPan: String,
        holderName: String,
        expiryMonth: Int,
        expiryYear: Int,
        cvv: String
    ): CardTokenResult {
        val cleanPan = rawPan.filter { it.isDigit() }

        if (cleanPan.length < 13 || cleanPan.length > 19) {
            return CardTokenResult.Failure("Número de cartão inválido. O tamanho deve conter entre 13 e 19 dígitos.")
        }

        if (!isValidLuhn(cleanPan)) {
            return CardTokenResult.Failure("Número de cartão inválido pelo algoritmo de Luhn (dígito verificador incorreto).")
        }

        val brand = detectBrand(cleanPan)

        if (cvv.length != brand.expectedCvvLength) {
            return CardTokenResult.Failure("Código de segurança (CVV) inválido para a bandeira ${brand.displayName}. Esperado ${brand.expectedCvvLength} dígitos.")
        }

        if (holderName.trim().length < 3) {
            return CardTokenResult.Failure("Nome do titular impresso no cartão é obrigatório.")
        }

        if (isExpired(expiryMonth, expiryYear)) {
            return CardTokenResult.Failure("Cartão vencido. Verifique a data de expiração informada.")
        }

        val maskedPan = "•••• •••• •••• " + cleanPan.takeLast(4)
        val token = generateHashToken(cleanPan, holderName, expiryMonth, expiryYear)

        return CardTokenResult.Success(
            token = token,
            brand = brand,
            maskedPan = maskedPan
        )
    }

    /**
     * Valida o dígito verificador do cartão de acordo com o algoritmo de Luhn (MOD 10).
     */
    fun isValidLuhn(number: String): Boolean {
        var checksum = 0
        var isSecond = false

        for (i in number.length - 1 downTo 0) {
            var digit = number[i].digitToIntOrNull() ?: return false

            if (isSecond) {
                digit *= 2
                if (digit > 9) {
                    digit -= 9
                }
            }

            checksum += digit
            isSecond = !isSecond
        }

        return checksum % 10 == 0
    }

    /**
     * Identifica a bandeira comercial do cartão através do prefixo numérico (BIN).
     */
    fun detectBrand(pan: String): CardBrand {
        return when {
            pan.startsWith("4") -> CardBrand.VISA
            pan.matches(Regex("^(5[1-5]|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720).*")) -> CardBrand.MASTERCARD
            pan.matches(Regex("^(4011|4389|5041|5067|5090|6362|6363).*")) -> CardBrand.ELO
            pan.startsWith("34") || pan.startsWith("37") -> CardBrand.AMEX
            pan.startsWith("606282") -> CardBrand.HIPERCARD
            else -> CardBrand.UNKNOWN
        }
    }

    private fun isExpired(month: Int, year: Int): Boolean {
        if (month !in 1..12) return true
        val now = YearMonth.now()
        val cardExpiry = YearMonth.of(year, month)
        return cardExpiry.isBefore(now)
    }

    private fun generateHashToken(pan: String, holder: String, month: Int, year: Int): String {
        val rawSeed = "$pan:$holder:$month:$year:${UUID.randomUUID()}"
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(rawSeed.toByteArray())
        return "tok_wgc_" + hash.joinToString("") { "%02x".format(it) }.take(32)
    }
}
