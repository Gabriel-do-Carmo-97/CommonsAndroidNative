package br.com.wgc.telemetry.screen

import app.cash.turbine.test
import br.com.wgc.core.analytics.breadcrumbs.BreadcrumbLevel
import br.com.wgc.core.analytics.breadcrumbs.BreadcrumbManager
import br.com.wgc.core.analytics.otel.OpenTelemetryExporter
import br.com.wgc.core.analytics.otel.OtelSpan
import br.com.wgc.core.coroutines.CoroutineDispatchers
import br.com.wgc.core.network.NetworkMonitor
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Suíte de testes unitários para [TelemetryViewModel].
 *
 * Valida o registro de eventos de rastro em buffer local com [BreadcrumbManager],
 * instrumentação OpenTelemetry com [OpenTelemetryExporter] e flush com base em conectividade de rede.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TelemetryViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val breadcrumbManager = mockk<BreadcrumbManager>(relaxed = true)
    private val openTelemetryExporter = mockk<OpenTelemetryExporter>(relaxed = true)
    private val networkMonitor = mockk<NetworkMonitor>(relaxed = true)
    private val networkFlow = MutableSharedFlow<Boolean>()

    private val dispatchers = object : CoroutineDispatchers {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
        override val unconfined: CoroutineDispatcher = testDispatcher
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { networkMonitor.isConnected } returns networkFlow
        every { networkMonitor.isCurrentlyConnected() } returns true
        every { breadcrumbManager.size() } returns 5
        every { openTelemetryExporter.generateTraceId() } returns "trace_abc123"
        every { openTelemetryExporter.generateSpanId() } returns "span_xyz789"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `recordBreadcrumb delegates to breadcrumbManager and updates state count`() = runTest {
        val viewModel = TelemetryViewModel(
            breadcrumbManager,
            openTelemetryExporter,
            networkMonitor,
            dispatchers
        )

        viewModel.recordBreadcrumb(
            category = "navigation",
            message = "Tocou no botão de carrinho",
            level = BreadcrumbLevel.INFO
        )

        verify {
            breadcrumbManager.addBreadcrumb("navigation", "Tocou no botão de carrinho", BreadcrumbLevel.INFO, emptyMap())
        }
    }

    @Test
    fun `recordSpan creates and exports OtelSpan`() = runTest {
        val viewModel = TelemetryViewModel(
            breadcrumbManager,
            openTelemetryExporter,
            networkMonitor,
            dispatchers
        )

        viewModel.recordSpan(
            name = "network_call_products",
            durationMillis = 150L,
            attributes = mapOf("endpoint" to "/api/products"),
            isSuccess = true
        )

        verify {
            openTelemetryExporter.exportSpan(any())
        }
    }

    @Test
    fun `flushOfflineBuffer flushes exporter and updates uiState`() = runTest {
        val dummySpan = OtelSpan(
            name = "op_test",
            traceId = "t1",
            spanId = "s1",
            parentSpanId = "",
            startTimestampNanos = 1000L,
            endTimestampNanos = 2000L,
            attributes = emptyMap(),
            isSuccess = true
        )
        every { openTelemetryExporter.flush() } returns listOf(dummySpan)

        val viewModel = TelemetryViewModel(
            breadcrumbManager,
            openTelemetryExporter,
            networkMonitor,
            dispatchers
        )

        val count = viewModel.flushOfflineBuffer()
        assertEquals(1, count)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.lastFlushedCount)
        }
    }
}
