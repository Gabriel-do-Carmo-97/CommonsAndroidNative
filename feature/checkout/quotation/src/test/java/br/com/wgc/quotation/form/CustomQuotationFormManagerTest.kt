package br.com.wgc.quotation.form

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [CustomQuotationFormManager] validando etapas 1, 2 e 3 do formulário e compilação do protocolo.
 */
class CustomQuotationFormManagerTest {

    private lateinit var formManager: CustomQuotationFormManager

    @Before
    fun setup() {
        formManager = CustomQuotationFormManager()
    }

    @Test
    fun `step 1 validation fails if title or description are too short`() {
        var draft = formManager.createDraft(QuotationCategory.RESIDENTIAL_REPAIR)
        draft = formManager.updateStep1(draft, title = "Oi", description = "Curto")

        val invalidTitle = formManager.validateStep(draft, 1)
        assertTrue(invalidTitle is QuotationValidationResult.Invalid)
        assertEquals("title", (invalidTitle as QuotationValidationResult.Invalid).field)

        draft = formManager.updateStep1(draft, title = "Reforma Completa do Banheiro", description = "Troca de pisos")
        val invalidDesc = formManager.validateStep(draft, 1)
        assertTrue(invalidDesc is QuotationValidationResult.Invalid)
        assertEquals("description", (invalidDesc as QuotationValidationResult.Invalid).field)

        draft = formManager.updateStep1(draft, title = "Reforma Completa do Banheiro", description = "Troca de pisos, revestimentos e instalação de nova bancada de mármore.")
        val valid = formManager.validateStep(draft, 1)
        assertTrue(valid is QuotationValidationResult.Valid)
    }

    @Test
    fun `step 2 and 3 validation and final compilation succeed when all fields are valid`() {
        var draft = formManager.createDraft(QuotationCategory.CUSTOM_FURNITURE)
        draft = formManager.updateStep1(draft, title = "Armário Embutido Quarto", description = "Armário planejado de 3 portas com espelho e gaveteiros embutidos.")
        draft = formManager.updateStep2(draft, preferredDateIso = "2026-11-01", estimatedBudgetMax = 5000.0)
        draft = formManager.updateStep3(draft, photoPaths = listOf("/sdcard/projeto.jpg"), contactPhone = "11988887777")

        assertTrue(formManager.validateStep(draft, 1) is QuotationValidationResult.Valid)
        assertTrue(formManager.validateStep(draft, 2) is QuotationValidationResult.Valid)
        assertTrue(formManager.validateStep(draft, 3) is QuotationValidationResult.Valid)

        val submission = formManager.compileFinalQuotation(draft)
        assertNotNull(submission.quotationId)
        assertTrue(submission.quotationId.startsWith("COT-"))
        assertEquals("Armário Embutido Quarto", submission.draft.title)
    }

    @Test(expected = IllegalStateException::class)
    fun `compileFinalQuotation throws IllegalStateException when form is incomplete`() {
        val draft = formManager.createDraft(QuotationCategory.TECH_SERVICES)
        formManager.compileFinalQuotation(draft)
    }
}
