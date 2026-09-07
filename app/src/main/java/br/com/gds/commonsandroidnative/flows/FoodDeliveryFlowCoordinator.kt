package br.com.gds.commonsandroidnative.flows

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.gds.cart.screen.CartScreen
import br.com.gds.catalog.screen.CatalogScreen
import br.com.gds.catalog.screen.CommercialTemplateMode
import br.com.gds.feedback.screen.FeedbackScreen
import br.com.gds.maps.tracking.WgcLiveTrackingScreen
import br.com.gds.order_tracking.screen.OrderTrackingScreen
import br.com.gds.payment.screen.PaymentScreen

enum class FoodDeliveryStep(val title: String, val stepNumber: Int) {
    CATALOG("1. Cardápio iFood", 1),
    CART("2. Carrinho & Frete", 2),
    CHECKOUT("3. Pagamento Pix", 3),
    TRACKING("4. Rastreio & Mapa", 4),
    FEEDBACK("5. Avaliação NPS", 5)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDeliveryFlowCoordinator(
    modifier: Modifier = Modifier,
    onFinishFlow: () -> Unit = {}
) {
    var currentStep by rememberSaveable { mutableStateOf(FoodDeliveryStep.CATALOG) }

    BackHandler(enabled = currentStep != FoodDeliveryStep.CATALOG) {
        currentStep = when (currentStep) {
            FoodDeliveryStep.CATALOG -> FoodDeliveryStep.CATALOG
            FoodDeliveryStep.CART -> FoodDeliveryStep.CATALOG
            FoodDeliveryStep.CHECKOUT -> FoodDeliveryStep.CART
            FoodDeliveryStep.TRACKING -> FoodDeliveryStep.CHECKOUT
            FoodDeliveryStep.FEEDBACK -> FoodDeliveryStep.TRACKING
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Food Delivery Flow", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${currentStep.title} (Passo ${currentStep.stepNumber} de 5)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep == FoodDeliveryStep.CATALOG) {
                            onFinishFlow()
                        } else {
                            currentStep = when (currentStep) {
                                FoodDeliveryStep.CATALOG -> FoodDeliveryStep.CATALOG
                                FoodDeliveryStep.CART -> FoodDeliveryStep.CATALOG
                                FoodDeliveryStep.CHECKOUT -> FoodDeliveryStep.CART
                                FoodDeliveryStep.TRACKING -> FoodDeliveryStep.CHECKOUT
                                FoodDeliveryStep.FEEDBACK -> FoodDeliveryStep.TRACKING
                            }
                        }
                    }) {
                        Icon(
                            imageVector = if (currentStep == FoodDeliveryStep.CATALOG) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep != FoodDeliveryStep.FEEDBACK) {
                        Button(
                            onClick = {
                                currentStep = when (currentStep) {
                                    FoodDeliveryStep.CATALOG -> FoodDeliveryStep.CART
                                    FoodDeliveryStep.CART -> FoodDeliveryStep.CHECKOUT
                                    FoodDeliveryStep.CHECKOUT -> FoodDeliveryStep.TRACKING
                                    FoodDeliveryStep.TRACKING -> FoodDeliveryStep.FEEDBACK
                                    FoodDeliveryStep.FEEDBACK -> FoodDeliveryStep.FEEDBACK
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Avançar para: " + when (currentStep) {
                                FoodDeliveryStep.CATALOG -> "Carrinho"
                                FoodDeliveryStep.CART -> "Pagamento"
                                FoodDeliveryStep.CHECKOUT -> "Rastreamento"
                                FoodDeliveryStep.TRACKING -> "Avaliação"
                                FoodDeliveryStep.FEEDBACK -> "Concluir"
                            })
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                        }
                    } else {
                        Button(
                            onClick = onFinishFlow,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Concluir Fluxo de Delivery")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentStep) {
                FoodDeliveryStep.CATALOG -> CatalogScreen(
                    initialMode = CommercialTemplateMode.IFOOD_DELIVERY,
                    modifier = Modifier.fillMaxSize()
                )
                FoodDeliveryStep.CART -> CartScreen(modifier = Modifier.fillMaxSize())
                FoodDeliveryStep.CHECKOUT -> PaymentScreen(modifier = Modifier.fillMaxSize())
                FoodDeliveryStep.TRACKING -> OrderTrackingScreen(
                    onOpenMapTracking = { /* map tracking inline */ },
                    modifier = Modifier.fillMaxSize()
                )
                FoodDeliveryStep.FEEDBACK -> FeedbackScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
