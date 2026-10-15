package br.com.wgc.telemetry.vitals

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Classificação de saúde operacional do aplicativo de acordo com os limites do Google Play Vitals.
 */
enum class VitalsHealthStatus {
    /** As taxas de falha e travamento estão estritamente dentro das metas de qualidade. */
    HEALTHY,
    /** Uma das taxas aproxima-se ou atingiu o limite de alerta da loja. */
    NEEDS_ATTENTION,
    /** As taxas de ANR ou Crash violaram o teto estipulado pelo Google Play Vitals. */
    CRITICAL
}

/**
 * Relatório consolidado das métricas de estabilidade do aplicativo.
 *
 * @property totalSessions Quantidade de sessões ativas de usuários registradas.
 * @property totalCrashes Quantidade de falhas fatais (Uncaught Exceptions).
 * @property totalAnrs Quantidade de travamentos de interface (Application Not Responding).
 * @property crashRatePercent Percentual de sessões com crash (Meta: < 1.09%).
 * @property anrRatePercent Percentual de sessões com ANR (Meta: < 0.47%).
 * @property healthStatus Classificação de conformidade com os requisitos da Google Play Store.
 */
data class AppVitalsReport(
    val totalSessions: Long,
    val totalCrashes: Long,
    val totalAnrs: Long,
    val crashRatePercent: Double,
    val anrRatePercent: Double,
    val healthStatus: VitalsHealthStatus
)

/**
 * Monitor corporativo de integridade de software e observabilidade dos limites do Google Play Vitals.
 *
 * Consolida métricas de estabilidade em tempo real para prevenir que o aplicativo sofra
 * rebaixamento de visibilidade orgânica na Google Play Store decorrente de índices elevados de ANR ou falhas.
 *
 * ### 📌 Quando Usar:
 * - Em rotinas de telemetria contínua e dashboards de integridade (`:feature:platform:telemetry`).
 * - Em pipelines de pré-lançamento ou monitoramento em produção conectado ao OmniBackend Analytics.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val monitor = AppVitalsMonitor()
 * monitor.recordSession()
 * // Em caso de falha:
 * monitor.recordCrash()
 *
 * val report = monitor.generateReport()
 * println("Saúde: ${report.healthStatus} - Crash rate: ${report.crashRatePercent}%")
 * ```
 */
@Singleton
class AppVitalsMonitor @Inject constructor() {

    companion object {
        /** Teto máximo de Crash Rate estipulado pelo Google Play Vitals (1.09%). */
        const val MAX_CRASH_RATE_PERCENT = 1.09
        /** Teto máximo de ANR Rate estipulado pelo Google Play Vitals (0.47%). */
        const val MAX_ANR_RATE_PERCENT = 0.47
    }

    private var sessionsCount = 0L
    private var crashesCount = 0L
    private var anrsCount = 0L

    /**
     * Incrementa o contador de sessões ativas iniciadas.
     */
    @Synchronized
    fun recordSession() {
        sessionsCount++
    }

    /**
     * Registra a ocorrência de uma falha catastrófica (Crash).
     */
    @Synchronized
    fun recordCrash() {
        crashesCount++
    }

    /**
     * Registra a ocorrência de um congelamento de interface (ANR).
     */
    @Synchronized
    fun recordAnr() {
        anrsCount++
    }

    /**
     * Calcula as taxas atuais e gera o [AppVitalsReport].
     *
     * @return [AppVitalsReport] com os percentuais calculados e o status geral.
     */
    @Synchronized
    fun generateReport(): AppVitalsReport {
        if (sessionsCount == 0L) {
            return AppVitalsReport(
                totalSessions = 0L,
                totalCrashes = crashesCount,
                totalAnrs = anrsCount,
                crashRatePercent = 0.0,
                anrRatePercent = 0.0,
                healthStatus = if (crashesCount > 0 || anrsCount > 0) VitalsHealthStatus.CRITICAL else VitalsHealthStatus.HEALTHY
            )
        }

        val crashRate = (crashesCount.toDouble() / sessionsCount.toDouble()) * 100.0
        val anrRate = (anrsCount.toDouble() / sessionsCount.toDouble()) * 100.0

        val status = when {
            crashRate >= MAX_CRASH_RATE_PERCENT || anrRate >= MAX_ANR_RATE_PERCENT -> {
                VitalsHealthStatus.CRITICAL
            }
            crashRate >= (MAX_CRASH_RATE_PERCENT * 0.75) || anrRate >= (MAX_ANR_RATE_PERCENT * 0.75) -> {
                VitalsHealthStatus.NEEDS_ATTENTION
            }
            else -> {
                VitalsHealthStatus.HEALTHY
            }
        }

        return AppVitalsReport(
            totalSessions = sessionsCount,
            totalCrashes = crashesCount,
            totalAnrs = anrsCount,
            crashRatePercent = crashRate,
            anrRatePercent = anrRate,
            healthStatus = status
        )
    }

    /**
     * Zera as contagens para um novo ciclo de telemetria.
     */
    @Synchronized
    fun reset() {
        sessionsCount = 0L
        crashesCount = 0L
        anrsCount = 0L
    }
}
