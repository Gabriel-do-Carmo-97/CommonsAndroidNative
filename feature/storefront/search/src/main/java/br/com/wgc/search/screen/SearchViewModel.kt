package br.com.wgc.search.screen

import androidx.lifecycle.viewModelScope
import br.com.wgc.design_system.factories.search.templates.BaseSearchAndFilterViewModel
import br.com.wgc.design_system.factories.search.templates.SearchAndFilterUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel corporativo responsável pela busca textual reativa e filtros por categorias de catálogo.
 *
 * Implementa debounce automático de 300ms para evitar recomposições excessivas e requisições concorrentes,
 * mantendo a resposta visual suave para o usuário.
 *
 * ### 📌 Quando Usar:
 * Utilize este ViewModel ao montar a tela de pesquisa (`SearchScreen`) em qualquer vertical de negócio
 * (varejo, gastronomia, serviços ou farmácia), provendo uma experiência de filtragem em tempo real.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun MinhaTelaDeBusca(
 *     viewModel: SearchViewModel = hiltViewModel()
 * ) {
 *     val state by viewModel.uiState.collectAsState()
 *
 *     // Alteração da consulta com debounce de 300ms
 *     OutlinedTextField(
 *         value = state.searchQuery,
 *         onValueChange = { viewModel.onSearchQueryChange(it) }
 *     )
 *
 *     SearchAndFilterScreenTemplate(viewModel = viewModel)
 * }
 * ```
 */
@HiltViewModel
class SearchViewModel @Inject constructor() : BaseSearchAndFilterViewModel() {

    private val allCategories = listOf("Todos", "Pizzas", "Bebidas", "Sobremesas", "Combos", "Lanches")

    private val databaseItems = listOf(
        "Pizza Calabresa Tradicional",
        "Pizza Quatro Queijos Premium",
        "Pizza Portuguesa Especial",
        "Pizza Frango com Catupiry",
        "Refrigerante Coca-Cola 2L",
        "Refrigerante Guaraná Antarctica 2L",
        "Suco de Laranja Natural 500ml",
        "Pudim de Leite Condensado Caseiro",
        "Torta Holandesa de Chocolate",
        "Combo Burger Artesanal + Batata",
        "X-Salada Especial com Molho da Casa"
    )

    private val _uiState = MutableStateFlow(
        SearchAndFilterUiState(
            searchQuery = "",
            selectedCategory = "Todos",
            categories = allCategories,
            results = databaseItems
        )
    )

    /**
     * Fluxo observável com o estado da pesquisa e a lista filtrada de resultados.
     */
    override val uiState: StateFlow<SearchAndFilterUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    /**
     * Trata a alteração do texto de busca, cancelando buscas anteriores e aplicando debounce de 300ms.
     *
     * ### 📌 Quando Usar:
     * Acionado no callback `onValueChange` da barra de pesquisa sempre que o usuário digita ou apaga caracteres.
     *
     * @param query Termo de busca digitado pelo usuário.
     */
    override fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(DEBOUNCE_DELAY_MS)
            filterResults()
        }
    }

    /**
     * Aplica o filtro pela categoria selecionada.
     *
     * ### 📌 Quando Usar:
     * Acionado quando o usuário clica em uma das pílulas/chips de categoria.
     *
     * @param category Nome da categoria alvo selecionada.
     */
    override fun onCategorySelect(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        filterResults()
    }

    /**
     * Aplica os critérios de texto e categoria sobre o conjunto de itens disponíveis.
     */
    private fun filterResults() {
        val query = _uiState.value.searchQuery.trim().lowercase()
        val category = _uiState.value.selectedCategory

        val filtered = databaseItems.filter { item ->
            val matchesQuery = query.isEmpty() || item.lowercase().contains(query)
            val matchesCategory = when (category) {
                "Pizzas" -> item.startsWith("Pizza")
                "Bebidas" -> item.startsWith("Refrigerante") || item.startsWith("Suco")
                "Sobremesas" -> item.startsWith("Pudim") || item.startsWith("Torta")
                "Combos", "Lanches" -> item.startsWith("Combo") || item.startsWith("X-")
                else -> true
            }
            matchesQuery && matchesCategory
        }

        _uiState.update { it.copy(results = filtered) }
    }

    companion object {
        private const val DEBOUNCE_DELAY_MS = 300L
    }
}