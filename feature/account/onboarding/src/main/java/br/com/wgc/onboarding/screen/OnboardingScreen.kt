package br.com.wgc.onboarding.screen

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.wgc.design_system.templates.factories.WgcOnboardingFactory
import br.com.wgc.design_system.templates.factories.WgcOnboardingType

/**
 * Tela de Onboarding e apresentação inicial de propostas de valor do aplicativo.
 *
 * Suporta o catálogo corporativo de templates através da [WgcOnboardingFactory] do `DesignSystemAndroid 0.24.1`,
 * permitindo alternar dinamicamente entre estilos visuais segmentados (Gadget Shop, Finanças Pessoais,
 * Compra Rápida, Moda ou Padrão Corporativo).
 *
 * ### 📌 Quando Usar:
 * Renderize esta tela durante o primeiro acesso do usuário (verificado via DataStore local)
 * antes de conduzi-lo para a tela de autenticação ou tela principal do aplicativo.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * // 1. Uso padrão corporativo:
 * @Composable
 * fun AppIntro(navController: NavController) {
 *     OnboardingScreen(
 *         onFinished = {
 *             navController.navigate("login") {
 *                 popUpTo("onboarding") { inclusive = true }
 *             }
 *         }
 *     )
 * }
 *
 * // 2. Uso com variante temática da fábrica oficial (ex: Personal Finance):
 * OnboardingScreen(
 *     onboardingType = WgcOnboardingType.PERSONAL_FINANCE,
 *     onFinished = { navegarParaHome() }
 * )
 * ```
 *
 * @param modifier Modificador de layout Compose aplicado à raiz da tela.
 * @param onboardingType Estilo visual e temático de onboarding gerenciado por [WgcOnboardingType]. Padrão é [WgcOnboardingType.STANDARD].
 * @param onFinished Callback invocado quando o usuário conclui o fluxo ou toca no botão "Pular".
 * @param viewModel ViewModel injetado responsável pelo controle de estado e índice do carrossel.
 */
@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onboardingType: WgcOnboardingType = WgcOnboardingType.STANDARD,
    onFinished: () -> Unit = {},
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isCompleted) {
        onFinished()
    }

    if (onboardingType != WgcOnboardingType.STANDARD) {
        val currentPage = uiState.pages.getOrNull(uiState.currentPageIndex)
        WgcOnboardingFactory(
            modifier = modifier,
            type = onboardingType,
            step = uiState.currentPageIndex + 1,
            totalSteps = uiState.pages.size,
            title = currentPage?.title ?: uiState.title,
            description = currentPage?.description ?: "",
            onNextClick = { viewModel.nextPage() },
            onSkipClick = { viewModel.completeOnboarding() },
            onGetStartedClick = { viewModel.completeOnboarding() }
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar (Skip action)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "WGC COMMONS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            TextButton(onClick = viewModel::completeOnboarding) {
                Text("Pular", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Center Content with Animation
        val currentPage = uiState.pages[uiState.currentPageIndex]
        AnimatedContent(targetState = currentPage, label = "OnboardingPage") { page ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(100.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        val icon = when (page.tag) {
                            "E-COMMERCE" -> Icons.Default.ShoppingCart
                            "TRACKING" -> Icons.Default.LocationOn
                            else -> Icons.Default.Lock
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = page.description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Bottom Navigation (Dots and Next Button)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page Indicator Dots
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.pages.forEachIndexed { index, _ ->
                    val isSelected = uiState.currentPageIndex == index
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outlineVariant
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (uiState.currentPageIndex > 0) {
                    TextButton(onClick = viewModel::previousPage) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.size(4.dp))
                        Text("Voltar")
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }

                Button(
                    onClick = viewModel::nextPage,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    val isLastPage = uiState.currentPageIndex == uiState.pages.size - 1
                    Text(if (isLastPage) "Começar Agora" else "Avançar")
                    Spacer(modifier = Modifier.size(6.dp))
                    Icon(
                        imageVector = if (isLastPage) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}