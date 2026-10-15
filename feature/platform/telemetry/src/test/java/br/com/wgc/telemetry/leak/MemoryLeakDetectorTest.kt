package br.com.wgc.telemetry.leak

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [MemoryLeakDetector].
 */
class MemoryLeakDetectorTest {

    private lateinit var detector: MemoryLeakDetector

    @Before
    fun setup() {
        detector = MemoryLeakDetector()
    }

    @Test
    fun `watch deve registrar objeto e reportar como retido enquanto vivo`() {
        val dummyActivity = Any()
        val key = detector.watch(dummyActivity, "DummyActivity")

        assertNotNull(key)
        val report = detector.generateReport()
        assertEquals(1, report.retainedCount)
        assertEquals("DummyActivity", report.retainedReferences[0].tag)
    }

    @Test
    fun `clear deve remover todas as observacoes ativas`() {
        val obj1 = Any()
        val obj2 = Any()
        detector.watch(obj1, "Obj1")
        detector.watch(obj2, "Obj2")

        assertEquals(2, detector.generateReport().retainedCount)

        detector.clear()
        assertEquals(0, detector.generateReport().retainedCount)
    }
}
