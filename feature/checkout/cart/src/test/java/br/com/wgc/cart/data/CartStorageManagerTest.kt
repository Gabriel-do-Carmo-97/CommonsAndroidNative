package br.com.wgc.cart.data

import br.com.wgc.core.sharedPreferences.InMemoryKeyValueStorage
import br.com.wgc.design_system.factories.cart.templates.CartItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [CartStorageManager] validando persistência multi-sessão e serialização.
 */
class CartStorageManagerTest {

    private lateinit var storage: InMemoryKeyValueStorage
    private lateinit var cartStorageManager: CartStorageManager

    @Before
    fun setup() {
        storage = InMemoryKeyValueStorage()
        cartStorageManager = CartStorageManager(storage)
    }

    @Test
    fun `saveCart correctly persists items and loadCart restores them`() {
        val items = listOf(
            CartItem(id = "c1", title = "Pizza Especial", price = "R$ 55,00"),
            CartItem(id = "c2", title = "Refrigerante 2L", price = "R$ 12,00")
        )

        assertFalse(cartStorageManager.hasSavedCart())

        cartStorageManager.saveCart(items, subtotal = "R$ 67,00", deliveryFee = "R$ 8,00")

        assertTrue(cartStorageManager.hasSavedCart())

        val snapshot = cartStorageManager.loadCart()
        assertNotNull(snapshot)
        assertEquals(2, snapshot?.items?.size)
        assertEquals("R$ 67,00", snapshot?.subtotal)
        assertEquals("R$ 8,00", snapshot?.deliveryFee)
    }

    @Test
    fun `clearCart removes all saved data`() {
        val items = listOf(CartItem(id = "c1", title = "Burger", price = "R$ 30,00"))
        cartStorageManager.saveCart(items, subtotal = "R$ 30,00", deliveryFee = "R$ 5,00")

        assertTrue(cartStorageManager.hasSavedCart())

        cartStorageManager.clearCart()

        assertFalse(cartStorageManager.hasSavedCart())
        assertNull(cartStorageManager.loadCart())
    }
}
