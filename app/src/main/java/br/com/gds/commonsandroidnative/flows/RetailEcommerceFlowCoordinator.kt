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
import br.com.gds.order_tracking.screen.OrderTrackingScreen
import br.com.gds.payment.screen.PaymentScreen
import br.com.gds.search.screen.SearchScreen

enum class RetailEcommerceStep(val title: String, val stepNumber: Int) {
    SEARCH("1. Busca & Filtros", 1),
    CATALOG("2. Grade Mercado Livre", 2),
    CART("3. Carrinho de Compras", 3),
    PAYMENT("4. Pagamento & Checkout", 4),
    TRACKING("5. Acompanhamento", 5)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RetailEcommerceFlowCoordinator(
    modifier: Modifier = Modifier,
    onFinishFlow: () -> Unit = {}
) {
    var currentStep by rememberSaveable { mutableStateOf(RetailEcommerceStep.SEARCH) }

    BackHandler(enabled = currentStep != RetailEcommerceStep.SEARCH) {
        currentStep = when (currentStep) {
            RetailEcommerceStep.SEARCH -> RetailEcommerceStep.SEARCH
            RetailEcommerceStep.CATALOG -> RetailEcommerceStep.SEARCH
            RetailEcommerceStep.CART -> RetailEcommerceStep.CATALOG
            RetailEcommerceStep.PAYMENT -> RetailEcommerceStep.CART
            RetailEcommerceStep.TRACKING -> RetailEcommerceStep.PAYMENT
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Retail E-Commerce Flow", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${currentStep.title} (Passo ${currentStep.stepNumber} de 5)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep == RetailEcommerceStep.SEARCH) {
                            onFinishFlow()
                        } else {
                            currentStep = when (currentStep) {
                                RetailEcommerceStep.SEARCH -> RetailEcommerceStep.SEARCH
                                RetailEcommerceStep.CATALOG -> RetailEcommerceStep.SEARCH
                                RetailEcommerceStep.CART -> RetailEcommerceStep.CATALOG
                                RetailEcommerceStep.PAYMENT -> RetailEcommerceStep.CART
                                RetailEcommerceStep.TRACKING -> RetailEcommerceStep.PAYMENT
                            }
                        }
                    }) {
                        Icon(
                            imageVector = if (currentStep == RetailEcommerceStep.SEARCH) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
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
                    if (currentStep != RetailEcommerceStep.TRACKING) {
                        Button(
                            onClick = {
                                currentStep = when (currentStep) {
                                    RetailEcommerceStep.SEARCH -> RetailEcommerceStep.CATALOG
                                    RetailEcommerceStep.CATALOG -> RetailEcommerceStep.CART
                                    RetailEcommerceStep.CART -> RetailEcommerceStep.PAYMENT
                                    RetailEcommerceStep.PAYMENT -> RetailEcommerceStep.TRACKING
                                    RetailEcommerceStep.TRACKING -> RetailEcommerceStep.TRACKING
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Avançar para: " + when (currentStep) {
                                RetailEcommerceStep.SEARCH -> "Catálogo"
                                RetailEcommerceStep.CATALOG -> "Carrinho"
                                RetailEcommerceStep.CART -> "Pagamento"
                                RetailEcommerceStep.PAYMENT -> "Rastreio"
                                RetailEcommerceStep.TRACKING -> "Concluir"
                            })
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                        }
                    } else {
                        Button(
                            onClick = onFinishFlow,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Concluir Fluxo E-Commerce")
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
                RetailEcommerceStep.SEARCH -> SearchScreen(modifier = Modifier.fillMaxSize())
                RetailEcommerceStep.CATALOG -> CatalogScreen(
                    initialMode = CommercialTemplateMode.MERCADO_LIVRE,
                    modifier = Modifier.fillMaxSize()
                )
                RetailEcommerceStep.CART -> CartScreen(modifier = Modifier.fillMaxSize())
                RetailEcommerceStep.PAYMENT -> PaymentScreen(modifier = Modifier.fillMaxSize())
                RetailEcommerceStep.TRACKING -> OrderTrackingScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
