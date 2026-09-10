package br.com.wgc.search.screen

import androidx.lifecycle.viewModelScope
import br.com.wgc.ds_templates.screens.search.BaseSearchAndFilterViewModel
import br.com.wgc.ds_templates.screens.search.SearchAndFilterUiState
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
 * ViewModel que implementa busca textual instantÃ¢nea com mecanismo de debounce reativo.
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
        "Refrigerante GuaranÃ¡ Antarctica 2L",
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
    override val uiState: StateFlow<SearchAndFilterUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    /**
     * Trata a alteraÃ§Ã£o do texto de busca, aplicando debounce de 300ms antes do filtro.
     *
     * @param query Termo de busca digitado pelo usuÃ¡rio.
     */
    override fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            filterResults()
        }
    }

    /**
     * Aplica o filtro pela categoria selecionada.
     *
     * @param category Nome da categoria alvo.
     */
    override fun onCategorySelect(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        filterResults()
    }

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
}