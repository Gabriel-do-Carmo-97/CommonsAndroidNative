package br.com.wgc.catalog.screen

import androidx.lifecycle.viewModelScope
import br.com.wgc.core.coroutines.CoroutineDispatchers
import br.com.wgc.core.formatters.toCurrencyFormatted
import br.com.wgc.design_system.templates.screens.home.ecommerce.BaseEcommerceHomeViewModel
import br.com.wgc.design_system.templates.screens.home.ecommerce.EcommerceHomeUiState
import br.wgc.omnibackend.core.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel corporativo responsável pelo gerenciamento de catálogo, vitrines dinâmicas e sincronização via OmniBackend.
 *
 * Conecta-se em tempo real à coleção do Firestore (`catalog_products`) para sincronização
 * contínua de produtos e categorias, formatando valores monetários via [toCurrencyFormatted]
 * e mantendo fallback resiliente em memória caso a conexão esteja offline.
 *
 * ### 📌 Quando Usar:
 * Utilize este ViewModel na tela inicial de vitrine comercial (`CatalogScreen`) ou módulos
 * de e-commerce onde seja necessário exibir categorias de produtos filtráveis e feeds em tempo real.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun TelaDeCatalogo(
 *     viewModel: CatalogViewModel = hiltViewModel()
 * ) {
 *     val uiState by viewModel.uiState.collectAsState()
 *
 *     // Alternar categoria
 *     CategoryChips(
 *         categories = uiState.categories,
 *         selected = uiState.selectedCategory,
 *         onSelect = { viewModel.selectCategory(it) }
 *     )
 * }
 * ```
 *
 * @param firestoreRepository Repositório do OmniBackend Firestore para recuperação do catálogo remoto.
 * @param dispatchers Provedor unificado de CoroutineDispatchers do CoreAndroidNative.
 */
@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val dispatchers: CoroutineDispatchers
) : BaseEcommerceHomeViewModel() {

    private val sampleCategories = listOf("Todos", "Destaques", "Promoções", "Bebidas", "Combos")

    private val sampleProducts = listOf(
        "Pizza Pepperoni Especial - ${59.90.toCurrencyFormatted()}",
        "Burger Artesanal Costela - ${38.00.toCurrencyFormatted()}",
        "Combo Família 2 Pizzas + Refri - ${99.00.toCurrencyFormatted()}",
        "Refrigerante Lata 350ml - ${6.50.toCurrencyFormatted()}",
        "Sobremesa Petit Gateau - ${22.00.toCurrencyFormatted()}"
    )

    private val _uiState = MutableStateFlow(
        EcommerceHomeUiState(
            searchQuery = "",
            categories = sampleCategories,
            selectedCategory = "Todos",
            featuredProducts = sampleProducts
        )
    )

    /** Fluxo de estado imutável da vitrine de e-commerce consumido pela interface Compose. */
    override val uiState: StateFlow<EcommerceHomeUiState> = _uiState.asStateFlow()

    init {
        loadCatalogFromFirestore()
    }

    /**
     * Inicia a escuta em tempo real da coleção de produtos no Firestore do OmniBackend.
     * Atualiza os produtos em destaque conforme alterações ocorrem na nuvem.
     */
    private fun loadCatalogFromFirestore() {
        viewModelScope.launch(dispatchers.main) {
            try {
                firestoreRepository.listenToCollection(
                    collection = "catalog_products",
                    filters = emptyList(),
                    clazz = Map::class.java
                ).collect { result ->
                    when (result) {
                        is br.wgc.omnibackend.core.utils.DataResult.Success -> {
                            val remoteItems = result.data.mapNotNull { doc ->
                                val name = doc["name"] as? String
                                val priceNum = (doc["price"] as? Number)?.toDouble() ?: doc["price"]?.toString()?.toDoubleOrNull()
                                if (name != null && priceNum != null) {
                                    "$name - ${priceNum.toCurrencyFormatted()}"
                                } else if (name != null) {
                                    name
                                } else null
                            }
                            if (remoteItems.isNotEmpty()) {
                                _uiState.update { current ->
                                    current.copy(featuredProducts = remoteItems)
                                }
                            }
                        }
                        is br.wgc.omnibackend.core.utils.DataResult.Failure -> {
                            // Mantém produtos padrão em caso de erro transitório de rede
                        }
                    }
                }
            } catch (_: Exception) {
                // Preserva os produtos default caso o stream falhe
            }
        }
    }

    /**
     * Trata o clique no ícone do carrinho de compras disparado pelo template visual.
     *
     * ### 📌 Quando Usar:
     * Disparado quando o usuário toca no botão de carrinho no topo da tela do catálogo.
     */
    override fun onCartClick() {
        // Redirecionamento de navegação para a tela de carrinho
    }

    /**
     * Atualiza a categoria de produtos atualmente selecionada no filtro da vitrine.
     *
     * ### 📌 Quando Usar:
     * Invocado quando o usuário clica em um chip ou aba de categoria na interface.
     *
     * ### 🛠️ Modo de Usar:
     * ```kotlin
     * viewModel.selectCategory("Bebidas")
     * ```
     *
     * @param category Nome da categoria a ser filtrada (ex.: "Bebidas", "Combos", "Todos").
     */
    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
}