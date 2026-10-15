package br.com.wgc.ai_assistant.engine

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [AiAssistantStreamingEngine] validando emissão de chunks e parser de SSE.
 */
class AiAssistantStreamingEngineTest {

    private lateinit var engine: AiAssistantStreamingEngine

    @Before
    fun setup() {
        engine = AiAssistantStreamingEngine()
    }

    @Test
    fun `streamTokens emits all simulated tokens with last chunk marked as final`() = runTest {
        val simulated = listOf("Olá, ", "como ", "posso ", "ajudar?")
        val chunks = engine.streamTokens("Oi", simulatedTokens = simulated).toList()

        assertEquals(4, chunks.size)
        assertEquals("Olá, ", chunks[0].token)
        assertFalse(chunks[0].isFinalChunk)

        assertEquals("ajudar?", chunks[3].token)
        assertTrue(chunks[3].isFinalChunk)
    }

    @Test
    fun `parseServerSentEvent extracts token from raw text or json SSE lines`() {
        // Plain text data:
        val plain = engine.parseServerSentEvent("data: Mensagem de teste")
        assertEquals("Mensagem de teste", plain)

        // JSON data:
        val json = engine.parseServerSentEvent("""data: {"token": "palavra_chave"}""")
        assertEquals("palavra_chave", json)

        // [DONE] terminator
        val done = engine.parseServerSentEvent("data: [DONE]")
        assertNull(done)

        // Invalid line
        val invalid = engine.parseServerSentEvent(": ping heartbeat")
        assertNull(invalid)
    }
}
