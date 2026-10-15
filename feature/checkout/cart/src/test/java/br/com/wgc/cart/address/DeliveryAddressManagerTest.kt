package br.com.wgc.cart.address

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [DeliveryAddressManager] validando gestão e seleção de múltiplos endereços.
 */
class DeliveryAddressManagerTest {

    private lateinit var addressManager: DeliveryAddressManager

    @Before
    fun setup() {
        addressManager = DeliveryAddressManager()
    }

    @Test
    fun `initial addresses contains Home and Work defaults`() {
        val list = addressManager.addresses.value
        assertEquals(2, list.size)
        assertEquals("Casa", list[0].label)
        assertEquals("Trabalho", list[1].label)
        assertEquals("Casa", addressManager.selectedAddress.value?.label)
    }

    @Test
    fun `addAddress with isDefault true updates selectedAddress`() {
        val newAddr = DeliveryAddress(
            id = "new_01",
            label = "Casa de Praia",
            street = "Av. Beira Mar",
            number = "100",
            neighborhood = "Centro",
            city = "Santos",
            state = "SP",
            cep = "11000-000",
            isDefault = true
        )

        addressManager.addAddress(newAddr)

        assertEquals(3, addressManager.addresses.value.size)
        assertEquals("Casa de Praia", addressManager.selectedAddress.value?.label)
    }

    @Test
    fun `selectAddress switches active delivery address`() {
        addressManager.selectAddress("addr_default_02")
        assertEquals("Trabalho", addressManager.selectedAddress.value?.label)
    }

    @Test
    fun `deleteAddress removes item from list`() {
        addressManager.deleteAddress("addr_default_01")
        assertEquals(1, addressManager.addresses.value.size)
        assertEquals("Trabalho", addressManager.selectedAddress.value?.label)
    }
}
