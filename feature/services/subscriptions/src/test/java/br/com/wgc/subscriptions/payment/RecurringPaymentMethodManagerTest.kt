package br.com.wgc.subscriptions.payment

import br.com.wgc.core.sharedPreferences.InMemoryKeyValueStorage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [RecurringPaymentMethodManager] validando inclusão de cartões,
 * seleção de cartão primário e rotação automática ao remover o padrão.
 */
class RecurringPaymentMethodManagerTest {

    private lateinit var storage: InMemoryKeyValueStorage
    private lateinit var manager: RecurringPaymentMethodManager

    @Before
    fun setup() {
        storage = InMemoryKeyValueStorage()
        manager = RecurringPaymentMethodManager(storage)
    }

    @Test
    fun `first card saved automatically becomes primary`() {
        val card1 = RecurringCard(
            cardToken = "tok_1",
            lastFourDigits = "1111",
            brand = "VISA",
            cardholderName = "Gabriel",
            expiryMonth = 12,
            expiryYear = 2028,
            isPrimary = false
        )

        manager.saveCard(card1)

        val primary = manager.getPrimaryCard()
        assertNotNull(primary)
        assertEquals("tok_1", primary?.cardToken)
        assertTrue(primary?.isPrimary == true)
    }

    @Test
    fun `setPrimaryCard switches primary status between cards`() {
        val card1 = RecurringCard("tok_1", "1111", "VISA", "Gabriel", 12, 2028)
        val card2 = RecurringCard("tok_2", "2222", "MASTERCARD", "Gabriel", 8, 2029)

        manager.saveCard(card1)
        manager.saveCard(card2)

        manager.setPrimaryCard("tok_2")

        val primary = manager.getPrimaryCard()
        assertEquals("tok_2", primary?.cardToken)

        val cards = manager.getCards()
        assertEquals(1, cards.count { it.isPrimary })
    }

    @Test
    fun `removing primary card promotes remaining card to primary`() {
        val card1 = RecurringCard("tok_1", "1111", "VISA", "Gabriel", 12, 2028, isPrimary = true)
        val card2 = RecurringCard("tok_2", "2222", "MASTERCARD", "Gabriel", 8, 2029, isPrimary = false)

        manager.saveCard(card1)
        manager.saveCard(card2)

        manager.removeCard("tok_1")

        val cards = manager.getCards()
        assertEquals(1, cards.size)
        assertEquals("tok_2", cards[0].cardToken)
        assertTrue(cards[0].isPrimary)
    }
}
