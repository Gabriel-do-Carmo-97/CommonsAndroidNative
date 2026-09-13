package br.com.wgc.testing.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * Modelo de entidade de produto para testes unitários e de integração de vitrine/catálogo.
 *
 * @property id Identificador único do produto.
 * @property title Nome comercial do produto.
 * @property price Preço unitário.
 * @property category Categoria de catálogo.
 * @property inStock Indica disponibilidade de estoque.
 */
data class FakeProduct(
    val id: String,
    val title: String,
    val price: Double,
    val category: String,
    val inStock: Boolean = true
)

/**
 * Repositório em memória para simulação de catálogo de produtos em testes.
 *
 * @param initialProducts Lista inicial de produtos pré-carregados.
 */
class FakeCatalogRepository(
    initialProducts: List<FakeProduct> = defaultProducts
) {
    private val _products = MutableStateFlow(initialProducts)

    /** Fluxo contínuo com a lista de todos os produtos do catálogo. */
    val products: Flow<List<FakeProduct>> = _products.asStateFlow()

    /**
     * Busca um produto pelo seu identificador.
     *
     * @param productId Identificador único do produto desejado.
     * @return O [FakeProduct] correspondente ou nulo se não existir.
     */
    fun getProductById(productId: String): FakeProduct? {
        return _products.value.firstOrNull { it.id == productId }
    }

    /**
     * Filtra produtos pelo nome da categoria.
     *
     * @param category Nome da categoria de busca.
     * @return Fluxo com a lista de produtos da categoria especificada.
     */
    fun getProductsByCategory(category: String): Flow<List<FakeProduct>> {
        return _products.map { list -> list.filter { it.category.equals(category, ignoreCase = true) } }
    }

    /**
     * Adiciona ou atualiza um produto no catálogo simulado.
     *
     * @param product O produto a ser inserido ou modificado.
     */
    fun addProduct(product: FakeProduct) {
        _products.value = _products.value.filterNot { it.id == product.id } + product
    }

    /**
     * Remove um produto do catálogo simulado.
     *
     * @param productId Identificador do produto a remover.
     */
    fun removeProduct(productId: String) {
        _products.value = _products.value.filterNot { it.id == productId }
    }

    companion object {
        /** Lista pré-configurada de produtos mockados para cenários de testes imediatos. */
        val defaultProducts = listOf(
            FakeProduct("p1", "Smartphone WGC Pro 5G", 2999.99, "Eletrônicos"),
            FakeProduct("p2", "Notebook WGC Ultra 16GB", 4599.00, "Informática"),
            FakeProduct("p3", "Fone Bluetooth Noise Cancelling", 349.90, "Áudio"),
            FakeProduct("p4", "Smartwatch Fitness Monitor", 499.00, "Wearables")
        )
    }
}
