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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.home.ecommerce.EcommerceHomeScreenTemplate
import br.com.wgc.ds_templates.screens.ifood.FakeIFoodHomeViewModel
import br.com.wgc.ds_templates.screens.ifood.IFoodHomeScreenTemplate
import br.com.wgc.ds_templates.screens.mercadolivre.FakeMercadoLivreHomeViewModel
import br.com.wgc.ds_templates.screens.mercadolivre.MercadoLivreHomeScreenTemplate

/**
 * Modos de exibiÃ§Ã£o e layouts comerciais suportados pelo catÃ¡logo de produtos.
 *
 * @property title RÃ³tulo descritivo do template comercial.
 */
enum class CommercialTemplateMode(val title: String) {
    /** Layout padrÃ£o de e-commerce com grid e carrossÃ©is horizontais. */
    ECOMMERCE_STANDARD("E-Commerce PadrÃ£o"),

    /** Layout estilo app de delivery (iFood) com foco em restaurantes e pratos. */
    IFOOD_DELIVERY("iFood / Delivery"),

    /** Layout estilo marketplace (Mercado Livre) com banners e ofertas do dia. */
    MERCADO_LIVRE("Mercado Livre")
}

/**
 * Tela principal do CatÃ¡logo de Produtos e Vitrines Comerciais.
 *
 * Permite alternÃ¢ncia dinÃ¢mica em tempo de execuÃ§Ã£o entre diferentes templates
 * visuais corporativos mantidos pelo Design System.
 *
 * @param modifier Modificador de layout Compose.
 * @param initialMode Modo de layout comercial inicial a ser renderizado.
 * @param viewModel ViewModel de controle do catÃ¡logo e produtos.
 */
@Composable
fun CatalogScreen(
    modifier: Modifier = Modifier,
    initialMode: CommercialTemplateMode = CommercialTemplateMode.ECOMMERCE_STANDARD,
    viewModel: CatalogViewModel = hiltViewModel()
) {
    var selectedMode by rememberSaveable { mutableStateOf(initialMode) }

    val ifoodViewModel = remember { FakeIFoodHomeViewModel() }
    val mlViewModel = remember { FakeMercadoLivreHomeViewModel() }

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
            when (selectedMode) {
                CommercialTemplateMode.ECOMMERCE_STANDARD -> {
                    EcommerceHomeScreenTemplate(viewModel = viewModel)
                }
                CommercialTemplateMode.IFOOD_DELIVERY -> {
                    IFoodHomeScreenTemplate(viewModel = ifoodViewModel)
                }
                CommercialTemplateMode.MERCADO_LIVRE -> {
                    MercadoLivreHomeScreenTemplate(viewModel = mlViewModel)
                }
            }
        }
    }
}