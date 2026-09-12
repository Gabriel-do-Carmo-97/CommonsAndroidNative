package br.com.wgc.driver_app.screen

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
 * Tela inicial do aplicativo operacional do Entregador (Driver App).
 *
 * Provê interface para aceitação de entregas, visualização de rotas otimizadas
 * e confirmação de chegada ao ponto de coleta/entrega.
 *
 * @param modifier Modificador de layout Jetpack Compose aplicado ao contêiner raiz.
 * @param title Título descritivo exibido na interface operacional.
 */
@Composable
fun DriverAppScreen(
    modifier: Modifier = Modifier,
    title: String = "Módulo do Entregador e Logística WGC"
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "App operacional do motoboy, aceitação de entregas e rotas otimizadas WGC", style = MaterialTheme.typography.bodyMedium)
    }
}