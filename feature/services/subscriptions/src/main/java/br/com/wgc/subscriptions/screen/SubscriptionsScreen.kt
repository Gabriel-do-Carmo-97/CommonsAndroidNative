package br.com.wgc.subscriptions.screen

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
 * Tela de Gerenciamento de Assinaturas e Planos Recorrentes.
 *
 * Exibe catÃ¡logo de planos contratados, periodicidades de renovaÃ§Ã£o
 * e benefÃ­cios vigentes para o assinante.
 *
 * @param modifier Modificador Jetpack Compose aplicado Ã  raiz do layout.
 * @param title TÃ­tulo descritivo exibido no painel de assinaturas.
 */
@Composable
fun SubscriptionsScreen(
    modifier: Modifier = Modifier,
    title: String = "MÃ³dulo de Assinaturas e RecorrÃªncia WGC"
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Clube de assinaturas, planos mensais e cobranÃ§as recorrentes WGC", style = MaterialTheme.typography.bodyMedium)
    }
}