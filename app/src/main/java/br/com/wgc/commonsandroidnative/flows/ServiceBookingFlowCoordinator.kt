package br.com.wgc.commonsandroidnative.flows

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
import br.com.wgc.quotation.screen.QuotationScreen
import br.com.wgc.scheduling.screen.SchedulingScreen
import br.com.wgc.stores.screen.StoresScreen
import br.com.wgc.whatsapp_direct.screen.WhatsappDirectScreen

enum class ServiceBookingStep(val title: String, val stepNumber: Int) {
    SELECT_STORE("1. Escolha de Filial", 1),
    SCHEDULE("2. Data & Horário", 2),
    QUOTATION("3. Orçamento de Peças/Serviço", 3),
    CONFIRMATION("4. Confirmação no WhatsApp", 4)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceBookingFlowCoordinator(
    modifier: Modifier = Modifier,
    onFinishFlow: () -> Unit = {}
) {
    var currentStep by rememberSaveable { mutableStateOf(ServiceBookingStep.SELECT_STORE) }

    BackHandler(enabled = currentStep != ServiceBookingStep.SELECT_STORE) {
        currentStep = when (currentStep) {
            ServiceBookingStep.SELECT_STORE -> ServiceBookingStep.SELECT_STORE
            ServiceBookingStep.SCHEDULE -> ServiceBookingStep.SELECT_STORE
            ServiceBookingStep.QUOTATION -> ServiceBookingStep.SCHEDULE
            ServiceBookingStep.CONFIRMATION -> ServiceBookingStep.QUOTATION
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Service Booking Flow", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${currentStep.title} (Passo ${currentStep.stepNumber} de 4)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep == ServiceBookingStep.SELECT_STORE) {
                            onFinishFlow()
                        } else {
                            currentStep = when (currentStep) {
                                ServiceBookingStep.SELECT_STORE -> ServiceBookingStep.SELECT_STORE
                                ServiceBookingStep.SCHEDULE -> ServiceBookingStep.SELECT_STORE
                                ServiceBookingStep.QUOTATION -> ServiceBookingStep.SCHEDULE
                                ServiceBookingStep.CONFIRMATION -> ServiceBookingStep.QUOTATION
                            }
                        }
                    }) {
                        Icon(
                            imageVector = if (currentStep == ServiceBookingStep.SELECT_STORE) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
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
                    if (currentStep != ServiceBookingStep.CONFIRMATION) {
                        Button(
                            onClick = {
                                currentStep = when (currentStep) {
                                    ServiceBookingStep.SELECT_STORE -> ServiceBookingStep.SCHEDULE
                                    ServiceBookingStep.SCHEDULE -> ServiceBookingStep.QUOTATION
                                    ServiceBookingStep.QUOTATION -> ServiceBookingStep.CONFIRMATION
                                    ServiceBookingStep.CONFIRMATION -> ServiceBookingStep.CONFIRMATION
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Avançar para: " + when (currentStep) {
                                ServiceBookingStep.SELECT_STORE -> "Agendamento"
                                ServiceBookingStep.SCHEDULE -> "Orçamento"
                                ServiceBookingStep.QUOTATION -> "WhatsApp"
                                ServiceBookingStep.CONFIRMATION -> "Concluir"
                            })
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                        }
                    } else {
                        Button(
                            onClick = onFinishFlow,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Concluir Fluxo de Atendimento")
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
                ServiceBookingStep.SELECT_STORE -> StoresScreen(modifier = Modifier.fillMaxSize())
                ServiceBookingStep.SCHEDULE -> SchedulingScreen(modifier = Modifier.fillMaxSize())
                ServiceBookingStep.QUOTATION -> QuotationScreen(modifier = Modifier.fillMaxSize())
                ServiceBookingStep.CONFIRMATION -> WhatsappDirectScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
