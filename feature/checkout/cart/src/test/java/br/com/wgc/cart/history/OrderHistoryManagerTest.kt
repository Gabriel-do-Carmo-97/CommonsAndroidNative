package br.com.wgc.cart.history

import br.com.wgc.core.sharedPreferences.InMemoryKeyValueStorage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [OrderHistoryManager] validando gravação, recuperação e recompras em 1 clique.
 */
class OrderHistoryManagerTest {

    private lateinit var storage: InMemoryKeyValueStorage
    private lateinit var orderHistoryManager: OrderHistoryManager

    @Before
    fun setup() {
        storage = InMemoryKeyValueStorage()
        orderHistoryManager = OrderHistoryManager(storage)
    }

    @Test
    fun `recordOrder saves order and retrieves it correctly`() {
        val order = HistoricalOrder(
            orderId = "ord-99",
            orderNumber = "#WGC-99",
            dateFormatted = "15/10/2026",
            items = listOf(
                HistoricalOrderItem(id = "item-1", title = "Café Gourmet", unitPrice = "R$ 18,00", quantity = 2),
                HistoricalOrderItem(id = "item-2", title = "Pão de Queijo", unitPrice = "R$ 6,00", quantity = 3)
            ),
            totalFormatted = "R$ 54,00",
            status = HistoricalOrderStatus.DELIVERED
        )

        orderHistoryManager.recordOrder(order)

        val orders = orderHistoryManager.getOrders()
        assertEquals(1, orders.size)
        assertEquals("ord-99", orders[0].orderId)
        assertEquals("#WGC-99", orders[0].orderNumber)
        assertEquals(2, orders[0].items.size)
        assertEquals(2, orders[0].items[0].quantity)
        assertEquals(3, orders[0].items[1].quantity)
        assertEquals(HistoricalOrderStatus.DELIVERED, orders[0].status)
    }

    @Test
    fun `getOrderById returns matching order or null`() {
        val order1 = HistoricalOrder(
            orderId = "o-1",
            orderNumber = "#1",
            dateFormatted = "10/10/2026",
            items = emptyList(),
            totalFormatted = "R$ 10,00"
        )
        val order2 = HistoricalOrder(
            orderId = "o-2",
            orderNumber = "#2",
            dateFormatted = "11/10/2026",
            items = emptyList(),
            totalFormatted = "R$ 20,00"
        )

        orderHistoryManager.recordOrder(order1)
        orderHistoryManager.recordOrder(order2)

        val found = orderHistoryManager.getOrderById("o-1")
        assertNotNull(found)
        assertEquals("o-1", found?.orderId)

        val notFound = orderHistoryManager.getOrderById("o-unknown")
        assertNull(notFound)
    }

    @Test
    fun `reorder converts historical order items into cart items with quantities expanded`() {
        val order = HistoricalOrder(
            orderId = "reorder-test",
            orderNumber = "#R1",
            dateFormatted = "12/10/2026",
            items = listOf(
                HistoricalOrderItem(id = "p1", title = "Pizza", unitPrice = "R$ 50,00", quantity = 2),
                HistoricalOrderItem(id = "p2", title = "Suco", unitPrice = "R$ 10,00", quantity = 1)
            ),
            totalFormatted = "R$ 110,00"
        )

        orderHistoryManager.recordOrder(order)

        val cartItems = orderHistoryManager.reorder("reorder-test")
        assertEquals(3, cartItems.size) // 2 pizzas + 1 suco
        assertEquals(2, cartItems.count { it.id == "p1" })
        assertEquals(1, cartItems.count { it.id == "p2" })
    }

    @Test(expected = IllegalArgumentException::class)
    fun `reorder with invalid orderId throws exception`() {
        orderHistoryManager.reorder("non-existent-order")
    }

    @Test
    fun `clearHistory removes all orders`() {
        val order = HistoricalOrder(
            orderId = "o-clear",
            orderNumber = "#C",
            dateFormatted = "12/10/2026",
            items = emptyList(),
            totalFormatted = "R$ 0,00"
        )
        orderHistoryManager.recordOrder(order)
        orderHistoryManager.clearHistory()

        assertTrue(orderHistoryManager.getOrders().isEmpty())
    }
}
