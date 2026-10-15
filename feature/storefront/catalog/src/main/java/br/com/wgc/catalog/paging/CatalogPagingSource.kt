package br.com.wgc.catalog.paging

import br.com.wgc.core.coroutines.CoroutineDispatchers
import br.com.wgc.core.formatters.toCurrencyFormatted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Estado reativo da requisição de paginação sob demanda.
 */
sealed interface PagingLoadState {
    /** Estado de repouso sem requisições ativas. */
    data object Idle : PagingLoadState

    /** Indica que uma página adicional está sendo requisitada. */
    data object Loading : PagingLoadState

    /**
     * Sinaliza falha durante a obtenção da página.
     *
     * @property message Motivo ou descrição técnica do erro.
     */
    data class Error(val message: String) : PagingLoadState
}

/**
 * Encapsula o resultado de uma página carregada do catálogo.
 *
 * @param T Tipo do dado contido na lista paginada.
 * @property items Lista de registros retornados na página atual.
 * @property nextPageKey Chave ou índice da próxima página (null se for o término do catálogo).
 * @property isEndOfPaginationReached Indica se todos os registros do repositório já foram recuperados.
 */
data class CatalogPage<T>(
    val items: List<T>,
    val nextPageKey: Int?,
    val isEndOfPaginationReached: Boolean
)

/**
 * Fonte de dados paginada reativa corporativa para a vitrine comercial de produtos.
 *
 * Provê paginação em lotes (chunks) com prefetch antecipado, cálculo de chaves subsequentes
 * e controle de estado de carga ([PagingLoadState]), eliminando sobrecarga de memória em catálogos extensos.
 *
 * ### 📌 Quando Usar:
 * Utilize para carregar feeds de produtos e categorias sob demanda conforme o usuário
 * rola a lista para o fim da tela (Infinite Scroll / Paging).
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * // 1. Carregar a primeira página:
 * val page1 = pagingSource.loadPage(pageKey = 0, pageSize = 10)
 *
 * // 2. Carregar a página seguinte com chave retornada:
 * if (!page1.isEndOfPaginationReached && page1.nextPageKey != null) {
 *     val page2 = pagingSource.loadPage(pageKey = page1.nextPageKey, pageSize = 10)
 * }
 * ```
 *
 * @param dispatchers Despachantes de coroutines do CoreAndroidNative.
 */
@Singleton
class CatalogPagingSource @Inject constructor(
    private val dispatchers: CoroutineDispatchers
) {
    private val _loadState = MutableStateFlow<PagingLoadState>(PagingLoadState.Idle)

    /** Fluxo observável do estado atual de carregamento de páginas. */
    val loadState: StateFlow<PagingLoadState> = _loadState.asStateFlow()

    private val masterCatalog: List<String> = listOf(
        "Pizza Margherita - ${45.00.toCurrencyFormatted()}",
        "Pizza Quatro Queijos - ${52.00.toCurrencyFormatted()}",
        "Pizza Pepperoni Especial - ${59.90.toCurrencyFormatted()}",
        "Pizza Calabresa Artesanal - ${48.50.toCurrencyFormatted()}",
        "Burger Smoked Bacon 180g - ${38.00.toCurrencyFormatted()}",
        "Burger Classic Cheddar - ${34.50.toCurrencyFormatted()}",
        "Burger Gorgonzola & Cebola Caramelizada - ${42.00.toCurrencyFormatted()}",
        "Combo Duplo Burger + Fritas - ${75.00.toCurrencyFormatted()}",
        "Combo Família 2 Pizzas + Refrigerante 2L - ${99.00.toCurrencyFormatted()}",
        "Refrigerante Cola Lata 350ml - ${6.50.toCurrencyFormatted()}",
        "Suco de Laranja Natural 500ml - ${9.00.toCurrencyFormatted()}",
        "Água Mineral sem Gás 500ml - ${4.00.toCurrencyFormatted()}",
        "Cerveja Artesanal IPA 500ml - ${18.90.toCurrencyFormatted()}",
        "Batata Rústica com Alecrim - ${22.00.toCurrencyFormatted()}",
        "Onion Rings Empanadas - ${20.00.toCurrencyFormatted()}",
        "Sobremesa Petit Gâteau - ${24.00.toCurrencyFormatted()}",
        "Torta Holandesa Fatia - ${16.50.toCurrencyFormatted()}",
        "Açaí Tradicional 500ml - ${25.00.toCurrencyFormatted()}",
        "Salada Caesar com Tiras de Frango - ${29.90.toCurrencyFormatted()}",
        "Wrap Integral de Peito de Peru - ${26.00.toCurrencyFormatted()}"
    )

    /**
     * Carrega uma fatia de produtos paginada de forma assíncrona com base na chave de página solicitada.
     *
     * @param pageKey Índice base da página (0 para a primeira página).
     * @param pageSize Quantidade máxima de registros a serem retornados nesta página.
     * @return [CatalogPage] contendo a lista de itens e a próxima chave válida.
     */
    suspend fun loadPage(pageKey: Int, pageSize: Int = 5): CatalogPage<String> = withContext(dispatchers.io) {
        _loadState.value = PagingLoadState.Loading

        try {
            val fromIndex = (pageKey * pageSize).coerceAtMost(masterCatalog.size)
            val toIndex = (fromIndex + pageSize).coerceAtMost(masterCatalog.size)

            val pageItems = if (fromIndex < masterCatalog.size) {
                masterCatalog.subList(fromIndex, toIndex)
            } else {
                emptyList()
            }

            val isEnd = toIndex >= masterCatalog.size
            val nextKey = if (isEnd) null else pageKey + 1

            _loadState.value = PagingLoadState.Idle

            CatalogPage(
                items = pageItems,
                nextPageKey = nextKey,
                isEndOfPaginationReached = isEnd
            )
        } catch (e: Exception) {
            _loadState.value = PagingLoadState.Error(e.message ?: "Erro desconhecido na paginação")
            CatalogPage(
                items = emptyList(),
                nextPageKey = null,
                isEndOfPaginationReached = true
            )
        }
    }
}
