package br.com.wgc.testing.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * Item individual armazenado no carrinho de compras simulado.
 *
 * @property product Produto associado ao item.
 * @property quantity Quantidade adicionada.
 */
data class FakeCartItem(
    val product: FakeProduct,
    val quantity: Int = 1
)

/**
 * Repositório em memória para simulação e teste de carrinho de compras e checkout.
 */
class FakeCartRepository {
    private val _items = MutableStateFlow<Map<String, FakeCartItem>>(emptyMap())

    /** Fluxo contínuo com os itens contidos no carrinho. */
    val items: Flow<List<FakeCartItem>> = _items.asStateFlow().map { it.values.toList() }

    /** Fluxo reativo com a quantidade total de itens no carrinho. */
    val totalCount: Flow<Int> = _items.map { map -> map.values.sumOf { it.quantity } }

    /** Fluxo reativo com o valor monetário total do carrinho. */
    val subtotal: Flow<Double> = _items.map { map ->
        map.values.sumOf { it.product.price * it.quantity }
    }

    /**
     * Adiciona um produto ao carrinho ou incrementa sua quantidade existente.
     *
     * @param product O produto a adicionar.
     * @param quantity Quantidade a incrementar (padrão: 1).
     */
    fun addItem(product: FakeProduct, quantity: Int = 1) {
        val current = _items.value.toMutableMap()
        val existing = current[product.id]
        if (existing != null) {
            current[product.id] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            current[product.id] = FakeCartItem(product, quantity)
        }
        _items.value = current
    }

    /**
     * Remove ou decrementa a quantidade de um produto do carrinho.
     *
     * @param productId Identificador único do produto.
     */
    fun removeItem(productId: String) {
        val current = _items.value.toMutableMap()
        current.remove(productId)
        _items.value = current
    }

    /**
     * Esvazia todos os itens do carrinho simulado.
     */
    fun clear() {
        _items.value = emptyMap()
    }
}
