package br.com.wgc.commonsandroidnative.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Modelo de dados representando um produto com suporte a transições visuais contínuas.
 *
 * @property id Identificador único do produto para mapeamento de chaves compartilhadas.
 * @property title Nome comercial do item.
 * @property price Preço formatado do produto.
 * @property description Descrição detalhada do produto.
 * @property category Categoria de navegação comercial.
 */
data class SharedProductItem(
    val id: String,
    val title: String,
    val price: String,
    val description: String,
    val category: String
)

/**
 * Orquestrador corporativo de Navegação com Transições Compartilhadas (Shared Element Transitions).
 *
 * Utiliza o [SharedTransitionLayout] do Jetpack Compose para animar a expansão fluida
 * de um card da vitrine de catálogo diretamente para a tela de detalhes do produto, mantendo
 * a continuidade visual de imagem, título e container entre as telas.
 *
 * ### 📌 Quando Usar:
 * Utilize na transição de fluxo entre o catálogo (:feature:storefront:catalog), busca (:feature:storefront:search)
 * e tela de detalhes ou customização do item, eliminando saltos abruptos de navegação.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun CatalogoComDetalhe() {
 *     val sampleProduct = SharedProductItem(
 *         id = "prod-001",
 *         title = "Burger Artesanal",
 *         price = "R$ 38,00",
 *         description = "Pão brioche, blend 180g e queijo cheddar inglês.",
 *         category = "Burgers"
 *     )
 *
 *     WgcSharedProductTransitionShowcase(product = sampleProduct)
 * }
 * ```
 *
 * @param product Produto demonstrativo utilizado na transição de tela.
 * @param modifier Modificador de layout Compose.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun WgcSharedProductTransitionShowcase(
    product: SharedProductItem,
    modifier: Modifier = Modifier
) {
    var isDetailView by remember { mutableStateOf(false) }

    SharedTransitionLayout(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = isDetailView,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "ProductSharedTransition"
        ) { targetDetail ->
            if (targetDetail) {
                ProductDetailScreen(
                    product = product,
                    animatedVisibilityScope = this@AnimatedContent,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    onBackClick = { isDetailView = false }
                )
            } else {
                ProductCardPreview(
                    product = product,
                    animatedVisibilityScope = this@AnimatedContent,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    onProductClick = { isDetailView = true }
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ProductCardPreview(
    product: SharedProductItem,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    onProductClick: () -> Unit
) {
    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .sharedBounds(
                        rememberSharedContentState(key = "product-container-${product.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .clickable { onProductClick() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .sharedElement(
                                rememberSharedContentState(key = "product-image-${product.id}"),
                                animatedVisibilityScope = animatedVisibilityScope
                            ),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "product-title-${product.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = product.price,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "product-price-${product.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ProductDetailScreen(
    product: SharedProductItem,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    onBackClick: () -> Unit
) {
    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .sharedBounds(
                    rememberSharedContentState(key = "product-container-${product.id}"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                }
                Text("Detalhes do Produto", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .sharedElement(
                        rememberSharedContentState(key = "product-image-${product.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    ),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = product.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.sharedElement(
                    rememberSharedContentState(key = "product-title-${product.id}"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.price,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.sharedElement(
                    rememberSharedContentState(key = "product-price-${product.id}"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Adicionar ao Carrinho")
            }
        }
    }
}
