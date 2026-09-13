package br.com.wgc.testing

import br.com.wgc.testing.extensions.testSingle
import br.com.wgc.testing.fakes.FakeCartRepository
import br.com.wgc.testing.fakes.FakeCatalogRepository
import br.com.wgc.testing.fakes.FakePaymentMethod
import br.com.wgc.testing.fakes.FakePaymentRepository
import br.com.wgc.testing.fakes.FakeProduct
import br.com.wgc.testing.rules.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Testes unitários para validar a integridade e precisão dos utilitários e fixtures de [br.com.wgc.testing].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CoreTestingTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun fakeCatalogRepository_providesDefaultProductsAndFiltering() = runTest {
        val repo = FakeCatalogRepository()

        repo.products.testSingle { list ->
            assertEquals(4, list.size)
        }

        val item = repo.getProductById("p1")
        assertNotNull(item)
        assertEquals("Smartphone WGC Pro 5G", item?.title)

        repo.getProductsByCategory("Eletrônicos").testSingle { electronics ->
            assertEquals(1, electronics.size)
            assertEquals("p1", electronics.first().id)
        }
    }

    @Test
    fun fakeCartRepository_computesSubtotalAndItemsCorrectly() = runTest {
        val cart = FakeCartRepository()
        val product = FakeProduct("test_1", "Produto Teste", 100.0, "Geral")

        cart.addItem(product, 2)

        cart.items.testSingle { items ->
            assertEquals(1, items.size)
            assertEquals(2, items.first().quantity)
        }

        cart.subtotal.testSingle { subtotal ->
            assertEquals(200.0, subtotal, 0.01)
        }

        cart.clear()
        cart.items.testSingle { items ->
            assertTrue(items.isEmpty())
        }
    }

    @Test
    fun fakePaymentRepository_handlesSuccessAndInducedFailure() = runTest {
        val payment = FakePaymentRepository()

        val success = payment.processPayment(150.0, FakePaymentMethod.PIX)
        assertTrue(success.isSuccess)
        assertNotNull(success.transactionId)

        payment.induceFailure("Saldo insuficiente")
        val failure = payment.processPayment(150.0, FakePaymentMethod.PIX)
        assertFalse(failure.isSuccess)
        assertEquals("Saldo insuficiente", failure.errorMessage)

        val nextSuccess = payment.processPayment(50.0, FakePaymentMethod.PIX)
        assertTrue(nextSuccess.isSuccess)
    }
}
