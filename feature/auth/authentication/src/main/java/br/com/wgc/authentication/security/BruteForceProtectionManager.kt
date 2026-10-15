package br.com.wgc.authentication.security

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

/**
 * Resultado da auditoria de bloqueio contra ataques de força bruta.
 */
sealed interface LockoutCheckResult {
    /**
     * Tentativa permitida para validação de credenciais.
     *
     * @property consecutiveFailures Quantidade de erros acumulados.
     * @property attemptsUntilNextLockout Tentativas restantes antes do próximo bloqueio temporário.
     */
    data class Allowed(val consecutiveFailures: Int, val attemptsUntilNextLockout: Int) : LockoutCheckResult

    /**
     * Acesso temporariamente suspenso devido a excesso de tentativas incorretas.
     *
     * @property remainingLockoutSeconds Segundos restantes de penalidade antes da liberação.
     * @property lockedUntilMillis Carimbo de data/hora do término do bloqueio.
     * @property totalFailures Total de falhas consecutivas registradas.
     */
    data class Locked(
        val remainingLockoutSeconds: Int,
        val lockedUntilMillis: Long,
        val totalFailures: Int
    ) : LockoutCheckResult
}

private data class LoginAttemptRecord(
    val failureCount: Int,
    val lockedUntilMillis: Long = 0L
)

/**
 * Gerenciador corporativo de Prevenção contra Ataques de Força Bruta e Bloqueio Progressivo.
 *
 * Aplica penalidades exponenciais/progressivas de tempo de espera com base nas tentativas consecutivas
 * de login malsucedidas por conta/usuário:
 * - 3 falhas consecutivas: 30 segundos de suspensão.
 * - 5 falhas consecutivas: 5 minutos de suspensão.
 * - 10 ou mais falhas: 30 minutos de suspensão.
 *
 * ### 📌 Quando Usar:
 * Acione no fluxo de login de credenciais (:feature:auth:authentication) antes de disparar
 * requisições de autenticação ao OmniBackend para mitigar ataques de dicionário e credenciais vazadas (Credential Stuffing).
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val bruteForceManager = BruteForceProtectionManager()
 *
 * // 1. Verificar se a conta está suspensa antes de autenticar:
 * val check = bruteForceManager.checkLockout("user@email.com")
 * if (check is LockoutCheckResult.Locked) {
 *     exibirAlerta("Conta bloqueada temporariamente. Aguarde ${check.remainingLockoutSeconds}s.")
 *     return
 * }
 *
 * // 2. Ao falhar autenticação no backend:
 * bruteForceManager.recordFailedAttempt("user@email.com")
 *
 * // 3. Ao autenticar com sucesso:
 * bruteForceManager.recordSuccessfulLogin("user@email.com")
 * ```
 */
@Singleton
class BruteForceProtectionManager @Inject constructor() {

    private val attemptRecords = mutableMapOf<String, LoginAttemptRecord>()

    /**
     * Verifica se o identificador do usuário encontra-se bloqueado no momento da requisição.
     *
     * @param identifier E-mail, CPF ou identificador único do usuário.
     * @param currentTimeMillis Carimbo de data/hora atual em milissegundos.
     * @return [LockoutCheckResult] indicando se o acesso está liberado ou suspenso com tempo restante.
     */
    fun checkLockout(
        identifier: String,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): LockoutCheckResult {
        val record = attemptRecords[identifier.lowercase()] ?: return LockoutCheckResult.Allowed(0, 3)

        if (currentTimeMillis < record.lockedUntilMillis) {
            val remainingSecs = max(1, ((record.lockedUntilMillis - currentTimeMillis) / 1000L).toInt())
            return LockoutCheckResult.Locked(
                remainingLockoutSeconds = remainingSecs,
                lockedUntilMillis = record.lockedUntilMillis,
                totalFailures = record.failureCount
            )
        }

        val remainingBeforeLock = when {
            record.failureCount < 3 -> 3 - record.failureCount
            record.failureCount < 5 -> 5 - record.failureCount
            else -> 1
        }

        return LockoutCheckResult.Allowed(
            consecutiveFailures = record.failureCount,
            attemptsUntilNextLockout = remainingBeforeLock
        )
    }

    /**
     * Registra uma falha de autenticação e aplica o bloqueio progressivo caso atinja os limiares.
     *
     * @param identifier E-mail ou identificador do usuário.
     * @param currentTimeMillis Carimbo de data/hora da ocorrência.
     * @return [LockoutCheckResult] resultante após o registro da falha.
     */
    fun recordFailedAttempt(
        identifier: String,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): LockoutCheckResult {
        val key = identifier.lowercase()
        val current = attemptRecords[key] ?: LoginAttemptRecord(0)
        val newFailureCount = current.failureCount + 1

        val lockoutDurationMs = when {
            newFailureCount >= 10 -> 30 * 60 * 1000L // 30 minutos
            newFailureCount >= 5 -> 5 * 60 * 1000L   // 5 minutos
            newFailureCount >= 3 -> 30 * 1000L        // 30 segundos
            else -> 0L
        }

        val lockedUntil = if (lockoutDurationMs > 0) currentTimeMillis + lockoutDurationMs else 0L
        val updated = LoginAttemptRecord(
            failureCount = newFailureCount,
            lockedUntilMillis = lockedUntil
        )
        attemptRecords[key] = updated

        return if (lockoutDurationMs > 0) {
            LockoutCheckResult.Locked(
                remainingLockoutSeconds = (lockoutDurationMs / 1000L).toInt(),
                lockedUntilMillis = lockedUntil,
                totalFailures = newFailureCount
            )
        } else {
            LockoutCheckResult.Allowed(
                consecutiveFailures = newFailureCount,
                attemptsUntilNextLockout = 3 - newFailureCount
            )
        }
    }

    /**
     * Reseta integralmente os contadores de falhas após um login bem-sucedido.
     *
     * @param identifier E-mail ou identificador autenticado.
     */
    fun recordSuccessfulLogin(identifier: String) {
        attemptRecords.remove(identifier.lowercase())
    }

    /**
     * Limpa todos os registros de tentativas e bloqueios ativos em memória.
     */
    fun clearAllRecords() {
        attemptRecords.clear()
    }
}
