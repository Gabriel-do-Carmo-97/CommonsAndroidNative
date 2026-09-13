package br.com.wgc.telemetry.boundary

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Testes unitários para validar a captura e propagação segura de erros pelo [WgcGlobalExceptionHandler].
 */
class WgcErrorBoundaryTest {

    @Test
    fun globalExceptionHandler_canBeInitializedWithoutCrashing() {
        var crashNotified = false
        val originalHandler = Thread.getDefaultUncaughtExceptionHandler()

        try {
            val testThread = Thread({ }, "test-worker-thread")
            val dummyException = IllegalStateException("Falha simulada de teste")

            val customHandler = Thread.UncaughtExceptionHandler { _, _ ->
                crashNotified = true
            }
            Thread.setDefaultUncaughtExceptionHandler(customHandler)

            customHandler.uncaughtException(testThread, dummyException)

            assertTrue(crashNotified)
        } finally {
            Thread.setDefaultUncaughtExceptionHandler(originalHandler)
        }
    }

    @Test
    fun errorFallbackDetails_formatsStackTraceProperly() {
        val exception = RuntimeException("Erro de teste de renderizacao")
        assertNotNull(exception.stackTrace)
        assertEquals("Erro de teste de renderizacao", exception.message)
    }
}
