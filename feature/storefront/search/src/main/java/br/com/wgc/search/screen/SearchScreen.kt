package br.com.wgc.search.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.wgc.design_system.factories.search.WgcSearchScreenFactory
import br.com.wgc.design_system.factories.search.WgcSearchVariant
import br.com.wgc.design_system.factories.search.templates.SearchAndFilterScreenTemplate

/**
 * Tela corporativa de Busca Reativa e Filtros Avançados de produtos, lojas e serviços.
 *
 * Utiliza o motor da [WgcSearchScreenFactory] do `DesignSystemAndroid 0.25.0`, permitindo alternar entre
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
 *         searchType = WgcSearchVariant.STANDARD,
 *         onResultClick = onItemClick
 *     )
 * }
 *
 * // 2. Uso com variante de busca visual:
 * SearchScreen(
 *     searchType = WgcSearchVariant.VISUAL_SEARCH,
 *     onBackClick = { navegarParaHome() }
 * )
 * ```
 *
 * @param modifier Modificador de layout Compose aplicado ao contêiner da tela.
 * @param searchType Variante visual e comportamental de busca através de [WgcSearchVariant]. O padrão é [WgcSearchVariant.STANDARD].
 * @param viewModel ViewModel injetado via Hilt responsável pelo estado da consulta, debounce e filtragem de resultados.
 * @param onResultClick Callback acionado quando o usuário seleciona um item da listagem de resultados.
 * @param onBackClick Callback acionado ao tocar no botão de retorno da navegação.
 */
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchType: WgcSearchVariant = WgcSearchVariant.STANDARD,
    viewModel: SearchViewModel = hiltViewModel(),
    onResultClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    if (searchType == WgcSearchVariant.STANDARD) {
        SearchAndFilterScreenTemplate(
            viewModel = viewModel
        )
    } else {
        WgcSearchScreenFactory(
            modifier = modifier,
            variant = searchType,
            onResultClick = onResultClick,
            onBackClick = onBackClick
        )
    }
}