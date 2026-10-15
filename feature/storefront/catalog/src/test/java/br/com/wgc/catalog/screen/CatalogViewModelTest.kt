package br.com.wgc.catalog.screen

import app.cash.turbine.test
import br.com.wgc.core.coroutines.CoroutineDispatchers
import br.com.wgc.core.formatters.toCurrencyFormatted
import br.wgc.omnibackend.core.repository.FirestoreRepository
import br.wgc.omnibackend.core.utils.DataResult
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Suíte de testes unitários para validação de [CatalogViewModel].
 *
 * Valida estado inicial, sincronização em tempo real de produtos remotos do Firestore,
 * injeção de [CoroutineDispatchers] e formatação monetária corporativa.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val firestoreRepository = mockk<FirestoreRepository>(relaxed = true)

    private val dispatchers = object : CoroutineDispatchers {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
        override val unconfined: CoroutineDispatcher = testDispatcher
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every {
            firestoreRepository.listenToCollection(any(), any(), any<Class<Map<*, *>>>())
        } returns flowOf(
            DataResult.Success(
                listOf(
                    mapOf("name" to "Hamburguer Gourmet", "price" to "45.00")
                )
            )
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads remote products from Firestore collection stream`() = runTest {
        val viewModel = CatalogViewModel(firestoreRepository, dispatchers)
        val expectedPrice = 45.00.toCurrencyFormatted()
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Todos", state.selectedCategory)
            assertTrue(state.featuredProducts.contains("Hamburguer Gourmet - $expectedPrice"))
        }
    }

    @Test
    fun `selectCategory updates selected category in state`() = runTest {
        val viewModel = CatalogViewModel(firestoreRepository, dispatchers)
        viewModel.selectCategory("Bebidas")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Bebidas", state.selectedCategory)
        }
    }
}
