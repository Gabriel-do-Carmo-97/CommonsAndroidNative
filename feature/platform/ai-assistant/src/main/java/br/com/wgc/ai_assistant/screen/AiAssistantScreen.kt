package br.com.wgc.ai_assistant.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Tela do Assistente de InteligÃªncia Artificial generativa e recomendaÃ§Ãµes contextuais.
 *
 * @param modifier Modificador de layout Compose.
 * @param title TÃ­tulo do cabeÃ§alho da tela do assistente.
 */
@Composable
fun AiAssistantScreen(
    modifier: Modifier = Modifier,
    title: String = "MÃ³dulo de Assistente de IA WGC"
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Chatbot inteligente e recomendaÃ§Ãµes via Vertex AI / Gemini WGC", style = MaterialTheme.typography.bodyMedium)
    }
}