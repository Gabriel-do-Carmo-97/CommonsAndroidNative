package br.com.wgc.payment.scanner

import app.cash.turbine.test
import br.com.wgc.core.camera.QrCodeScannerAnalyzer
import br.com.wgc.core.coroutines.CoroutineDispatchers
import io.mockk.every
import io.mockk.mockk
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Suíte de testes unitários para [PaymentQrScannerViewModel].
 *
 * Valida a categorização correta de códigos PIX BR Code e Boletos bancários
 * lidos pela câmera, além do reset e tratamento de formatos desconhecidos.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PaymentQrScannerViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val analyzer = mockk<QrCodeScannerAnalyzer>()
    private val scannedCodesFlow = MutableSharedFlow<String>()

    private val dispatchers = object : CoroutineDispatchers {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
        override val unconfined: CoroutineDispatcher = testDispatcher
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { analyzer.scannedCodes } returns scannedCodesFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `processScannedCode detects PIX BR Code format accurately`() = runTest {
        val viewModel = PaymentQrScannerViewModel(analyzer, dispatchers)
        val pixPayload = "00020126580014br.gov.bcb.pix0136123e4567-e89b-12d3-a456-4266141740005204000053039865802BR"

        viewModel.processScannedCode(pixPayload)

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isScanning)
            assertEquals(PaymentCodeType.PIX_BR_CODE, state.codeType)
            assertEquals(pixPayload, state.lastScannedCode)
            assertEquals(null, state.errorMessage)
        }
    }

    @Test
    fun `processScannedCode detects Boleto 44 to 48 digit barcode`() = runTest {
        val viewModel = PaymentQrScannerViewModel(analyzer, dispatchers)
        val boletoDigits = "34191790010104351004791020150008489720026000"

        viewModel.processScannedCode(boletoDigits)

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isScanning)
            assertEquals(PaymentCodeType.BOLETO_BARCODE, state.codeType)
            assertEquals(boletoDigits, state.lastScannedCode)
        }
    }

    @Test
    fun `processScannedCode flags error for unknown random text`() = runTest {
        val viewModel = PaymentQrScannerViewModel(analyzer, dispatchers)

        viewModel.processScannedCode("random_invalid_string")

        viewModel.uiState.test {
            val state = awaitItem()
            assertNotNull(state.errorMessage)
            assertTrue(state.isScanning)
        }
    }

    @Test
    fun `resetScanner restores scanner to initial scanning state`() = runTest {
        val viewModel = PaymentQrScannerViewModel(analyzer, dispatchers)
        viewModel.processScannedCode("000201-pix")

        viewModel.resetScanner()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isScanning)
            assertEquals(null, state.lastScannedCode)
            assertEquals(PaymentCodeType.UNKNOWN, state.codeType)
        }
    }
}
