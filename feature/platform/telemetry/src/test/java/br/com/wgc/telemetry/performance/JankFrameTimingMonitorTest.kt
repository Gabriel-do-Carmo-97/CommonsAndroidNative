package br.com.wgc.telemetry.performance

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [JankFrameTimingMonitor].
 */
class JankFrameTimingMonitorTest {

    private lateinit var monitor: JankFrameTimingMonitor

    @Before
    fun setup() {
        monitor = JankFrameTimingMonitor()
    }

    @Test
    fun `recordFrame com frame rapido deve classificar como SMOOTH`() {
        val classification = monitor.recordFrame(10.0)
        assertEquals(FrameClassification.SMOOTH, classification)
    }

    @Test
    fun `recordFrame com frame lento deve classificar como JANK`() {
        val classification = monitor.recordFrame(25.0)
        assertEquals(FrameClassification.JANK, classification)
    }

    @Test
    fun `recordFrame com frame extremo deve classificar como FROZEN`() {
        val classification = monitor.recordFrame(850.0)
        assertEquals(FrameClassification.FROZEN, classification)
    }

    @Test
    fun `generateReport sem frames deve retornar conforme`() {
        val report = monitor.generateReport()
        assertEquals(0L, report.totalFrames)
        assertTrue(report.isVitalsCompliant)
    }

    @Test
    fun `generateReport com taxa alta de jank deve reprovar conformidade`() {
        // 90 frames suaves, 10 frames jank (10% de jank > 5% permitido)
        repeat(90) { monitor.recordFrame(12.0) }
        repeat(10) { monitor.recordFrame(30.0) }

        val report = monitor.generateReport()
        assertEquals(100L, report.totalFrames)
        assertEquals(10L, report.jankFrames)
        assertEquals(10f, report.jankPercentage, 0.01f)
        assertFalse(report.isVitalsCompliant)
    }

    @Test
    fun `reset deve zerar os contadores`() {
        monitor.recordFrame(20.0)
        monitor.reset()

        val report = monitor.generateReport()
        assertEquals(0L, report.totalFrames)
    }
}
