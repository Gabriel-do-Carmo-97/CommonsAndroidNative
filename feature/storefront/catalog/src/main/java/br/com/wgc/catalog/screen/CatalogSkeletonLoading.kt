package br.com.wgc.catalog.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.wgc.design_system.commons.shimmerEffect
import br.com.wgc.design_system.components.placeholder.WgcSkeletonCard
import br.com.wgc.design_system.components.placeholder.WgcSkeletonListItem

/**
 * Componente corporativo de carregamento esquelético (Skeleton Loading & Shimmer) para a vitrine do catálogo.
 *
 * Substitui os spinners circulares tradicionais por silhuetas de cards de produtos e linhas de lista
 * animadas via [shimmerEffect] do `DesignSystemAndroid 0.24.1`, reduzindo a percepção de latência pelo usuário.
 *
 * ### 📌 Quando Usar:
 * Renderize enquanto a coleção remota de produtos estiver sendo carregada do OmniBackend
 * ou durante operações de troca de filtro e busca assíncrona.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun CatalogoScreen(isLoading: Boolean, content: @Composable () -> Unit) {
 *     if (isLoading) {
 *         CatalogSkeletonLoading()
 *     } else {
 *         content()
 *     }
 * }
 * ```
 *
 * @param modifier Modificador de layout Compose.
 * @param itemCount Quantidade de cards esqueléticos a serem desenhados.
 */
@Composable
fun CatalogSkeletonLoading(
    modifier: Modifier = Modifier,
    itemCount: Int = 4
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Shimmer categories bar
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(5) {
                Spacer(
                    modifier = Modifier
                        .height(32.dp)
                        .width(72.dp)
                        .shimmerEffect(true)
                )
            }
        }

        // Shimmer cards grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WgcSkeletonCard(modifier = Modifier.weight(1f))
            WgcSkeletonCard(modifier = Modifier.weight(1f))
        }

        // Shimmer list items
        repeat(itemCount) {
            WgcSkeletonListItem(modifier = Modifier.fillMaxWidth())
        }
    }
}
