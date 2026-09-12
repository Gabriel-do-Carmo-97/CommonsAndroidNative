package br.com.wgc.force_update.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Tela de Bloqueio e Atualização Obrigatória (Force Update) ou Opcional do aplicativo.
 *
 * Intercepta o ciclo de vida caso o aplicativo cliente esteja operando em versão obsoleta
 * ou incompatível com as regras vigentes no Remote Config corporativo.
 *
 * @param modifier Modificador de layout Compose.
 * @param viewModel ViewModel de gerenciamento e verificação de versão remota.
 * @param currentVersionCode Código de versão atual do aplicativo instalado.
 */
@Composable
fun ForceUpdateScreen(
    modifier: Modifier = Modifier,
    viewModel: ForceUpdateViewModel = hiltViewModel(),
    currentVersionCode: Long = 1L
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.checkForUpdate(currentVersionCode)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Verificando versão no Remote Config...", style = MaterialTheme.typography.bodyMedium)
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.isForceUpdate) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (uiState.isForceUpdate) "Atualização Obrigatória" else if (uiState.isUpdateRequired) "Nova Versão Disponível" else "App Atualizado!",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (uiState.isForceUpdate) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sua versão: v${uiState.currentVersionCode} | Mínima exigida: v${uiState.minRequiredVersionCode}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (uiState.isUpdateRequired) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uiState.storeUrl))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Atualizar na Google Play")
                        }
                    } else {
                        OutlinedButton(
                            onClick = { viewModel.checkForUpdate(currentVersionCode) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Verificar Novamente")
                        }
                    }
                }
            }
        }
    }
}