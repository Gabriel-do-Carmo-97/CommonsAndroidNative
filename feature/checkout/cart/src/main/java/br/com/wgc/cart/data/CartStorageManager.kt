package br.com.wgc.cart.data

import br.com.wgc.core.sharedPreferences.KeyValueStorage
import br.com.wgc.design_system.templates.screens.cart.CartItem
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Snapshot imutável de persistência contendo o estado do carrinho preservado entre sessões.
 *
 * @property items Lista de itens presentes no carrinho.
 * @property subtotal Valor consolidado dos produtos.
 * @property deliveryFee Taxa de entrega calculada no momento do snapshot.
 * @property timestamp Carimbo de data/hora em milissegundos do salvamento.
 */
data class PersistentCartSnapshot(
    val items: List<CartItem>,
    val subtotal: String,
    val deliveryFee: String,
    val timestamp: Long
)

/**
 * Gerenciador corporativo de Persistência Local Multi-Sessão do Carrinho de Compras.
 *
 * Garante que os itens adicionados ao carrinho pelo cliente permaneçam salvos mesmo após o encerramento
 * do processo do aplicativo, queda de bateria ou alternância de telas, utilizando a infraestrutura
 * de [KeyValueStorage] do `core-storage 1.5.1`.
 *
 * ### 📌 Quando Usar:
 * Injete no [br.com.wgc.cart.screen.CartViewModel] para carregar o carrinho salvo na inicialização
 * e persistir alterações imediatamente após cada adição ou remoção de item.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * // 1. Salvar o estado atual do carrinho:
 * cartStorageManager.saveCart(items = currentItems, subtotal = "R$ 60,00", deliveryFee = "R$ 8,00")
 *
 * // 2. Recuperar itens salvos na abertura do app:
 * val snapshot = cartStorageManager.loadCart()
 * if (snapshot != null) {
 *     restaurarCarrinho(snapshot.items)
 * }
 * ```
 *
 * @param storage Implementação de [KeyValueStorage] (criptografada em produção, in-memory em testes).
 */
@Singleton
class CartStorageManager @Inject constructor(
    private val storage: KeyValueStorage
) {
    companion object {
        private const val KEY_CART_ITEMS = "wgc_cart_persistent_items"
        private const val KEY_CART_SUBTOTAL = "wgc_cart_persistent_subtotal"
        private const val KEY_CART_DELIVERY_FEE = "wgc_cart_persistent_delivery"
        private const val KEY_CART_TIMESTAMP = "wgc_cart_persistent_time"
        private const val DELIMITER = ";;;"
    }

    /**
     * Persiste o estado completo do carrinho de compras de forma síncrona/atômica.
     *
     * @param items Lista de itens do carrinho.
     * @param subtotal Subtotal dos produtos formatado.
     * @param deliveryFee Taxa de entrega calculada.
     */
    fun saveCart(items: List<CartItem>, subtotal: String, deliveryFee: String) {
        val serializedItems = items.map { item ->
            "${item.id}$DELIMITER${item.title}$DELIMITER${item.price}"
        }.toSet()

        storage.saveStringSet(KEY_CART_ITEMS, serializedItems)
        storage.saveString(KEY_CART_SUBTOTAL, subtotal)
        storage.saveString(KEY_CART_DELIVERY_FEE, deliveryFee)
        storage.saveLong(KEY_CART_TIMESTAMP, System.currentTimeMillis())
    }

    /**
     * Recupera o snapshot do carrinho persistido, retornando null caso o armazenamento esteja vazio.
     *
     * @return [PersistentCartSnapshot] ou null se não houver registros.
     */
    fun loadCart(): PersistentCartSnapshot? {
        val serializedItems = storage.getStringSet(KEY_CART_ITEMS, emptySet())
        if (serializedItems.isEmpty()) return null

        val items = serializedItems.mapNotNull { record ->
            val parts = record.split(DELIMITER)
            if (parts.size >= 3) {
                CartItem(id = parts[0], title = parts[1], price = parts[2])
            } else null
        }

        val subtotal = storage.getString(KEY_CART_SUBTOTAL, "R$ 0,00") ?: "R$ 0,00"
        val deliveryFee = storage.getString(KEY_CART_DELIVERY_FEE, "R$ 0,00") ?: "R$ 0,00"
        val timestamp = storage.getLong(KEY_CART_TIMESTAMP, 0L)

        return PersistentCartSnapshot(
            items = items,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            timestamp = timestamp
        )
    }

    /**
     * Remove todos os dados persistidos do carrinho após conclusão da compra ou limpeza explícita.
     */
    fun clearCart() {
        storage.remove(KEY_CART_ITEMS)
        storage.remove(KEY_CART_SUBTOTAL)
        storage.remove(KEY_CART_DELIVERY_FEE)
        storage.remove(KEY_CART_TIMESTAMP)
    }

    /**
     * Verifica se existe um carrinho previamente salvo com itens válidos.
     *
     * @return True se houver ao menos um item salvo no armazenamento local.
     */
    fun hasSavedCart(): Boolean {
        return storage.getStringSet(KEY_CART_ITEMS, emptySet()).isNotEmpty()
    }
}
