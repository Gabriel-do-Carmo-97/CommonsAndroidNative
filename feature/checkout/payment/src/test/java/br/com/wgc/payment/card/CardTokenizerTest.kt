package br.com.wgc.payment.card

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para o tokenizador seguro de cartão [CardTokenizer].
 */
class CardTokenizerTest {

    private lateinit var tokenizer: CardTokenizer

    @Before
    fun setup() {
        tokenizer = CardTokenizer()
    }

    @Test
    fun `detectBrand identifies VISA and Mastercard correctly`() {
        assertEquals(CardBrand.VISA, tokenizer.detectBrand("4111111111111111"))
        assertEquals(CardBrand.MASTERCARD, tokenizer.detectBrand("5105105105105100"))
        assertEquals(CardBrand.AMEX, tokenizer.detectBrand("378282246310005"))
        assertEquals(CardBrand.HIPERCARD, tokenizer.detectBrand("6062821111111111"))
    }

    @Test
    fun `isValidLuhn returns true for valid test card and false for corrupt card`() {
        // Test Visa card with valid checksum
        assertTrue(tokenizer.isValidLuhn("4111111111111111"))
        // Corrupted digit
        assertFalse(tokenizer.isValidLuhn("4111111111111112"))
    }

    @Test
    fun `tokenize valid card returns Success with masked PAN and token`() {
        val result = tokenizer.tokenize(
            rawPan = "4111111111111111",
            holderName = "GABRIEL CARMO",
            expiryMonth = 12,
            expiryYear = 2032,
            cvv = "123"
        )

        assertTrue(result is CardTokenResult.Success)
        val success = result as CardTokenResult.Success
        assertEquals(CardBrand.VISA, success.brand)
        assertEquals("•••• •••• •••• 1111", success.maskedPan)
        assertTrue(success.token.startsWith("tok_wgc_"))
    }

    @Test
    fun `tokenize invalid CVV length returns Failure`() {
        val result = tokenizer.tokenize(
            rawPan = "4111111111111111",
            holderName = "GABRIEL CARMO",
            expiryMonth = 12,
            expiryYear = 2032,
            cvv = "1234" // Visa expects 3 digits
        )

        assertTrue(result is CardTokenResult.Failure)
        val failure = result as CardTokenResult.Failure
        assertTrue(failure.error.contains("CVV"))
    }
}
