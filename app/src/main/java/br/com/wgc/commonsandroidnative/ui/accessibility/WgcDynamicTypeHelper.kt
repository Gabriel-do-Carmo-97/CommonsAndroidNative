package br.com.wgc.commonsandroidnative.ui.accessibility

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlin.math.min

/**
 * Utilitário corporativo para suporte a Dimensionamento Dinâmico de Tipografia (Dynamic Type / Font Scaling).
 *
 * Gerencia a adaptação de interfaces Jetpack Compose para escalas de fonte do sistema operacional
 * de até 200% (2.0x), prevenindo cortes de labels (clipping), elipses indesejadas e quebra de contêineres fixos.
 *
 * ### 📌 Quando Usar:
 * Utilize para inspecionar a escala ativa do dispositivo e definir se um componente deve
 * alternar dinamicamente entre layout horizontal (Row) ou vertical (Column), além de aplicar
 * limites máximos de saturação visual quando necessário.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun BotoesDeAcao(onConfirm: () -> Unit, onCancel: () -> Unit) {
 *     val isHighScale = WgcDynamicTypeHelper.isHighFontScale()
 *
 *     if (isHighScale) {
 *         // Em escala de fonte elevada (>= 130%), empilhe verticalmente para não cortar texto:
 *         Column {
 *             Button(onClick = onConfirm) { Text("Confirmar Pagamento") }
 *             Button(onClick = onCancel) { Text("Cancelar") }
 *         }
 *     } else {
 *         // Em escala normal, posicione lado a lado:
 *         Row {
 *             Button(onClick = onCancel) { Text("Cancelar") }
 *             Button(onClick = onConfirm) { Text("Confirmar Pagamento") }
 *         }
 *     }
 * }
 * ```
 */
object WgcDynamicTypeHelper {

    /**
     * Limiar de escala de fonte (130%) a partir do qual layouts horizontais com textos longos
     * devem ser reconfigurados para orientação vertical.
     */
    const val HIGH_FONT_SCALE_THRESHOLD = 1.30f

    /**
     * Limite máximo de dimensionamento para prevenção de distorções estruturais severas.
     */
    const val MAX_SUPPORTED_FONT_SCALE = 2.00f

    /**
     * Inspeciona se a escala de fonte configurada no sistema operacional atinge o limiar elevado.
     *
     * @param fontScale Escala de fonte atual (padrão obtido via LocalConfiguration no Compose).
     * @return True se a escala de fonte for superior ou igual a 1.3x.
     */
    fun isHighFontScale(fontScale: Float): Boolean {
        return fontScale >= HIGH_FONT_SCALE_THRESHOLD
    }

    /**
     * Composable helper que verifica a escala de fonte do dispositivo atual.
     *
     * @return True se a escala de fonte for superior ou igual a 1.3x.
     */
    @Composable
    fun isHighFontScale(): Boolean {
        val configuration = LocalConfiguration.current
        return isHighFontScale(configuration.fontScale)
    }

    /**
     * Limita a escala máxima de um tamanho de fonte em pontos independentes de escala (sp),
     * garantindo legibilidade sem ultrapassar os limites de contêineres críticos.
     *
     * @param baseSp Tamanho base da tipografia.
     * @param currentFontScale Fator de escala atual do sistema (ex: 1.5 para 150%).
     * @param maxScale Limite máximo permitido de multiplicação de escala (padrão: 2.0x).
     * @return Tamanho escalonado e seguro em [TextUnit].
     */
    fun clampScaledTextSize(baseSp: Float, currentFontScale: Float, maxScale: Float = MAX_SUPPORTED_FONT_SCALE): TextUnit {
        val effectiveScale = min(currentFontScale, maxScale)
        return (baseSp * effectiveScale).sp
    }
}
