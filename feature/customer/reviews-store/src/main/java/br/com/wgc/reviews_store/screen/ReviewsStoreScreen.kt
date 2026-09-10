package br.com.wgc.reviews_store.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Tela inteligente para fomento de avaliaÃ§Ãµes na Google Play Store.
 *
 * Implementa estratÃ©gia de triagem inteligente (smart review gating): avaliaÃ§Ãµes altas
 * (4 ou 5 estrelas) sÃ£o direcionadas Ã  Play Store oficial, enquanto avaliaÃ§Ãµes mais baixas
 * sÃ£o coletadas internamente como feedbacks de melhoria contÃ­nua.
 *
 * @param modifier Modificador de layout Compose a ser aplicado na raiz.
 * @param appPackageName Identificador do pacote do aplicativo na Google Play Store.
 * @param viewModel ViewModel injetado responsÃ¡vel pela triagem das notas e comentÃ¡rios.
 */
@Composable
fun ReviewsStoreScreen(
    modifier: Modifier = Modifier,
    appPackageName: String = "br.com.wgc.commonsandroidnative",
    viewModel: ReviewsStoreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = uiState.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "EstratÃ©gia inteligente de avaliaÃ§Ã£o: usuÃ¡rios satisfeitos avaliam na Google Play, crÃ­ticas viram melhorias internas.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.isCompleted) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Agradecemos sua contribuiÃ§Ã£o!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Sua opiniÃ£o nos ajuda a construir uma experiÃªncia cada vez melhor.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Quantas estrelas vocÃª daria para o aplicativo?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // 5 stars selector
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (1..5).forEach { star ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "$star estrelas",
                                tint = if (star <= uiState.starRating) Color(0xFFFFB300) else MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clickable { viewModel.selectRating(star) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (uiState.redirectTarget == ReviewRedirectTarget.PLAY_STORE) {
                        Text(
                            text = "Ficamos muito felizes que vocÃª estÃ¡ gostando! Poderia nos avaliar com 5 estrelas na Google Play Store?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                viewModel.completeReview()
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
                                try {
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Avaliar na Google Play Store")
                        }
                    } else if (uiState.redirectTarget == ReviewRedirectTarget.INTERNAL_FEEDBACK) {
                        Text(
                            text = "Queremos melhorar! O que podemos fazer para ganhar 5 estrelas na sua prÃ³xima avaliaÃ§Ã£o?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = uiState.feedbackComment,
                            onValueChange = viewModel::updateFeedbackComment,
                            placeholder = { Text("Conte-nos o que podemos aprimorar...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = viewModel::completeReview,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Enviar SugestÃ£o de Melhoria")
                        }
                    }
                }
            }
        }
    }
}