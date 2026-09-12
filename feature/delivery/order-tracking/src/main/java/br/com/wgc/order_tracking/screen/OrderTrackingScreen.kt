package br.com.wgc.order_tracking.screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Tela de Acompanhamento e Linha do Tempo de Pedidos (Order Tracking).
 *
 * Apresenta o status atual do pedido através de um stepper vertical progressivo,
 * estimativa de tempo restante (ETA), dados do entregador parceiro e botão para abrir o rastreamento em mapa.
 *
 * @param modifier Modificador de layout Jetpack Compose aplicado à tela.
 * @param onOpenMapTracking Callback invocado para transição à tela com o mapa ao vivo.
 * @param viewModel ViewModel injetado gerenciador do ciclo de vida e estado do pedido.
 */
@Composable
fun OrderTrackingScreen(
    modifier: Modifier = Modifier,
    onOpenMapTracking: () -> Unit = {},
    viewModel: OrderTrackingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = uiState.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Pedido ${uiState.orderId}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = if (uiState.estimatedMinutesRemaining > 0) "~${uiState.estimatedMinutesRemaining} min" else "Chegou!",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stepper Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OrderStatus.entries.forEachIndexed { index, step ->
                    val isDone = uiState.status.stepIndex >= step.stepIndex
                    val isCurrent = uiState.status == step

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(32.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                             isCurrent -> MaterialTheme.colorScheme.primary
                                             isDone -> Color(0xFF2E7D32)
                                             else -> MaterialTheme.colorScheme.outlineVariant
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            if (index < OrderStatus.entries.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(28.dp)
                                        .background(
                                            if (uiState.status.stepIndex > step.stepIndex) Color(0xFF2E7D32)
                                            else MaterialTheme.colorScheme.outlineVariant
                                        )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = step.label,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = step.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Delivery & Driver Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = uiState.driverName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = uiState.deliveryAddress, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Total:", style = MaterialTheme.typography.bodyMedium)
                    Text(text = uiState.totalAmount, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Simulation CTA button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onOpenMapTracking,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ver no Mapa")
            }

            Button(
                onClick = viewModel::advanceStatus,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Avançar Status")
            }
        }
    }
}