package br.com.wgc.telemetry.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.wgc.core.analytics.breadcrumbs.BreadcrumbLevel
import br.com.wgc.core.analytics.breadcrumbs.BreadcrumbManager
import br.com.wgc.core.analytics.otel.OpenTelemetryExporter
import br.com.wgc.core.analytics.otel.OtelSpan
import br.com.wgc.core.coroutines.CoroutineDispatchers
import br.com.wgc.core.network.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado observável do painel e do pipeline de telemetria comportamental.
 *
 * @property title Título institucional do módulo de telemetria.
 * @property isLoading Indica processamento assíncrono de lote ou envio de métricas.
 * @property bufferedBreadcrumbsCount Quantidade de eventos e rastros retidos em buffer local.
 * @property isOfflineMode Indica se o envio imediato está represado devido a ausência de conectividade.
 * @property lastFlushedCount Quantidade de itens despachados no último flush com sucesso.
 */
data class TelemetryUiState(
    val title: String = "Módulo de Telemetria e Observabilidade WGC",
    val isLoading: Boolean = false,
    val bufferedBreadcrumbsCount: Int = 0,
    val isOfflineMode: Boolean = false,
    val lastFlushedCount: Int = 0
)

/**
 * ViewModel corporativo responsável pelo buffer e despacho resiliente de telemetria e rastreamento.
 *
 * Utiliza o [BreadcrumbManager] e o [OpenTelemetryExporter] do CoreAndroidNative 1.5.1 para reter
 * eventos comportamentais, falhas e métricas em fila local, garantindo zero perda de dados durante
 * períodos de instabilidade ou desconexão de rede (Modo Offline).
 *
 * ### 📌 Quando Usar:
 * Utilize para registrar rastros de navegação (breadcrumbs), spans de performance distribuídos
 * e despachar métricas em lote quando o dispositivo recuperar conectividade.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun TelaDeMonitoramento(viewModel: TelemetryViewModel = hiltViewModel()) {
 *     val state by viewModel.uiState.collectAsState()
 *
 *     Button(onClick = {
 *         viewModel.recordBreadcrumb("UI", "Usuário clicou no checkout", BreadcrumbLevel.INFO)
 *     }) {
 *         Text("Registrar Evento")
 *     }
 * }
 * ```
 *
 * @param breadcrumbManager Gerenciador de buffer circular de rastros e eventos.
 * @param openTelemetryExporter Exportador W3C / OpenTelemetry para APM e observabilidade distribuída.
 * @param networkMonitor Monitor de conectividade de rede para orquestração de flush offline.
 * @param dispatchers Provedor unificado de CoroutineDispatchers do CoreAndroidNative.
 */
@HiltViewModel
class TelemetryViewModel @Inject constructor(
    private val breadcrumbManager: BreadcrumbManager,
    private val openTelemetryExporter: OpenTelemetryExporter,
    private val networkMonitor: NetworkMonitor,
    private val dispatchers: CoroutineDispatchers
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        TelemetryUiState(
            bufferedBreadcrumbsCount = breadcrumbManager.size(),
            isOfflineMode = !networkMonitor.isCurrentlyConnected()
        )
    )

    /** Fluxo de estado contendo leituras ativas de telemetria e status do buffer. */
    val uiState: StateFlow<TelemetryUiState> = _uiState.asStateFlow()

    init {
        observeNetwork()
    }

    /**
     * Observa mudanças no estado da conexão de rede para disparar auto-flush quando online.
     */
    private fun observeNetwork() {
        viewModelScope.launch(dispatchers.main) {
            networkMonitor.isConnected.collect { connected ->
                _uiState.update { it.copy(isOfflineMode = !connected) }
                if (connected) {
                    flushOfflineBuffer()
                }
            }
        }
    }

    /**
     * Registra um rastro de navegação ou ação do usuário no buffer circular local.
     *
     * ### 📌 Quando Usar:
     * Disparado em cliques, transições de tela, ou eventos de negócio relevantes para auditoria e depuração.
     *
     * ### 🛠️ Modo de Usar:
     * ```kotlin
     * viewModel.recordBreadcrumb("checkout", "Item adicionado ao carrinho", BreadcrumbLevel.INFO)
     * ```
     *
     * @param category Categoria semântica do evento (ex: `navigation`, `checkout`, `auth`).
     * @param message Descrição concisa do ocorrido.
     * @param level Severidade do rastro ([BreadcrumbLevel.DEBUG], [BreadcrumbLevel.INFO], etc.).
     * @param metadata Atributos contextuais adicionais em formato de chave-valor.
     */
    fun recordBreadcrumb(
        category: String,
        message: String,
        level: BreadcrumbLevel = BreadcrumbLevel.INFO,
        metadata: Map<String, String> = emptyMap()
    ) {
        breadcrumbManager.addBreadcrumb(category, message, level, metadata)
        _uiState.update {
            it.copy(bufferedBreadcrumbsCount = breadcrumbManager.size())
        }
    }

    /**
     * Registra um span OpenTelemetry para medição de latência de operações.
     *
     * ### 📌 Quando Usar:
     * Invocado no início e fim de requisições de API, carregamentos de tela ou operações de IO.
     *
     * @param name Nome da operação medida (ex: `fetch_catalog`).
     * @param durationMillis Duração total da execução em milissegundos.
     * @param attributes Metadados de instrumentação do span.
     * @param isSuccess Indica se a operação foi completada com sucesso.
     */
    fun recordSpan(
        name: String,
        durationMillis: Long,
        attributes: Map<String, String> = emptyMap(),
        isSuccess: Boolean = true
    ) {
        val nowNanos = System.currentTimeMillis() * 1_000_000L
        val span = OtelSpan(
            name = name,
            traceId = openTelemetryExporter.generateTraceId(),
            spanId = openTelemetryExporter.generateSpanId(),
            parentSpanId = "",
            startTimestampNanos = nowNanos - (durationMillis * 1_000_000L),
            endTimestampNanos = nowNanos,
            attributes = attributes,
            isSuccess = isSuccess
        )
        openTelemetryExporter.exportSpan(span)
    }

    /**
     * Despacha o buffer de eventos e spans acumulados para os coletores remotos.
     *
     * ### 📌 Quando Usar:
     * Invocado automaticamente na reconexão de rede ou manualmente pelo usuário no painel de suporte.
     *
     * @return Quantidade de spans/itens despachados para a nuvem.
     */
    fun flushOfflineBuffer(): Int {
        val spans = openTelemetryExporter.flush()
        val dispatchedCount = spans.size
        _uiState.update {
            it.copy(
                bufferedBreadcrumbsCount = breadcrumbManager.size(),
                lastFlushedCount = dispatchedCount
            )
        }
        return dispatchedCount
    }
}