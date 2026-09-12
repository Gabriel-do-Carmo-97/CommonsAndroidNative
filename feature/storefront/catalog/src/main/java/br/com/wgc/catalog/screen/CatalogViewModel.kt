package br.com.wgc.catalog.screen

import androidx.lifecycle.viewModelScope
import br.com.wgc.ds_templates.screens.home.ecommerce.BaseEcommerceHomeViewModel
import br.com.wgc.ds_templates.screens.home.ecommerce.EcommerceHomeUiState
import br.wgc.omnibackend.core.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel que gerencia a vitrine de produtos, categorias ativas e busca de catálogo.
 *
 * @param firestoreRepository Repositório do OmniBackend Firestore para recuperação do catálogo remoto.
 */
@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : BaseEcommerceHomeViewModel() {

    private val sampleCategories = listOf("Todos", "Destaques", "Promoções", "Bebidas", "Combos")

    private val sampleProducts = listOf(
        "Pizza Pepperoni Especial - R$ 59,90",
        "Burger Artesanal Costela - R$ 38,00",
        "Combo Família 2 Pizzas + Refri - R$ 99,00",
        "Refrigerante Lata 350ml - R$ 6,50",
        "Sobremesa Petit Gateau - R$ 22,00"
    )

    private val _uiState = MutableStateFlow(
        EcommerceHomeUiState(
            searchQuery = "",
            categories = sampleCategories,
            selectedCategory = "Todos",
            featuredProducts = sampleProducts
        )
    )
    override val uiState: StateFlow<EcommerceHomeUiState> = _uiState.asStateFlow()

    init {
        loadCatalogFromFirestore()
    }

    private fun loadCatalogFromFirestore() {
        if (firestoreRepository == null) return
        viewModelScope.launch {
            try {
                // Tenta carregar os produtos remotos do OmniBackend Firestore
            } catch (_: Exception) {
                // Mantém produtos padrão
            }
        }
    }

    /** Trata o clique no ícone do carrinho de compras. */
    override fun onCartClick() {
        // Ação de abertura do carrinho disparada pelo template
    }

    /**
     * Atualiza a categoria de produtos atualmente selecionada no filtro da vitrine.
     *
     * @param category Nome da categoria selecionada.
     */
    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
}