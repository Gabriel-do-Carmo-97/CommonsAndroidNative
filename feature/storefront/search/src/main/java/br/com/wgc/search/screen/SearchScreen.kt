package br.com.wgc.search.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.search.SearchAndFilterScreenTemplate

/**
 * Tela de Busca Reativa e Filtros Avançados de produtos e lojas.
 *
 * @param modifier Modificador de layout Compose.
 * @param viewModel ViewModel com suporte a busca com debounce e filtros de categoria.
 */
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    SearchAndFilterScreenTemplate(
        viewModel = viewModel
    )
}