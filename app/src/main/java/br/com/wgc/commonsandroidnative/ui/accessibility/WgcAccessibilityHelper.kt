package br.com.wgc.commonsandroidnative.ui.accessibility

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Utilitários corporativos e modificadores de acessibilidade em conformidade com o padrão WCAG AAA.
 *
 * Fornece modificadores semânticos para Leitores de Tela (TalkBack), garantindo alvos de toque
 * mínimos de 48dp, marcação de cabeçalhos hierárquicos e cálculo de taxa de contraste de cores.
 *
 * ### 📌 Quando Usar:
 * Aplique estes modificadores em botões customizados, cabeçalhos de seções, ícones de ação e
 * elementos interativos em todas as telas de features para assegurar conformidade com a legislação
 * de acessibilidade digital e padrão WCAG AAA.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * // 1. Marcar título como cabeçalho para TalkBack:
 * Text(
 *     text = "Meu Carrinho",
 *     modifier = Modifier.wcagHeading()
 * )
 *
 * // 2. Tornar elemento clicável com descrição semântica e alvo de toque 48dp:
 * Box(
 *     modifier = Modifier
 *         .wcagTouchTarget()
 *         .wcagButton(description = "Adicionar item ao carrinho", onClickLabel = "Adicionar") {
 *             adicionarItem()
 *         }
 * )
 * ```
 */

/**
 * Marca o elemento Composable como um cabeçalho estrutural de navegação para leitores de tela.
 *
 * @return Modificador Compose com semântica de heading ativada.
 */
fun Modifier.wcagHeading(): Modifier = semantics {
    heading()
}

/**
 * Configura o papel semântico de botão com descrição e rótulo de ação customizado.
 *
 * @param description Texto falado pelo leitor de tela ao focar no elemento.
 * @param onClickLabel Rótulo que descreve a ação disparada (ex: "Toque duplo para pagar").
 * @param onClick Ação executada ao clicar no botão.
 * @return Modificador com semântica de acessibilidade de botão.
 */
fun Modifier.wcagButton(
    description: String,
    onClickLabel: String? = null,
    onClick: (() -> Unit)? = null
): Modifier = semantics(mergeDescendants = true) {
    this.role = Role.Button
    this.contentDescription = description
    if (onClick != null) {
        this.onClick(label = onClickLabel) {
            onClick()
            true
        }
    }
}

/**
 * Garante que a área clicável ou tocável do elemento atinja a dimensão mínima recomendada
 * pelas diretrizes WCAG e Material Design (48x48 dp), prevenindo toques acidentais ou perda de precisão motora.
 *
 * @param minSizeDp Dimensão mínima de largura e altura. Padrão: 48dp.
 * @return Modificador com tamanho mínimo delimitado.
 */
fun Modifier.wcagTouchTarget(minSizeDp: Dp = 48.dp): Modifier =
    this.defaultMinSize(minWidth = minSizeDp, minHeight = minSizeDp)

/**
 * Calculador de taxa de contraste de cores baseado na especificação oficial do W3C WCAG 2.1.
 *
 * Verifica a conformidade de pares de cores (texto/fundo) com os níveis de conformidade:
 * - Nível AA: Contraste mínimo de 4.5:1 para texto normal e 3.0:1 para texto grande.
 * - Nível AAA: Contraste mínimo de 7.0:1 para texto normal e 4.5:1 para texto grande.
 */
object WgcAccessibilityContrastCalculator {

    /**
     * Calcula a luminância relativa de uma cor RGB normalizada entre 0.0 e 1.0.
     *
     * @param color Cor Jetpack Compose a ser analisada.
     * @return Valor da luminância relativa ponderada (sRGB).
     */
    fun calculateLuminance(color: Color): Double {
        val r = gammaCorrect(color.red)
        val g = gammaCorrect(color.green)
        val b = gammaCorrect(color.blue)
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    private fun gammaCorrect(value: Float): Double {
        return if (value <= 0.03928) {
            value / 12.92
        } else {
            ((value + 0.055) / 1.055).toDouble().pow(2.4)
        }
    }

    /**
     * Calcula a razão de contraste (Contrast Ratio) entre a cor de primeiro plano (texto/ícone)
     * e a cor de plano de fundo (superfície).
     *
     * @param foreground Cor do texto ou ícone.
     * @param background Cor da superfície de fundo.
     * @return Razão de contraste no intervalo de 1.0 a 21.0.
     */
    fun calculateContrastRatio(foreground: Color, background: Color): Double {
        val l1 = calculateLuminance(foreground)
        val l2 = calculateLuminance(background)
        val lighter = max(l1, l2)
        val darker = min(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    /**
     * Verifica se a razão de contraste atinge o critério de aprovação WCAG AAA.
     *
     * @param foreground Cor do texto ou ícone.
     * @param background Cor da superfície de fundo.
     * @param isLargeText Indica se o texto possui tamanho grande (>= 18pt regular ou >= 14pt bold).
     * @return True se o contraste for superior ou igual a 7:1 (ou 4.5:1 para texto grande).
     */
    fun isWcagAaaCompliant(foreground: Color, background: Color, isLargeText: Boolean = false): Boolean {
        val ratio = calculateContrastRatio(foreground, background)
        val requiredRatio = if (isLargeText) 4.5 else 7.0
        return ratio >= requiredRatio
    }
}
