package br.com.wgc.maps.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.wgc.maps.tracking.WgcLiveTrackingScreen

/**
 * High-level Maps Screen providing live vehicle and order tracking.
 * Delegates directly to [WgcLiveTrackingScreen] connected with OmniBackend and DS Templates.
 */
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    entityId: String = "driver_demo_01",
    entityType: String = "drivers",
    driverName: String = "Motorista Parceiro",
    destinationAddress: String = "Destino Selecionado",
    onContactDriver: () -> Unit = {}
) {
    WgcLiveTrackingScreen(
        entityId = entityId,
        modifier = modifier,
        entityType = entityType,
        driverName = driverName,
        destinationAddress = destinationAddress,
        onContactDriver = onContactDriver
    )
}
