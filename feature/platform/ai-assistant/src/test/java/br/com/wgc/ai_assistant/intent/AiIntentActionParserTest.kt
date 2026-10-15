package br.com.wgc.ai_assistant.intent

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [AiIntentActionParser] validando extração de intenções, parâmetros e rotas de atalho.
 */
class AiIntentActionParserTest {

    private lateinit var parser: AiIntentActionParser

    @Before
    fun setup() {
        parser = AiIntentActionParser()
    }

    @Test
    fun `parseUserInput detects reorder intent`() {
        val action = parser.parseUserInput("Quero repetir meu último pedido de pizza")
        assertEquals(AiIntentType.REORDER_LAST_PURCHASE, action.intentType)
        assertEquals("wgc://checkout/cart/reorder", action.targetNavigationRoute)
    }

    @Test
    fun `parseUserInput detects order tracking intent`() {
        val action = parser.parseUserInput("Onde está meu pedido agora?")
        assertEquals(AiIntentType.TRACK_ACTIVE_ORDER, action.intentType)
        assertEquals("wgc://delivery/tracking/active", action.targetNavigationRoute)
    }

    @Test
    fun `parseUserInput detects catalog search intent and extracts query term`() {
        val action = parser.parseUserInput("Buscar hambúrguer artesanal")
        assertEquals(AiIntentType.SEARCH_CATALOG, action.intentType)
        assertTrue(action.targetNavigationRoute.contains("hambúrguer artesanal"))
        assertEquals("hambúrguer artesanal", action.parameters["query"])
    }

    @Test
    fun `parseUserInput detects support intent`() {
        val action = parser.parseUserInput("Preciso falar com o suporte urgente")
        assertEquals(AiIntentType.OPEN_SUPPORT, action.intentType)
        assertEquals("wgc://support/chat", action.targetNavigationRoute)
    }

    @Test
    fun `parseUserInput returns conversational for unmapped general queries`() {
        val action = parser.parseUserInput("Qual a previsão do tempo para amanhã?")
        assertEquals(AiIntentType.CONVERSATIONAL_ONLY, action.intentType)
        assertEquals("", action.targetNavigationRoute)
    }
}
