package br.com.wgc.payment.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.wgc.core.camera.QrCodeScannerAnalyzer
import br.com.wgc.core.coroutines.CoroutineDispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Tipo de código financeiro identificado no escaneamento com a câmera.
 */
enum class PaymentCodeType {
    /** Código PIX dinâmico ou estático no padrão BR Code (EMVCo). */
    PIX_BR_CODE,

    /** Linha digitável ou código de barras de Boleto bancário ou fatura de consumo. */
    BOLETO_BARCODE,

    /** Formato de código desconhecido ou não suportado para transação. */
    UNKNOWN
}

/**
 * Estado da interface do leitor de QR Code e código de barras para pagamento.
 *
 * @property isScanning Indica se o analisador de câmera está ativo buscando códigos.
 * @property lastScannedCode Último código capturado bruto pela câmera.
 * @property codeType Tipo de código financeiro classificado.
 * @property errorMessage Mensagem amigável de erro se a leitura falhar ou for inválida.
 */
data class PaymentScannerUiState(
    val isScanning: Boolean = true,
    val lastScannedCode: String? = null,
    val codeType: PaymentCodeType = PaymentCodeType.UNKNOWN,
    val errorMessage: String? = null
)

/**
 * ViewModel corporativo responsável pelo processamento de pagamentos via leitura de câmera.
 *
 * Consome o analisador de visão computacional [QrCodeScannerAnalyzer] provido pelo `core-camera`,
 * interpretando payloads de PIX (BR Code) e boletos bancários para prosseguir com a liquidação da fatura.
 *
 * ### 📌 Quando Usar:
 * Utilize na tela de scanner da câmera (`PaymentQrScannerScreen`) ou em Bottom Sheets de checkout
 * onde o cliente opta por "Pagar com Leitor de QR / Código de Barras".
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun TelaDoScanner(viewModel: PaymentQrScannerViewModel = hiltViewModel()) {
 *     val state by viewModel.uiState.collectAsState()
 *     // Configurar CameraPreview com o analyzer...
 * }
 * ```
 *
 * @param analyzer Analisador de imagens ML Kit do CoreAndroidNative.
 * @param dispatchers Provedor centralizado de dispatchers de Coroutines.
 */
@HiltViewModel
class PaymentQrScannerViewModel @Inject constructor(
    private val analyzer: QrCodeScannerAnalyzer,
    private val dispatchers: CoroutineDispatchers
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentScannerUiState())

    /**
     * Fluxo reativo com o estado do scanner de pagamentos.
     */
    val uiState: StateFlow<PaymentScannerUiState> = _uiState.asStateFlow()

    init {
        startListening()
    }

    /**
     * Inicia a escuta reativa aos códigos detectados pelo analisador de câmera.
     */
    private fun startListening() {
        viewModelScope.launch(dispatchers.main) {
            analyzer.scannedCodes.collect { rawCode ->
                processScannedCode(rawCode)
            }
        }
    }

    /**
     * Processa e categoriza o código lido via câmera ou digitado manualmente.
     *
     * ### 📌 Quando Usar:
     * Chamado automaticamente a cada detecção bem-sucedida de código pelo scanner.
     *
     * @param rawCode Texto do código lido pela câmera.
     */
    fun processScannedCode(rawCode: String) {
        val trimmed = rawCode.trim()
        val type = when {
            trimmed.startsWith("000201") || trimmed.contains("br.gov.bcb.pix", ignoreCase = true) -> {
                PaymentCodeType.PIX_BR_CODE
            }
            trimmed.length in 44..48 && trimmed.all { it.isDigit() || it == '.' || it == ' ' } -> {
                PaymentCodeType.BOLETO_BARCODE
            }
            else -> PaymentCodeType.UNKNOWN
        }

        if (type != PaymentCodeType.UNKNOWN) {
            _uiState.update {
                it.copy(
                    isScanning = false,
                    lastScannedCode = trimmed,
                    codeType = type,
                    errorMessage = null
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    errorMessage = "Código não reconhecido como PIX ou Boleto válido."
                )
            }
        }
    }

    /**
     * Reinicia o analisador de câmera para realizar nova leitura.
     *
     * ### 📌 Quando Usar:
     * Invocado quando o usuário clica em "Escanear novamente".
     */
    fun resetScanner() {
        _uiState.update {
            it.copy(
                isScanning = true,
                lastScannedCode = null,
                codeType = PaymentCodeType.UNKNOWN,
                errorMessage = null
            )
        }
    }
}
