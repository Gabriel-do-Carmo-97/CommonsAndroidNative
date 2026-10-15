package br.com.wgc.catalog.paging

import br.com.wgc.core.coroutines.CoroutineDispatchers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [CatalogPagingSource] garantindo paginação determinística e prefetch.
 */
class CatalogPagingSourceTest {

    private val testDispatchers = object : CoroutineDispatchers {
        override val main: kotlinx.coroutines.CoroutineDispatcher = Dispatchers.Unconfined
        override val io: kotlinx.coroutines.CoroutineDispatcher = Dispatchers.Unconfined
        override val default: kotlinx.coroutines.CoroutineDispatcher = Dispatchers.Unconfined
        override val unconfined: kotlinx.coroutines.CoroutineDispatcher = Dispatchers.Unconfined
    }

    private lateinit var pagingSource: CatalogPagingSource

    @Before
    fun setup() {
        pagingSource = CatalogPagingSource(testDispatchers)
    }

    @Test
    fun `loadPage page 0 returns correct page size and next key`() = runTest {
        val result = pagingSource.loadPage(pageKey = 0, pageSize = 5)

        assertEquals(5, result.items.size)
        assertEquals(1, result.nextPageKey)
        assertFalse(result.isEndOfPaginationReached)
    }

    @Test
    fun `loadPage until end reaches endOfPagination`() = runTest {
        val result = pagingSource.loadPage(pageKey = 3, pageSize = 5)
        assertEquals(5, result.items.size)
        assertTrue(result.isEndOfPaginationReached)
        assertEquals(null, result.nextPageKey)
    }

    @Test
    fun `loadPage beyond master catalog returns empty list`() = runTest {
        val result = pagingSource.loadPage(pageKey = 10, pageSize = 5)
        assertTrue(result.items.isEmpty())
        assertTrue(result.isEndOfPaginationReached)
    }
}
