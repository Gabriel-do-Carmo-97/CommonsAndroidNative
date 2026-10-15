package br.com.wgc.payment.scanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Interface gráfica do leitor de QR Code PIX e Código de Barras de Pagamentos.
 *
 * Exibe o feedback visual em tempo real da leitura ótica pela câmera, alertando sobre
 * formatos reconhecidos (Pix Copia e Cola / Boleto bancário) e permitindo confirmação da operação.
 *
 * ### 📌 Quando Usar:
 * Renderizado quando o usuário clica em "Escanear Pix / Código de Barras" na carteira ou checkout.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun TelaDePagamento(viewModel: PaymentQrScannerViewModel = hiltViewModel()) {
 *     PaymentQrScannerScreen(
 *         viewModel = viewModel,
 *         onPaymentConfirmed = { code, type ->
 *             processarTransacao(code, type)
 *         }
 *     )
 * }
 * ```
 *
 * @param viewModel ViewModel de orquestração do leitor de câmera.
 * @param modifier Modificador visual Jetpack Compose.
 * @param onPaymentConfirmed Callback executado ao validar o código escaneado.
 */
@Composable
fun PaymentQrScannerScreen(
    viewModel: PaymentQrScannerViewModel,
    modifier: Modifier = Modifier,
    onPaymentConfirmed: (String, PaymentCodeType) -> Unit = { _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Leitor de Código de Barras & PIX",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Aponte a câmera para o QR Code PIX ou código de barras da fatura.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isScanning) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Buscando código na área da câmera...",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Código Identificado!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tipo: ${uiState.codeType.name}",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.lastScannedCode ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = uiState.errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (!uiState.isScanning && uiState.lastScannedCode != null) {
            Button(
                onClick = {
                    onPaymentConfirmed(uiState.lastScannedCode!!, uiState.codeType)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar Pagamento")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { viewModel.resetScanner() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Escanear Novamente")
            }
        }
    }
}
