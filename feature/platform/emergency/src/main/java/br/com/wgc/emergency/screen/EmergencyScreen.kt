package br.com.wgc.emergency.screen

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
 * Tela do BotÃ£o de PÃ¢nico e Alerta Silencioso de EmergÃªncia com geolocalizaÃ§Ã£o imediata.
 *
 * @param modifier Modificador de layout Compose.
 * @param title TÃ­tulo do mÃ³dulo de emergÃªncia.
 */
@Composable
fun EmergencyScreen(
    modifier: Modifier = Modifier,
    title: String = "MÃ³dulo de BotÃ£o de PÃ¢nico WGC"
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "BotÃ£o de pÃ¢nico e alerta silencioso de seguranÃ§a WGC", style = MaterialTheme.typography.bodyMedium)
    }
}