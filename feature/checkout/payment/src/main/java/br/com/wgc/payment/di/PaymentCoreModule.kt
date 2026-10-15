package br.com.wgc.payment.di

import br.com.wgc.core.camera.QrCodeScannerAnalyzer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt de provisão de dependências para leitura e processamento de pagamentos óticos.
 *
 * Provê a instância de [QrCodeScannerAnalyzer] do CoreAndroidNative para o [br.com.wgc.payment.scanner.PaymentQrScannerViewModel].
 *
 * ### 📌 Quando Usar:
 * Configurado automaticamente pelo Dagger Hilt no [SingletonComponent].
 *
 * ### 🛠️ Modo de Usar:
 * Injetado automaticamente pelo Hilt em ViewModels de pagamento:
 * ```kotlin
 * @Inject
 * lateinit var analyzer: QrCodeScannerAnalyzer
 * ```
 */
@Module
@InstallIn(SingletonComponent::class)
object PaymentCoreModule {

    /**
     * Provê o analisador de QR Code e código de barras baseado no ML Kit do CoreAndroidNative.
     *
     * @return Instância configurada de [QrCodeScannerAnalyzer].
     */
    @Provides
    @Singleton
    fun provideQrCodeScannerAnalyzer(): QrCodeScannerAnalyzer {
        return QrCodeScannerAnalyzer()
    }
}
