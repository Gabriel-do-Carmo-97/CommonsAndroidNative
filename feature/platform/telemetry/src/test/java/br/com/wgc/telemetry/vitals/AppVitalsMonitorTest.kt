package br.com.wgc.telemetry.vitals

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [AppVitalsMonitor].
 */
class AppVitalsMonitorTest {

    private lateinit var monitor: AppVitalsMonitor

    @Before
    fun setup() {
        monitor = AppVitalsMonitor()
    }

    @Test
    fun `generateReport sem falhas em 1000 sessoes deve retornar HEALTHY`() {
        repeat(1000) { monitor.recordSession() }

        val report = monitor.generateReport()
        assertEquals(1000L, report.totalSessions)
        assertEquals(0L, report.totalCrashes)
        assertEquals(0L, report.totalAnrs)
        assertEquals(0.0, report.crashRatePercent, 0.001)
        assertEquals(0.0, report.anrRatePercent, 0.001)
        assertEquals(VitalsHealthStatus.HEALTHY, report.healthStatus)
    }

    @Test
    fun `generateReport com taxa de crash superior a 1 virgula 09 por cento deve retornar CRITICAL`() {
        // 1000 sessões e 15 crashes = 1.5% (> 1.09%)
        repeat(1000) { monitor.recordSession() }
        repeat(15) { monitor.recordCrash() }

        val report = monitor.generateReport()
        assertEquals(1.5, report.crashRatePercent, 0.01)
        assertEquals(VitalsHealthStatus.CRITICAL, report.healthStatus)
    }

    @Test
    fun `generateReport com taxa de ANR superior a 0 virgula 47 por cento deve retornar CRITICAL`() {
        // 1000 sessões e 5 ANRs = 0.5% (> 0.47%)
        repeat(1000) { monitor.recordSession() }
        repeat(5) { monitor.recordAnr() }

        val report = monitor.generateReport()
        assertEquals(0.5, report.anrRatePercent, 0.01)
        assertEquals(VitalsHealthStatus.CRITICAL, report.healthStatus)
    }

    @Test
    fun `reset deve zerar os dados de telemetria de vitals`() {
        monitor.recordSession()
        monitor.recordCrash()
        monitor.reset()

        val report = monitor.generateReport()
        assertEquals(0L, report.totalSessions)
        assertEquals(0L, report.totalCrashes)
    }
}
