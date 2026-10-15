package br.com.wgc.commonsandroidnative.navigation

import android.net.Uri
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para o roteador [UniversalDeepLinkRouter].
 */
class UniversalDeepLinkRouterTest {

    private lateinit var router: UniversalDeepLinkRouter

    @Before
    fun setup() {
        router = UniversalDeepLinkRouter()
    }

    private fun createMockUri(
        scheme: String?,
        host: String?,
        pathSegments: List<String> = emptyList(),
        queryParams: Map<String, String> = emptyMap()
    ): Uri {
        val uri = mockk<Uri>()
        every { uri.scheme } returns scheme
        every { uri.host } returns host
        every { uri.pathSegments } returns pathSegments
        every { uri.queryParameterNames } returns queryParams.keys
        queryParams.forEach { (key, value) ->
            every { uri.getQueryParameter(key) } returns value
        }
        return uri
    }

    @Test
    fun `resolve com uri nula deve retornar null`() {
        assertNull(router.resolve(null))
    }

    @Test
    fun `resolve com host nao autorizado deve retornar null`() {
        val uri = createMockUri("https", "phishing-site.com", listOf("catalog"))
        assertNull(router.resolve(uri))
    }

    @Test
    fun `resolve com esquema nao autorizado deve retornar null`() {
        val uri = createMockUri("ftp", "app.wgc.com.br", listOf("catalog"))
        assertNull(router.resolve(uri))
    }

    @Test
    fun `resolve para catalogo deve retornar rota catalog`() {
        val uri = createMockUri("https", "app.wgc.com.br", listOf("catalog"))
        val result = router.resolve(uri)

        assertNotNull(result)
        assertEquals("catalog", result?.route)
    }

    @Test
    fun `resolve para produto com id deve retornar rota com parametro`() {
        val uri = createMockUri(
            scheme = "https",
            host = "app.wgc.com.br",
            pathSegments = listOf("product", "prod_abc"),
            queryParams = mapOf("coupon" to "WGC10")
        )
        val result = router.resolve(uri)

        assertNotNull(result)
        assertEquals("product_details/prod_abc", result?.route)
        assertEquals("prod_abc", result?.parameters?.get("id"))
        assertEquals("WGC10", result?.parameters?.get("coupon"))
    }

    @Test
    fun `resolve para tracking com orderId deve retornar rota order_tracking`() {
        val uri = createMockUri("https", "app.wgc.com.br", listOf("tracking", "ord_789"))
        val result = router.resolve(uri)

        assertNotNull(result)
        assertEquals("order_tracking/ord_789", result?.route)
        assertEquals("ord_789", result?.parameters?.get("orderId"))
    }

    @Test
    fun `resolve para custom scheme wgc app chat deve retornar rota chat`() {
        val uri = createMockUri("wgc", "app", listOf("chat", "support_1"))
        val result = router.resolve(uri)

        assertNotNull(result)
        assertEquals("chat/support_1", result?.route)
    }

    @Test
    fun `resolve com raiz sem segmentos deve retornar rota home`() {
        val uri = createMockUri("https", "app.wgc.com.br", emptyList())
        val result = router.resolve(uri)

        assertNotNull(result)
        assertEquals("home", result?.route)
    }
}
