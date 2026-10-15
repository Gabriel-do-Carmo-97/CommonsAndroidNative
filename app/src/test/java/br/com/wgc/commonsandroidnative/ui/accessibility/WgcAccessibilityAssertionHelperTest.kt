package br.com.wgc.commonsandroidnative.ui.accessibility

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [WgcAccessibilityAssertionHelper].
 */
class WgcAccessibilityAssertionHelperTest {

    private lateinit var helper: WgcAccessibilityAssertionHelper

    @Before
    fun setup() {
        helper = WgcAccessibilityAssertionHelper()
    }

    @Test
    fun `auditInteractiveElement com dimensoes e descricao adequadas deve ser Aprovado`() {
        val result = helper.auditInteractiveElement(
            widthDp = 48f,
            heightDp = 48f,
            contentDescription = "Botão Finalizar Pedido"
        )

        assertTrue(result is AccessibilityValidationResult.Approved)
    }

    @Test
    fun `auditInteractiveElement com dimensao menor que 48dp deve ser Rejeitado`() {
        val result = helper.auditInteractiveElement(
            widthDp = 32f,
            heightDp = 48f,
            contentDescription = "Botão Voltar"
        )

        assertTrue(result is AccessibilityValidationResult.Rejected)
        val rejected = result as AccessibilityValidationResult.Rejected
        assertEquals(1, rejected.violations.size)
        assertTrue(rejected.violations[0].contains("48x48 dp"))
    }

    @Test
    fun `auditInteractiveElement com contentDescription vazio deve ser Rejeitado`() {
        val result = helper.auditInteractiveElement(
            widthDp = 50f,
            heightDp = 50f,
            contentDescription = ""
        )

        assertTrue(result is AccessibilityValidationResult.Rejected)
        val rejected = result as AccessibilityValidationResult.Rejected
        assertEquals(1, rejected.violations.size)
        assertTrue(rejected.violations[0].contains("contentDescription"))
    }

    @Test
    fun `auditInteractiveElement com multiplas violacoes deve reportar ambas`() {
        val result = helper.auditInteractiveElement(
            widthDp = 24f,
            heightDp = 24f,
            contentDescription = null
        )

        assertTrue(result is AccessibilityValidationResult.Rejected)
        val rejected = result as AccessibilityValidationResult.Rejected
        assertEquals(2, rejected.violations.size)
    }
}
