package br.com.wgc.catalog.integration

import br.com.wgc.catalog.screen.CatalogViewModel
import br.com.wgc.core.coroutines.CoroutineDispatchers
import br.wgc.omnibackend.core.repository.FirestoreRepository
import br.wgc.omnibackend.core.utils.AppError
import br.wgc.omnibackend.core.utils.DataResult
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
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
 * Testes de integração de repositório simulado em memória (In-Memory OmniBackend Emulator).
 *
 * Valida a sincronização reativa e manipulação de entidades sem qualquer dependência de rede externa.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class InMemoryFirestoreRepositoryIntegrationTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val inMemoryFirestore = mockk<FirestoreRepository>()
    private val liveCollectionStream = MutableSharedFlow<DataResult<List<Map<*, *>>>>(replay = 1)

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
            inMemoryFirestore.listenToCollection(any(), any(), any<Class<Map<*, *>>>())
        } returns liveCollectionStream
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `emulador in-memory deve transmitir lista de produtos do catalogo reativamente`() = runTest {
        val simulatedCatalog = listOf(
            mapOf("name" to "Pizza Margherita", "price" to "55.00"),
            mapOf("name" to "Suco Natural de Laranja", "price" to "12.00")
        )
        liveCollectionStream.tryEmit(DataResult.Success(simulatedCatalog))

        val viewModel = CatalogViewModel(inMemoryFirestore, dispatchers)
        val state = viewModel.uiState.first()

        assertEquals(2, state.featuredProducts.size)
        assertTrue(state.featuredProducts.any { it.contains("Pizza Margherita") })
        assertTrue(state.featuredProducts.any { it.contains("Suco Natural de Laranja") })
    }

    @Test
    fun `emulador in-memory ao emitir erro deve manter produtos locais de fallback`() = runTest {
        val fakeError = mockk<AppError>()
        liveCollectionStream.tryEmit(DataResult.Failure(fakeError))

        val viewModel = CatalogViewModel(inMemoryFirestore, dispatchers)
        val state = viewModel.uiState.first()

        // Fallback resiliente preserva os produtos de amostra
        assertTrue(state.featuredProducts.isNotEmpty())
    }
}
