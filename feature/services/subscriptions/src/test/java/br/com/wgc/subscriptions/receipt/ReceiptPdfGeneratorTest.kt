package br.com.wgc.subscriptions.receipt

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [ReceiptPdfGenerator] validando formatação de recibos e especificações de compartilhamento.
 */
class ReceiptPdfGeneratorTest {

    private lateinit var generator: ReceiptPdfGenerator

    @Before
    fun setup() {
        generator = ReceiptPdfGenerator()
    }

    @Test
    fun `formatReceiptText produces complete structured fiscal layout`() {
        val doc = ServiceReceiptDocument(
            receiptNumber = "REC-8849",
            customerName = "Gabriel do Carmo",
            customerTaxIdMasked = "***.123.456-**",
            providerName = "WGC Serviços Ltda",
            providerTaxId = "12.345.678/0001-90",
            issueDate = "15/10/2026 14:00",
            items = listOf(
                ReceiptItem(description = "Assinatura Pro Anual", quantity = 1, unitPriceFormatted = "R$ 450,00", totalFormatted = "R$ 450,00")
            ),
            subtotalFormatted = "R$ 450,00",
            totalFormatted = "R$ 450,00",
            paymentMethodDescription = "Cartão de Crédito final 9911",
            authorizationCode = "AUTH-992144"
        )

        val text = generator.formatReceiptText(doc)

        assertTrue(text.contains("COMPROVANTE DE PAGAMENTO"))
        assertTrue(text.contains("REC-8849"))
        assertTrue(text.contains("Gabriel do Carmo"))
        assertTrue(text.contains("WGC Serviços Ltda"))
        assertTrue(text.contains("Assinatura Pro Anual"))
        assertTrue(text.contains("AUTH-992144"))
    }

    @Test
    fun `buildShareSpec creates valid Intent spec`() {
        val doc = ServiceReceiptDocument(
            receiptNumber = "REC-100",
            customerName = "Cliente",
            customerTaxIdMasked = "***",
            providerName = "Empresa",
            providerTaxId = "000",
            issueDate = "Hoje",
            items = emptyList(),
            subtotalFormatted = "R$ 10,00",
            totalFormatted = "R$ 10,00",
            paymentMethodDescription = "PIX",
            authorizationCode = "AUTH-1"
        )

        val spec = generator.buildShareSpec(doc)

        assertEquals("android.intent.action.SEND", spec.action)
        assertEquals("text/plain", spec.mimeType)
        assertTrue(spec.subject.contains("REC-100"))
        assertTrue(spec.shareBody.contains("COMPROVANTE DE PAGAMENTO"))
    }
}
