package br.com.wgc.payment.success

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Dados fiscais e resumo operacional da compra finalizada com sucesso.
 *
 * @property orderId Identificador único do pedido para rastreamento.
 * @property protocolNumber Número de protocolo fiscal gerado.
 * @property totalAmount Valor total pago formatado.
 * @property paymentMethodModal Nome descritivo da forma de pagamento (ex: "Pix", "Cartão de Crédito").
 * @property estimatedDeliveryTime Previsão média para chegada dos itens.
 */
data class CompletedOrderSummary(
    val orderId: String,
    val protocolNumber: String,
    val totalAmount: String,
    val paymentMethodModal: String,
    val estimatedDeliveryTime: String = "35 a 50 minutos"
)

/**
 * Tela corporativa de Celebração e Confirmação de Pedido Concluído (Order Success & Fiscal Feedback).
 *
 * Apresenta o feedback visual imediato da transação autorizada, número de protocolo fiscal,
 * resumo de pagamento e atalhos rápidos para rastreamento de entrega em tempo real ou retorno à loja.
 *
 * ### 📌 Quando Usar:
 * Renderize no encerramento da jornada de checkout (:feature:checkout:payment) após confirmação
 * de pagamento por Pix, Cartão ou Google Pay.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val summary = CompletedOrderSummary(
 *     orderId = "order_12345",
 *     protocolNumber = "NF-WGC-2026-9871",
 *     totalAmount = "R$ 78,90",
 *     paymentMethodModal = "Pix Instantâneo"
 * )
 *
 * OrderSuccessScreen(
 *     summary = summary,
 *     onTrackOrderClick = { orderId -> navegarParaRastreamento(orderId) },
 *     onBackToHomeClick = { navegarParaHome() }
 * )
 * ```
 *
 * @param summary Resumo fiscal do pedido finalizado.
 * @param modifier Modificador de layout Compose.
 * @param onTrackOrderClick Callback acionado ao tocar em "Acompanhar Pedido".
 * @param onBackToHomeClick Callback acionado ao tocar em "Voltar para o Início".
 */
@Composable
fun OrderSuccessScreen(
    summary: CompletedOrderSummary,
    modifier: Modifier = Modifier,
    onTrackOrderClick: (String) -> Unit = {},
    onBackToHomeClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(96.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Sucesso",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Pedido Confirmado!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.semantics { heading() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Seu pagamento foi aprovado e o pedido já está sendo preparado pela equipe.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Receipt, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Protocolo:", style = MaterialTheme.typography.labelMedium)
                        }
                        Text(summary.protocolNumber, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Forma de Pagamento:", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(summary.paymentMethodModal, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Valor Total Pago:", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(summary.totalAmount, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Previsão: ${summary.estimatedDeliveryTime}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { onTrackOrderClick(summary.orderId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Acompanhar Pedido")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBackToHomeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Voltar para o Início")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
