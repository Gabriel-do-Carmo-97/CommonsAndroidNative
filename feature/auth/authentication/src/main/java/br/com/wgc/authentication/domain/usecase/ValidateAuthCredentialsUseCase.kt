package br.com.wgc.authentication.domain.usecase

import java.util.regex.Pattern
import javax.inject.Inject

/**
 * Resultado da validação de credenciais de acesso corporativas.
 */
sealed class CredentialsValidationResult {
    /** Credenciais aprovadas e em conformidade com as políticas de segurança. */
    data object Valid : CredentialsValidationResult()

    /**
     * E-mail informado não atende ao padrão RFC 5322.
     *
     * @property message Motivo amigável da rejeição do e-mail.
     */
    data class InvalidEmail(val message: String) : CredentialsValidationResult()

    /**
     * Senha não atende aos requisitos mínimos de entropia e complexidade.
     *
     * @property message Motivo amigável da rejeição da senha.
     */
    data class WeakPassword(val message: String) : CredentialsValidationResult()
}

/**
 * Caso de uso corporativo para validação rigorosa de credenciais de autenticação e registro.
 *
 * Aplica políticas corporativas de segurança da informação:
 * - Validação de formato de e-mail compatível com padrão RFC.
 * - Comprimento mínimo de senha (8 caracteres).
 * - Exigência de composição combinada (pelo menos uma letra e pelo menos um dígito numérico).
 *
 * ### 📌 Quando Usar:
 * - Na tela de Login (`LoginWGCViewModel`) antes de disparar chamada de rede ao OmniBackend.
 * - Na tela de Criação de Conta (`RegisterUserViewModel`) para fornecer feedback instantâneo ao usuário.
 * - Na tela de Redefinição de Senha (`ForgotPasswordViewModel`).
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val validator = ValidateAuthCredentialsUseCase()
 * val result = validator("usuario@dominio.com.br", "SenhaForte123")
 * if (result is CredentialsValidationResult.Valid) {
 *     // prosseguir com login
 * }
 * ```
 */
class ValidateAuthCredentialsUseCase @Inject constructor() {

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
        private val EMAIL_REGEX = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
        )
    }

    /**
     * Valida o e-mail e a senha informados contra as políticas de conformidade.
     *
     * @param email Endereço de e-mail do usuário.
     * @param password Senha em texto plano a ser auditada.
     * @return [CredentialsValidationResult] indicando se os campos são válidos ou o detalhe da rejeição.
     */
    operator fun invoke(email: String?, password: String?): CredentialsValidationResult {
        if (email.isNullOrBlank() || !EMAIL_REGEX.matcher(email.trim()).matches()) {
            return CredentialsValidationResult.InvalidEmail("Informe um endereço de e-mail válido.")
        }

        if (password.isNullOrBlank()) {
            return CredentialsValidationResult.WeakPassword("A senha não pode estar em branco.")
        }

        if (password.length < MIN_PASSWORD_LENGTH) {
            return CredentialsValidationResult.WeakPassword("A senha deve conter no mínimo $MIN_PASSWORD_LENGTH caracteres.")
        }

        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }

        if (!hasLetter || !hasDigit) {
            return CredentialsValidationResult.WeakPassword("A senha deve conter pelo menos uma letra e um número.")
        }

        return CredentialsValidationResult.Valid
    }
}
