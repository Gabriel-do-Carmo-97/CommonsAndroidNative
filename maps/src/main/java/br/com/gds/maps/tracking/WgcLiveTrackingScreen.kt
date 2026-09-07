package br.com.gds.maps.tracking

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.map.RealtimeLocationMapScreenTemplate

/**
 * Screen providing ready-to-use live tracking of vehicles/deliveries, integrating
 * Design System templates and Firebase Realtime Database Geolocation.
 */
@Composable
fun WgcLiveTrackingScreen(
    entityId: String,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    entityType: String = "drivers",
    driverName: String = "Motorista Parceiro",
    destinationAddress: String = "Destino Selecionado",
    onContactDriver: () -> Unit = {},
    viewModel: WgcLiveTrackingViewModel = hiltViewModel()
) {
    LaunchedEffect(entityId) {
        viewModel.initializeTracking(
            entityType = entityType,
            entityId = entityId,
            driverName = driverName,
            destinationAddress = destinationAddress,
            onContact = onContactDriver
        )
    }

    RealtimeLocationMapScreenTemplate(viewModel = viewModel)
}
