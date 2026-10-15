package br.com.wgc.commonsandroidnative.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import br.com.wgc.design_system.core.WgcBrandTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Provedor do tema de marca ativa no contexto da árvore de composição Jetpack Compose.
 */
val LocalWgcBrandTheme = staticCompositionLocalOf { WgcBrandTheme.foodDelivery() }

/**
 * Gerenciador corporativo de White-Labeling para troca dinâmica de marcas em tempo de execução.
 *
 * Permite que a aplicação ou módulos cliente alterem a identidade visual completa (paleta de cores,
 * tipografia e logos de marca) do monorepo Commons com apenas uma linha de código.
 *
 * ### 📌 Quando Usar:
 * Utilize para alternar a marca global do aplicativo (ex: Food Delivery, E-commerce, Fintech,
 * Saúde) com base no tenant corporativo, flavor de compilação ou preferência do usuário em tempo de execução.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * // 1. Alterar a marca em tempo de execução (1 linha de código):
 * WgcBrandThemeManager.setBrand(WgcBrandTheme.finance())
 *
 * // 2. Observar a marca ativa no Compose:
 * val activeBrand by WgcBrandThemeManager.currentBrand.collectAsState()
 * ```
 */
object WgcBrandThemeManager {
    private val _currentBrand = MutableStateFlow(WgcBrandTheme.foodDelivery())

    /**
     * Fluxo reativo que emite a marca [WgcBrandTheme] corporativa ativa sempre que houver alteração.
     */
    val currentBrand: StateFlow<WgcBrandTheme> = _currentBrand.asStateFlow()

    /**
     * Define a marca corporativa ativa no aplicativo.
     *
     * @param brand Instância de [WgcBrandTheme] contendo as diretrizes visuais e cores primárias.
     */
    fun setBrand(brand: WgcBrandTheme) {
        _currentBrand.value = brand
    }

    /**
     * Retorna a marca atualmente em vigor.
     *
     * @return Marca corporativa ativa.
     */
    fun getBrand(): WgcBrandTheme = _currentBrand.value
}
