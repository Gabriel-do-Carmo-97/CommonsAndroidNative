package br.com.wgc.cart.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [ValidateCartCheckoutUseCase].
 */
class ValidateCartCheckoutUseCaseTest {

    private lateinit var useCase: ValidateCartCheckoutUseCase

    @Before
    fun setup() {
        useCase = ValidateCartCheckoutUseCase()
    }

    @Test
    fun `invoke com carrinho vazio deve retornar Invalid`() {
        val request = CheckoutValidationRequest(items = emptyList(), addressId = "addr_1")
        val result = useCase(request)

        assertTrue(result is CheckoutValidationResult.Invalid)
        assertEquals("O carrinho não pode estar vazio para fechar o pedido.", (result as CheckoutValidationResult.Invalid).errorMessage)
    }

    @Test
    fun `invoke com item com quantidade zero deve retornar Invalid`() {
        val request = CheckoutValidationRequest(
            items = listOf(CheckoutItem("p1", "Livro", 0, 50.0)),
            addressId = "addr_1"
        )
        val result = useCase(request)

        assertTrue(result is CheckoutValidationResult.Invalid)
    }

    @Test
    fun `invoke com item com preco negativo deve retornar Invalid`() {
        val request = CheckoutValidationRequest(
            items = listOf(CheckoutItem("p1", "Livro", 1, -10.0)),
            addressId = "addr_1"
        )
        val result = useCase(request)

        assertTrue(result is CheckoutValidationResult.Invalid)
    }

    @Test
    fun `invoke para delivery sem endereco deve retornar Invalid`() {
        val request = CheckoutValidationRequest(
            items = listOf(CheckoutItem("p1", "Livro", 1, 50.0)),
            addressId = null,
            isDelivery = true
        )
        val result = useCase(request)

        assertTrue(result is CheckoutValidationResult.Invalid)
    }

    @Test
    fun `invoke para retirada sem endereco deve retornar Valid`() {
        val request = CheckoutValidationRequest(
            items = listOf(CheckoutItem("p1", "Livro", 1, 50.0)),
            addressId = null,
            isDelivery = false
        )
        val result = useCase(request)

        assertTrue(result is CheckoutValidationResult.Valid)
    }

    @Test
    fun `invoke com dados validos deve retornar Valid`() {
        val request = CheckoutValidationRequest(
            items = listOf(CheckoutItem("p1", "Livro", 2, 50.0)),
            addressId = "addr_123",
            isDelivery = true
        )
        val result = useCase(request)

        assertTrue(result is CheckoutValidationResult.Valid)
    }
}
