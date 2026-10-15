package br.com.wgc.cart.history

import br.com.wgc.core.sharedPreferences.KeyValueStorage
import br.com.wgc.design_system.templates.screens.cart.CartItem
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Status de ciclo de vida de um pedido no histórico.
 */
enum class HistoricalOrderStatus {
    /** Pedido entregue ao destinatário com sucesso. */
    DELIVERED,
    /** Pedido em trânsito com entregador a caminho. */
    IN_TRANSIT,
    /** Pedido sendo separado ou preparado pelo lojista. */
    PROCESSING,
    /** Pedido cancelado pelo cliente ou estornado. */
    CANCELLED
}

/**
 * Item individual pertencente a um pedido histórico.
 *
 * @property id Identificador único do produto ou SKU.
 * @property title Nome comercial do produto.
 * @property unitPrice Preço unitário formatado (ex: "R$ 49,90").
 * @property quantity Quantidade de unidades adquiridas.
 */
data class HistoricalOrderItem(
    val id: String,
    val title: String,
    val unitPrice: String,
    val quantity: Int = 1
)

/**
 * Representação completa de um pedido consolidado no histórico de compras do usuário.
 *
 * @property orderId Identificador único universal do pedido (UUID).
 * @property orderNumber Código amigável visível ao cliente (ex: "#WGC-84920").
 * @property dateFormatted Data e hora formatadas da compra (ex: "14/10/2026 às 19:42").
 * @property items Lista de itens comprados neste pedido.
 * @property totalFormatted Valor total pago com frete incluso.
 * @property status Estado de entrega e processamento do pedido.
 */
data class HistoricalOrder(
    val orderId: String,
    val orderNumber: String,
    val dateFormatted: String,
    val items: List<HistoricalOrderItem>,
    val totalFormatted: String,
    val status: HistoricalOrderStatus = HistoricalOrderStatus.DELIVERED
)

/**
 * Gerenciador corporativo de Histórico de Pedidos e Recompra Instantânea em 1 Clique.
 *
 * Permite ao usuário consultar compras passadas, inspecionar detalhes fiscais e operacionais,
 * e recriar o carrinho de compras imediatamente com um único toque ("Pedir Novamente").
 *
 * ### 📌 Quando Usar:
 * Utilize na tela de Histórico de Pedidos do perfil do usuário ou na visualização detalhada
 * de pedidos anteriores para disparar a recompra com adição direta ao carrinho ativo.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * // 1. Registrar um pedido recém-concluído no histórico:
 * val newOrder = HistoricalOrder(
 *     orderId = "ord-1234",
 *     orderNumber = "#WGC-9821",
 *     dateFormatted = "15/10/2026 10:30",
 *     items = listOf(HistoricalOrderItem("prod-1", "Hambúrguer Artesanal", "R$ 35,00", 2)),
 *     totalFormatted = "R$ 78,00"
 * )
 * orderHistoryManager.recordOrder(newOrder)
 *
 * // 2. Executar Recompra em 1 Clique (re-adiciona itens ao carrinho):
 * val cartItems = orderHistoryManager.reorder("ord-1234")
 * cartViewModel.restoreCart(cartItems)
 * ```
 *
 * @param storage Implementação de [KeyValueStorage] para persistência segura das compras anteriores.
 */
