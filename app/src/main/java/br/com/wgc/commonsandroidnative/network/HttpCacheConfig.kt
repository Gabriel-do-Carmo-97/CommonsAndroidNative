package br.com.wgc.commonsandroidnative.network

import android.content.Context
import br.com.wgc.core.network.NetworkMonitor
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Configurador de Cache HTTP inteligente e resiliente para clientes de rede OkHttp.
 *
 * Implementa diretivas de `Cache-Control` agressivas para respostas HTTP bem-sucedidas (GET),
 * permitindo carregamento instantâneo em conexões instáveis e fallback automático offline
 * por até 7 dias quando o dispositivo não possuir conexão ativa à internet.
 *
 * ### 📌 Quando Usar:
 * Utilize na configuração do [okhttp3.OkHttpClient] corporativo para requisições de catálogo,
 * imagens estáticas, metadados de configuração e rotas de APIs REST.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Inject
 * lateinit var httpCacheConfig: HttpCacheConfig
 *
 * val okHttpClient = OkHttpClient.Builder()
 *     .cache(httpCacheConfig.createCache())
 *     .addInterceptor(httpCacheConfig.createOfflineCacheInterceptor())
 *     .addNetworkInterceptor(httpCacheConfig.createOnlineCacheInterceptor())
 *     .build()
 * ```
 *
 * @property context Contexto de aplicação para acesso ao diretório interno de cache.
 * @property networkMonitor Monitor de conectividade do dispositivo para detecção de status offline.
 */
@Singleton
class HttpCacheConfig @Inject constructor(
    private val context: Context,
    private val networkMonitor: NetworkMonitor
) {

    companion object {
        /** Tamanho padrão do cache em disco: 50 MegaBytes. */
        const val DEFAULT_CACHE_SIZE_BYTES: Long = 50L * 1024L * 1024L

        /** Nome da pasta dedicada ao cache HTTP. */
        const val CACHE_DIRECTORY_NAME: String = "http_response_cache"

        /** Tempo máximo de validade do cache online em segundos (60 segundos). */
        const val ONLINE_MAX_AGE_SECONDS: Int = 60

        /** Tempo máximo de permanência offline em dias (7 dias). */
        const val OFFLINE_MAX_STALE_DAYS: Int = 7
    }

    /**
     * Cria e retorna o diretório de cache [Cache] do OkHttp alocado no armazenamento privado.
     *
     * ### 📌 Quando Usar:
     * Invocado na construção do cliente HTTP OkHttp.
     *
     * @param maxSize Tamanho máximo permitido em bytes (padrão: 50MB).
     * @return Instância configurada de [Cache].
     */
    fun createCache(maxSize: Long = DEFAULT_CACHE_SIZE_BYTES): Cache {
        val cacheDir = File(context.cacheDir, CACHE_DIRECTORY_NAME)
        return Cache(cacheDir, maxSize)
    }

    /**
     * Cria o interceptor de nível de aplicação para forçar uso do cache quando o dispositivo estiver offline.
     *
     * ### 📌 Quando Usar:
     * Adicione via `addInterceptor(...)` no [okhttp3.OkHttpClient.Builder].
     *
     * @return [Interceptor] que aplica diretivas `only-if-cached` em modo offline.
     */
    fun createOfflineCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            if (!networkMonitor.isCurrentlyConnected()) {
                val cacheControl = CacheControl.Builder()
                    .onlyIfCached()
                    .maxStale(OFFLINE_MAX_STALE_DAYS, TimeUnit.DAYS)
                    .build()
                request = request.newBuilder()
                    .cacheControl(cacheControl)
                    .build()
            }
            chain.proceed(request)
        }
    }

    /**
     * Cria o interceptor de nível de rede para reescrever cabeçalhos de resposta HTTP com `max-age`.
     *
     * ### 📌 Quando Usar:
     * Adicione via `addNetworkInterceptor(...)` no [okhttp3.OkHttpClient.Builder].
     *
     * @return [Interceptor] que reescreve cabeçalhos para habilitar cache transparente.
     */
    fun createOnlineCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            val response: Response = chain.proceed(chain.request())
            val cacheControl = CacheControl.Builder()
                .maxAge(ONLINE_MAX_AGE_SECONDS, TimeUnit.SECONDS)
                .build()

            response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .header("Cache-Control", cacheControl.toString())
                .build()
        }
    }
}
