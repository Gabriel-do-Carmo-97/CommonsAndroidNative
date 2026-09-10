package br.com.wgc.cart.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.cart.StandardCartScreenTemplate

/**
 * Tela principal do Carrinho de Compras (Cart).
 *
 * Renderiza os itens adicionados ao pedido, taxas de entrega dinÃ¢micas,
 * subtotal e permite o avanÃ§o para o checkout utilizando o template padronizado [StandardCartScreenTemplate].
 *
 * @param modifier Modificador de layout Jetpack Compose aplicado ao contÃªiner raiz da tela.
 * @param viewModel ViewModel injetado via Hilt para gerenciamento do ciclo de vida e estado do carrinho.
 */
@Composable
fun CartScreen(
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = hiltViewModel()
) {
    StandardCartScreenTemplate(
        viewModel = viewModel
    )
}