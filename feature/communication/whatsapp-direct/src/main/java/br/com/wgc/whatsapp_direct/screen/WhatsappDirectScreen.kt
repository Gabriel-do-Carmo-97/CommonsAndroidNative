package br.com.wgc.whatsapp_direct.screen

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.wgc.whatsapp_direct.helper.WhatsappDirectHelper

/**
 * Tela de Disparo Direto de Pedidos para o WhatsApp comercial do lojista.
 *
 * Permite customizaÃ§Ã£o do telefone de destino, nome do cliente, observaÃ§Ãµes do pedido
 * e prÃ©-visualizaÃ§Ã£o em tempo real do cartÃ£o de resumo formatado antes da transmissÃ£o.
 *
 * @param modifier Modificador Jetpack Compose aplicado na raiz do contÃªiner.
 * @param viewModel ViewModel injetado responsÃ¡vel pela formataÃ§Ã£o da mensagem e validaÃ§Ã£o do telefone.
 */
@Composable
fun WhatsappDirectScreen(
    modifier: Modifier = Modifier,
    viewModel: WhatsappDirectViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

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
            text = "Dispare pedidos e orÃ§amentos formatados direto para o WhatsApp do comÃ©rcio.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Recipient configuration
        OutlinedTextField(
            value = uiState.recipientPhone,
            onValueChange = viewModel::updateRecipientPhone,
            label = { Text("WhatsApp do Estabelecimento") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = uiState.customerName,
            onValueChange = viewModel::updateCustomerName,
            label = { Text("Nome do Cliente") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = uiState.notes,
            onValueChange = viewModel::updateNotes,
            label = { Text("ObservaÃ§Ãµes adicionais do pedido") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Formatted Preview Card (styled like a WhatsApp bubble)
        Text(text = "PrÃ©-visualizaÃ§Ã£o da Mensagem:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE7FFDB))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = uiState.formattedMessage,
                    color = Color(0xFF1B3B1B),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Send Button
        Button(
            onClick = {
                val uri = WhatsappDirectHelper.createWhatsAppUri(
                    phone = uiState.recipientPhone,
                    message = uiState.formattedMessage
                )
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    setPackage("com.whatsapp")
                }
                try {
                    context.startActivity(intent)
                } catch (_: Exception) {
                    // Fallback to browser or any app handling the uri
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text("Enviar Pedido no WhatsApp", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}