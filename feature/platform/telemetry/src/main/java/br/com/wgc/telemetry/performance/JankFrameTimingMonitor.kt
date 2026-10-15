package br.com.wgc.telemetry.performance

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Classificação da estabilidade de renderização de um quadro gráfico (Frame).
 */
enum class FrameClassification {
    /** Quadro renderizado dentro da janela alvo de 60fps (<= 16.6ms) ou 120fps (<= 8.3ms). */
    SMOOTH,
    /** Quadro engasgado (Jank) que excedeu o tempo de sincronização vertical (16.6ms a 700ms). */
    JANK,
    /** Quadro congelado (Frozen Frame) com duração extrema (> 700ms), gerando percepção de travamento. */
    FROZEN
}

/**
 * Relatório consolidado de desempenho e estabilidade de taxas de quadros (Jank Stats).
 *
 * @property totalFrames Total de quadros renderizados durante a medição.
 * @property jankFrames Quantidade de quadros lentos detectados (> 16.6ms).
 * @property frozenFrames Quantidade de quadros congelados detectados (> 700ms).
 * @property jankPercentage Percentual de quadros engasgados (0.0% a 100.0%).
 * @property frozenPercentage Percentual de quadros congelados (0.0% a 100.0%).
 * @property isVitalsCompliant Indica se a tela atende aos limites exigidos pelo Google Play Vitals (Jank < 5% e Frozen < 0.1%).
 */
data class FramePerformanceReport(
    val totalFrames: Long,
    val jankFrames: Long,
    val frozenFrames: Long,
    val jankPercentage: Float,
    val frozenPercentage: Float,
    val isVitalsCompliant: Boolean
)

/**
 * Monitor corporativo de tempo de quadros e estabilidade de renderização gráfica (Jank Stats).
 *
 * Coleta métricas de latência de exibição de frames em listas complexas (Catálogo, Checkout, Mapas),
 * computando o percentual de Jank e Frozen Frames conforme as metas de qualidade do Google Play Vitals.
 *
 * ### 📌 Quando Usar:
 * - Em testes de carga e scroll de telas (`LazyColumn`, `LazyVerticalGrid`).
 * - Durante navegação contínua nos módulos de vitrine comercial (`:feature:storefront:catalog`).
 * - Em pipelines de regressão de performance e benchmark.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val monitor = JankFrameTimingMonitor()
 * monitor.recordFrame(durationMillis = 12.0) // smooth
 * monitor.recordFrame(durationMillis = 35.0) // jank
 *
 * val report = monitor.generateReport()
 * println("Jank rate: ${report.jankPercentage}% - Conforme: ${report.isVitalsCompliant}")
 * ```
 */
@Singleton
class JankFrameTimingMonitor @Inject constructor() {

    companion object {
        /** Limite de duração para considerar um frame fluido a 60 FPS (16.6ms). */
        const val JANK_THRESHOLD_MS = 16.6
        /** Limite de duração para classificar um frame como congelado (700ms). */
        const val FROZEN_THRESHOLD_MS = 700.0
        /** Meta máxima permitida de Jank pelo Google Play Vitals (5.0%). */
        const val MAX_ALLOWED_JANK_PERCENT = 5.0f
        /** Meta máxima permitida de Frozen Frames pelo Google Play Vitals (0.1%). */
        const val MAX_ALLOWED_FROZEN_PERCENT = 0.1f
    }

    private var totalFramesCount = 0L
    private var jankFramesCount = 0L
    private var frozenFramesCount = 0L

    /**
     * Registra o tempo de renderização de um quadro medido pelo `JankStats` ou Choreographer.
     *
     * @param durationMillis Tempo total consumido pelo frame em milissegundos.
     * @return [FrameClassification] correspondente à duração informada.
     */
    @Synchronized
    fun recordFrame(durationMillis: Double): FrameClassification {
        totalFramesCount++

        val classification = when {
            durationMillis > FROZEN_THRESHOLD_MS -> {
                frozenFramesCount++
                jankFramesCount++
                FrameClassification.FROZEN
            }
            durationMillis > JANK_THRESHOLD_MS -> {
                jankFramesCount++
                FrameClassification.JANK
            }
            else -> {
                FrameClassification.SMOOTH
            }
        }

        return classification
    }

    /**
     * Computa as taxas acumuladas de estabilidade e gera o [FramePerformanceReport].
     *
     * @return [FramePerformanceReport] consolidado.
     */
    @Synchronized
    fun generateReport(): FramePerformanceReport {
        if (totalFramesCount == 0L) {
            return FramePerformanceReport(
                totalFrames = 0L,
                jankFrames = 0L,
                frozenFrames = 0L,
                jankPercentage = 0f,
                frozenPercentage = 0f,
                isVitalsCompliant = true
            )
        }

        val jankPercent = (jankFramesCount.toFloat() / totalFramesCount.toFloat()) * 100f
        val frozenPercent = (frozenFramesCount.toFloat() / totalFramesCount.toFloat()) * 100f
        val compliant = jankPercent <= MAX_ALLOWED_JANK_PERCENT && frozenPercent <= MAX_ALLOWED_FROZEN_PERCENT

        return FramePerformanceReport(
            totalFrames = totalFramesCount,
            jankFrames = jankFramesCount,
            frozenFrames = frozenFramesCount,
            jankPercentage = jankPercent,
            frozenPercentage = frozenPercent,
            isVitalsCompliant = compliant
        )
    }

    /**
     * Redefine os contadores para início de uma nova sessão de medição.
     */
    @Synchronized
    fun reset() {
        totalFramesCount = 0L
        jankFramesCount = 0L
        frozenFramesCount = 0L
    }
}
