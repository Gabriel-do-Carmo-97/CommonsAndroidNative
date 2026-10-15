package br.com.wgc.whatsapp_direct.dispatcher

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [WhatsAppDirectDispatcher] validando formatação de mensagens, higienização de telefones e codificação de URL.
 */
class WhatsAppDirectDispatcherTest {

    private lateinit var dispatcher: WhatsAppDirectDispatcher

    @Before
    fun setup() {
        dispatcher = WhatsAppDirectDispatcher()
    }

    @Test
    fun `buildOrderSupportSpec formats text and sanitizes phone with country code`() {
        val spec = dispatcher.buildOrderSupportSpec(
            merchantPhone = "(11) 98765-4321",
            orderId = "#WGC-555",
            customerName = "Gabriel",
            status = "Em Preparação"
        )

        assertEquals("5511987654321", spec.targetPhone)
        assertEquals("com.whatsapp", spec.targetPackage)
        assertTrue(spec.uriString.startsWith("https://api.whatsapp.com/send?phone=5511987654321"))
        assertTrue(spec.uriString.contains("WGC-555"))
        assertTrue(spec.messageText.contains("Gabriel"))
    }

    @Test
    fun `buildServiceBookingSpec formats appointment details correctly`() {
        val spec = dispatcher.buildServiceBookingSpec(
            professionalPhone = "11911112222",
            professionalName = "Dra. Renata",
            serviceName = "Consulta Fisioterapia",
            slotDateTime = "20/10 às 15h"
        )

        assertEquals("5511911112222", spec.targetPhone)
        assertTrue(spec.messageText.contains("Dra. Renata"))
        assertTrue(spec.messageText.contains("Consulta Fisioterapia"))
    }

    @Test
    fun `createSpec preserves existing 55 country code`() {
        val spec = dispatcher.createSpec("5511999990000", "Teste")
        assertEquals("5511999990000", spec.targetPhone)
    }
}
