package br.com.wgc.catalog.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.wgc.design_system.templates.screens.home.ecommerce.EcommerceHomeScreenTemplate
import br.com.wgc.design_system.templates.screens.fooddelivery.FakeFoodDeliveryHomeViewModel
import br.com.wgc.design_system.templates.screens.fooddelivery.WgcFoodDeliveryHomeScreenTemplate
import br.com.wgc.design_system.templates.screens.marketplacescreen.FakeMarketplaceHomeViewModel
import br.com.wgc.design_system.templates.screens.marketplacescreen.WgcMarketplaceHomeScreenTemplate

/**
 * Modos de exibição e layouts comerciais suportados pela vitrine corporativa de produtos.
 *
 * ### 📌 Quando Usar:
 * Utilize para alternar a experiência visual entre layout tradicional de e-commerce de varejo,
 * delivery de alimentação (estilo iFood) ou marketplace de múltiplos vendedores (estilo Mercado Livre).
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val mode = CommercialTemplateMode.IFOOD_DELIVERY
 * println("Modo selecionado: ${mode.title}")
 * ```
 *
 * @property title Rótulo descritivo exibido nos seletores de layout da interface.
 */
enum class CommercialTemplateMode(val title: String) {
    /** Layout padrão de e-commerce com grid e carrosséis horizontais. */
    ECOMMERCE_STANDARD("E-Commerce Padrão"),

    /** Layout estilo app de delivery (iFood) com foco em restaurantes e pratos. */
    IFOOD_DELIVERY("iFood / Delivery"),

    /** Layout estilo marketplace (Mercado Livre) com banners e ofertas do dia. */
    MERCADO_LIVRE("Mercado Livre")
}

/**
 * Tela principal do Catálogo de Produtos e Vitrines Comerciais Multi-segmento.
 *
 * Permite alternância dinâmica em tempo de execução entre diferentes templates
 * visuais corporativos mantidos pelo Design System (`EcommerceHomeScreenTemplate`,
 * `WgcFoodDeliveryHomeScreenTemplate`, `WgcMarketplaceHomeScreenTemplate`).
 *
 * ### 📌 Quando Usar:
 * Ponto de entrada visual da aba "Início" / "Loja" em aplicações white-label que precisam
 * suportar catálogos de restaurantes, supermercados ou lojas de departamentos.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun AppNavigation() {
 *     CatalogScreen(
 *         initialMode = CommercialTemplateMode.ECOMMERCE_STANDARD
 *     )
 * }
 * ```
 *
 * @param modifier Modificador de layout Compose para estilização externa.
 * @param initialMode Modo de layout comercial inicial a ser renderizado na abertura da tela.
 * @param viewModel ViewModel injetado com o estado do catálogo e produtos do OmniBackend.
 */
@Composable
fun CatalogScreen(
    modifier: Modifier = Modifier,
    initialMode: CommercialTemplateMode = CommercialTemplateMode.ECOMMERCE_STANDARD,
    viewModel: CatalogViewModel = hiltViewModel()
) {
    var selectedMode by rememberSaveable { mutableStateOf(initialMode) }

    val ifoodViewModel = remember { FakeFoodDeliveryHomeViewModel() }
    val mlViewModel = remember { FakeMarketplaceHomeViewModel() }

    Column(modifier = modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Layout Comercial:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                FilterChip(
                    selected = selectedMode == CommercialTemplateMode.ECOMMERCE_STANDARD,
                    onClick = { selectedMode = CommercialTemplateMode.ECOMMERCE_STANDARD },
                    leadingIcon = { Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    label = { Text(CommercialTemplateMode.ECOMMERCE_STANDARD.title) }
                )

                FilterChip(
                    selected = selectedMode == CommercialTemplateMode.IFOOD_DELIVERY,
                    onClick = { selectedMode = CommercialTemplateMode.IFOOD_DELIVERY },
                    leadingIcon = { Icon(Icons.Default.Fastfood, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    label = { Text(CommercialTemplateMode.IFOOD_DELIVERY.title) }
                )

                FilterChip(
                    selected = selectedMode == CommercialTemplateMode.MERCADO_LIVRE,
                    onClick = { selectedMode = CommercialTemplateMode.MERCADO_LIVRE },
                    leadingIcon = { Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    label = { Text(CommercialTemplateMode.MERCADO_LIVRE.title) }
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            val uiState by viewModel.uiState.collectAsState()
            if (uiState.featuredProducts.isEmpty()) {
                CatalogSkeletonLoading()
            } else {
                when (selectedMode) {
                    CommercialTemplateMode.ECOMMERCE_STANDARD -> {
                        EcommerceHomeScreenTemplate(viewModel = viewModel)
                    }
                    CommercialTemplateMode.IFOOD_DELIVERY -> {
                        WgcFoodDeliveryHomeScreenTemplate(viewModel = ifoodViewModel)
                    }
                    CommercialTemplateMode.MERCADO_LIVRE -> {
                        WgcMarketplaceHomeScreenTemplate(viewModel = mlViewModel)
                    }
                }
            }
        }
    }
}