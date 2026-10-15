package br.com.wgc.commonsandroidnative.network

import android.content.Context
import br.com.wgc.core.network.NetworkMonitor
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.io.File

/**
 * Suíte de testes unitários para [HttpCacheConfig].
 *
 * Valida a criação do diretório de cache e dos interceptores de controle de cache.
 */
class HttpCacheConfigTest {

    private val context = mockk<Context>()
    private val networkMonitor = mockk<NetworkMonitor>()

    @Test
    fun `createCache initializes cache in context cache directory`() {
        val tempDir = File(System.getProperty("java.io.tmpdir"), "test_cache_${System.currentTimeMillis()}")
        tempDir.mkdirs()
        every { context.cacheDir } returns tempDir

        val config = HttpCacheConfig(context, networkMonitor)
        val cache = config.createCache(10L * 1024L * 1024L)

        assertNotNull(cache)
        assertEquals(10L * 1024L * 1024L, cache.maxSize())

        tempDir.deleteRecursively()
    }

    @Test
    fun `createOfflineCacheInterceptor creates non-null interceptor`() {
        val tempDir = File(System.getProperty("java.io.tmpdir"), "test_cache_${System.currentTimeMillis()}")
        every { context.cacheDir } returns tempDir

        val config = HttpCacheConfig(context, networkMonitor)
        val interceptor = config.createOfflineCacheInterceptor()

        assertNotNull(interceptor)
    }

    @Test
    fun `createOnlineCacheInterceptor creates non-null interceptor`() {
        val tempDir = File(System.getProperty("java.io.tmpdir"), "test_cache_${System.currentTimeMillis()}")
        every { context.cacheDir } returns tempDir

        val config = HttpCacheConfig(context, networkMonitor)
        val interceptor = config.createOnlineCacheInterceptor()

        assertNotNull(interceptor)
    }
}
