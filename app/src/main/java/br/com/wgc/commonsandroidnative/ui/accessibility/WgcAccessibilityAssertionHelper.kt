package br.com.wgc.commonsandroidnative.ui.accessibility

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Resultado da auditoria automatizada de acessibilidade digital WCAG.
 */
sealed class AccessibilityValidationResult {
    /** Elemento totalmente em conformidade com as diretrizes de acessibilidade. */
    data object Approved : AccessibilityValidationResult()

    /**
     * Elemento reprovado com violações de acessibilidade.
     *
     * @property violations Lista de falhas encontradas no componente inspecionado.
     */
    data class Rejected(val violations: List<String>) : AccessibilityValidationResult()
}

/**
 * Validador automatizado para testes de acessibilidade em componentes Jetpack Compose.
 *
 * Assegura em tempo de execução e durante suítes de testes que nenhum botão ou elemento interativo:
 * 1. Careça de rótulo falado (`contentDescription`).
 * 2. Apresente alvo de toque menor que o limite mínimo ergonômico de 48x48 dp estipulado pelo WCAG AAA e Android Vitals.
 *
 * ### 📌 Quando Usar:
 * - Em testes unitários e testes de interface (`createComposeRule`) de componentes e botões de tela.
 * - Na esteira de CI/CD para impedir regressões de acessibilidade motora ou visual.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val helper = WgcAccessibilityAssertionHelper()
 * val result = helper.auditInteractiveElement(
 *     widthDp = 48f,
 *     heightDp = 48f,
 *     contentDescription = "Confirmar pagamento"
 * )
 * assertTrue(result is AccessibilityValidationResult.Approved)
 * ```
 */
@Singleton
class WgcAccessibilityAssertionHelper @Inject constructor() {

    companion object {
        /** Dimensão mínima em DP estipulada pela diretriz de acessibilidade do Material Design e WCAG. */
        const val MIN_TOUCH_TARGET_DP = 48f
    }

    /**
     * Audita um elemento de interface interativo quanto à dimensão ergonômica e descrição TalkBack.
     *
     * @param widthDp Largura medida do alvo de toque em dp.
     * @param heightDp Altura medida do alvo de toque em dp.
     * @param contentDescription Texto de descrição semântica falado pelo leitor de tela.
     * @return [AccessibilityValidationResult] com parecer da aprovação ou lista de violações.
     */
    fun auditInteractiveElement(
        widthDp: Float,
        heightDp: Float,
        contentDescription: String?
    ): AccessibilityValidationResult {
        val violations = mutableListOf<String>()

        if (widthDp < MIN_TOUCH_TARGET_DP || heightDp < MIN_TOUCH_TARGET_DP) {
            violations.add(
                "Alvo de toque com dimensão (${widthDp}x${heightDp} dp) inferior ao mínimo de 48x48 dp exigido."
            )
        }

        if (contentDescription.isNullOrBlank()) {
            violations.add(
                "Elemento interativo sem descrição semântica ou contentDescription vazio para leitores de tela."
            )
        }

        return if (violations.isEmpty()) {
            AccessibilityValidationResult.Approved
        } else {
            AccessibilityValidationResult.Rejected(violations)
        }
    }
}
