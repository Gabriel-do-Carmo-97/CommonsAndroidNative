package br.com.wgc.quotation.screen

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Tela de Simulação e Geração de Orçamentos Comerciais (Quotation).
 *
 * Permite inserção interativa de itens com quantitativos e valores monetários,
 * ajuste de percentual de desconto comercial em tempo real e compartilhamento
 * imediato do resumo formatado através do WhatsApp.
 *
 * @param modifier Modificador de layout Compose a ser aplicado no contêiner principal.
 * @param onSendWhatsApp Callback invocado com o texto formatado do orçamento pronto para despacho.
 * @param viewModel ViewModel injetado responsável pelos cálculos e estado da cotação.
 */
@Composable
fun QuotationScreen(
    modifier: Modifier = Modifier,
    onSendWhatsApp: (String) -> Unit = {},
    viewModel: QuotationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var newItemName by remember { mutableStateOf("") }
    var newItemPrice by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = uiState.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Adicione itens de serviços e peças, aplique desconto e gere o orçamento pronto para envio.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Client Name field
        OutlinedTextField(
            value = uiState.clientName,
            onValueChange = viewModel::updateClientName,
            label = { Text("Nome do Cliente") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Add Item Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "Adicionar Item ao Orçamento", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newItemName,
                        onValueChange = { newItemName = it },
                        placeholder = { Text("Item/Serviço") },
                        modifier = Modifier.weight(1.8f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = newItemPrice,
                        onValueChange = { newItemPrice = it },
                        placeholder = { Text("Valor R$") },
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val price = newItemPrice.toDoubleOrNull() ?: 0.0
                        if (newItemName.isNotBlank() && price > 0) {
                            viewModel.addItem(newItemName, price)
                            newItemName = ""
                            newItemPrice = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.End),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Adicionar")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Items List
        Text(text = "Itens do Orçamento (${uiState.items.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        uiState.items.forEach { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = item.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text(text = "${item.quantity}x R$ ${String.format("%.2f", item.unitPrice)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(text = "R$ ${String.format("%.2f", item.total)}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    IconButton(onClick = { viewModel.removeItem(item.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Remover", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Discount slider
        Text(text = "Desconto Comercial: ${uiState.discountPercent}%", style = MaterialTheme.typography.titleSmall)
        Slider(
            value = uiState.discountPercent.toFloat(),
            onValueChange = { viewModel.setDiscount(it.toInt()) },
            valueRange = 0f..50f,
            steps = 9
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Financial Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Subtotal:", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "R$ ${String.format("%.2f", uiState.subtotal)}", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Desconto (${uiState.discountPercent}%):", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "- R$ ${String.format("%.2f", uiState.discountAmount)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Valor Total:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = "R$ ${String.format("%.2f", uiState.total)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val text = viewModel.generateWhatsAppText()
                onSendWhatsApp(text)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Enviar Orçamento via WhatsApp")
        }
    }
}