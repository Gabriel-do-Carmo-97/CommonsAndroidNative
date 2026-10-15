package br.com.wgc.telemetry.di

import br.com.wgc.core.analytics.otel.OpenTelemetryExporter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt de provisão de dependências para o subsistema de telemetria e observabilidade distribuída.
 *
 * Provê a instância de [OpenTelemetryExporter] do CoreAndroidNative para o [br.com.wgc.telemetry.screen.TelemetryViewModel].
 *
 * ### 📌 Quando Usar:
 * Configurado automaticamente pelo Dagger Hilt no [SingletonComponent].
 *
 * ### 🛠️ Modo de Usar:
 * Injetado automaticamente pelo Hilt onde for solicitado [OpenTelemetryExporter]:
 * ```kotlin
 * @Inject
 * lateinit var exporter: OpenTelemetryExporter
 * ```
 */
@Module
@InstallIn(SingletonComponent::class)
object TelemetryCoreModule {

    /**
     * Provê o exportador OpenTelemetry / W3C para instrumentação de rastreamento distribuído.
     *
     * @return Instância configurada de [OpenTelemetryExporter].
     */
    @Provides
    @Singleton
    fun provideOpenTelemetryExporter(): OpenTelemetryExporter {
        return OpenTelemetryExporter()
    }
}