@Singleton
class OrderHistoryManager @Inject constructor(
    private val storage: KeyValueStorage
) {
    companion object {
        private const val KEY_ORDERS = "wgc_order_history_records"
        private const val ORDER_DELIMITER = "###"
        private const val FIELD_DELIMITER = "@@@"
        private const val ITEM_DELIMITER = "&&&"
        private const val ITEM_FIELD_DELIMITER = ":::"
    }

    /**
     * Registra um novo pedido no histórico persistente do usuário.
     *
     * @param order Dados consolidados do pedido concluído.
     */
    fun recordOrder(order: HistoricalOrder) {
        val currentOrders = getOrders().toMutableList()
        // Remove duplicata se já existir com mesmo ID
        currentOrders.removeAll { it.orderId == order.orderId }
        currentOrders.add(0, order) // Inserção no topo (mais recente primeiro)
        saveOrders(currentOrders)
    }

    /**
     * Retorna a lista completa de pedidos históricos, ordenados do mais recente para o mais antigo.
     *
     * @return Lista de [HistoricalOrder] persistidos.
     */
    fun getOrders(): List<HistoricalOrder> {
        val serializedData = storage.getString(KEY_ORDERS, "") ?: ""
        if (serializedData.isBlank()) return emptyList()

        return try {
            serializedData.split(ORDER_DELIMITER)
                .filter { it.isNotBlank() }
                .mapNotNull { deserializeOrder(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Busca um pedido específico pelo seu [orderId].
     *
     * @param orderId Identificador único do pedido.
     * @return [HistoricalOrder] correspondente ou null se não for encontrado.
     */
    fun getOrderById(orderId: String): HistoricalOrder? {
        return getOrders().find { it.orderId == orderId }
    }

    /**
     * Converte os itens de um pedido passado em uma lista de [CartItem] pronta para alimentar o carrinho ativo.
     *
     * Se um item possuir quantidade > 1, gera instâncias correspondentes para recomposição integral da cesta.
     *
     * @param orderId Identificador do pedido a ser recomprado.
     * @return Lista de [CartItem] para ser injetada no carrinho de compras.
     * @throws IllegalArgumentException se o pedido especificado não for localizado.
     */
    fun reorder(orderId: String): List<CartItem> {
        val order = getOrderById(orderId)
            ?: throw IllegalArgumentException("Pedido com ID $orderId não foi encontrado para recompra.")

        val cartItems = mutableListOf<CartItem>()
        order.items.forEach { item ->
            repeat(item.quantity) {
                cartItems.add(
                    CartItem(
                        id = item.id,
                        title = item.title,
                        price = item.unitPrice
                    )
                )
            }
        }
        return cartItems
    }

    /**
     * Limpa todo o histórico de compras registrado.
     */
    fun clearHistory() {
        storage.remove(KEY_ORDERS)
    }

    private fun saveOrders(orders: List<HistoricalOrder>) {
        val serialized = orders.joinToString(ORDER_DELIMITER) { serializeOrder(it) }
        storage.saveString(KEY_ORDERS, serialized)
    }

    private fun serializeOrder(order: HistoricalOrder): String {
        val serializedItems = order.items.joinToString(ITEM_DELIMITER) { item ->
            "${item.id}$ITEM_FIELD_DELIMITER${item.title}$ITEM_FIELD_DELIMITER${item.unitPrice}$ITEM_FIELD_DELIMITER${item.quantity}"
        }
        return "${order.orderId}$FIELD_DELIMITER${order.orderNumber}$FIELD_DELIMITER${order.dateFormatted}$FIELD_DELIMITER$serializedItems$FIELD_DELIMITER${order.totalFormatted}$FIELD_DELIMITER${order.status.name}"
    }

    private fun deserializeOrder(raw: String): HistoricalOrder? {
        val parts = raw.split(FIELD_DELIMITER)
        if (parts.size < 6) return null

        val items = if (parts[3].isBlank()) {
            emptyList()
        } else {
            parts[3].split(ITEM_DELIMITER).mapNotNull { itemRaw ->
                val itemParts = itemRaw.split(ITEM_FIELD_DELIMITER)
                if (itemParts.size >= 4) {
                    HistoricalOrderItem(
                        id = itemParts[0],
                        title = itemParts[1],
                        unitPrice = itemParts[2],
                        quantity = itemParts[3].toIntOrNull() ?: 1
                    )
                } else null
            }
        }

        val status = try {
            HistoricalOrderStatus.valueOf(parts[5])
        } catch (e: Exception) {
            HistoricalOrderStatus.DELIVERED
        }

        return HistoricalOrder(
            orderId = parts[0],
            orderNumber = parts[1],
            dateFormatted = parts[2],
            items = items,
            totalFormatted = parts[4],
            status = status
        )
    }
}
