package br.com.wgc.search.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.wgc.design_system.templates.factories.WgcSearchFactory
import br.com.wgc.design_system.templates.factories.WgcSearchType
import br.com.wgc.design_system.templates.screens.search.SearchAndFilterScreenTemplate

/**
 * Tela corporativa de Busca Reativa e Filtros Avançados de produtos, lojas e serviços.
 *
 * Utiliza o motor da [WgcSearchFactory] do `DesignSystemAndroid 0.24.1`, permitindo alternar entre
 * busca padrão com filtros de categoria, busca visual assistida por câmera/IA e layouts específicos
 * de delivery de comida ou e-commerce geral.
 *
 * ### 📌 Quando Usar:
 * Utilize esta tela como ponto central de descoberta de produtos ou serviços em qualquer aplicativo
 * do ecossistema WGC, oferecendo resposta imediata a digitação com debounce e filtros de faceta.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * // 1. Uso padrão com ViewModel Hilt:
 * @Composable
 * fun FluxoDeBusca(onItemClick: (String) -> Unit) {
 *     SearchScreen(
 *         searchType = WgcSearchType.STANDARD,
 *         onResultClick = onItemClick
 *     )
 * }
 *
 * // 2. Uso com variante de busca visual:
 * SearchScreen(
 *     searchType = WgcSearchType.VISUAL_SEARCH,
 *     onBackClick = { navegarParaHome() }
 * )
 * ```
 *
 * @param modifier Modificador de layout Compose aplicado ao contêiner da tela.
 * @param searchType Variante visual e comportamental de busca através de [WgcSearchType]. O padrão é [WgcSearchType.STANDARD].
 * @param viewModel ViewModel injetado via Hilt responsável pelo estado da consulta, debounce e filtragem de resultados.
 * @param onResultClick Callback acionado quando o usuário seleciona um item da listagem de resultados.
 * @param onBackClick Callback acionado ao tocar no botão de retorno da navegação.
 */
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchType: WgcSearchType = WgcSearchType.STANDARD,
    viewModel: SearchViewModel = hiltViewModel(),
    onResultClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    if (searchType == WgcSearchType.STANDARD) {
        SearchAndFilterScreenTemplate(
            viewModel = viewModel
        )
    } else {
        WgcSearchFactory(
            modifier = modifier,
            type = searchType,
            onResultClick = onResultClick,
            onBackClick = onBackClick
        )
    }
}