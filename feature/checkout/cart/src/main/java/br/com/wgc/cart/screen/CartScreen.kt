package br.com.wgc.cart.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.wgc.design_system.templates.factories.WgcCartFactory
import br.com.wgc.design_system.templates.factories.WgcCartType
import br.com.wgc.design_system.templates.screens.cart.StandardCartScreenTemplate

/**
 * Tela principal e ponto de entrada corporativo do Carrinho de Compras (Cart).
 *
 * Construída sobre a [WgcCartFactory] do `DesignSystemAndroid 0.24.1`, suporta múltiplas variantes visuais
 * e comerciais do ecossistema WGC (Padrão, Supermercado, Farmácia, Varejo, Moda e Megastore).
 *
 * ### 📌 Quando Usar:
 * Utilize esta Composable para apresentar o carrinho de compras do usuário em qualquer segmento comercial
 * do ecossistema, permitindo adição e remoção de itens, cálculo de taxa de entrega e avanço para o checkout.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * // 1. Uso padrão integrado com ViewModel Hilt:
 * @Composable
 * fun FluxoDoCarrinho(
 *     onNavigateToCheckout: () -> Unit,
 *     onNavigateBack: () -> Unit
 * ) {
 *     CartScreen(
 *         cartType = WgcCartType.STANDARD,
 *         onCheckoutClick = onNavigateToCheckout,
 *         onBackClick = onNavigateBack
 *     )
 * }
 *
 * // 2. Uso com variante visual especializada (ex: Farmácia):
 * CartScreen(
 *     cartType = WgcCartType.CARE_PHARMACY,
 *     onCheckoutClick = { iniciarCheckoutFarmacia() }
 * )
 * ```
 *
 * @param modifier Modificador de layout Compose aplicado ao contêiner raiz da tela.
 * @param cartType Tipo e estilo comercial de carrinho a ser renderizado via [WgcCartType]. O padrão é [WgcCartType.STANDARD].
 * @param viewModel ViewModel injetado via Hilt para gerenciamento de itens, regras de cálculo e estado reativo.
 * @param onBackClick Callback opcional disparado quando o usuário clica no botão de voltar.
 * @param onCheckoutClick Callback opcional disparado quando o usuário clica para finalizar a compra / ir para o checkout.
 */
@Composable
fun CartScreen(
    modifier: Modifier = Modifier,
    cartType: WgcCartType = WgcCartType.STANDARD,
    viewModel: CartViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onCheckoutClick: () -> Unit = { viewModel.onCheckoutClick() }
) {
    if (cartType == WgcCartType.STANDARD) {
        StandardCartScreenTemplate(
            viewModel = viewModel
        )
    } else {
        WgcCartFactory(
            modifier = modifier,
            type = cartType,
            onBackClick = onBackClick,
            onCheckoutClick = onCheckoutClick
        )
    }
}