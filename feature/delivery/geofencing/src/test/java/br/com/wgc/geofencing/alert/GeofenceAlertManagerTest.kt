package br.com.wgc.geofencing.alert

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [GeofenceAlertManager] validando detecção de entrada no raio de 500m,
 * saída e prevenção de alertas repetidos.
 */
class GeofenceAlertManagerTest {

    private lateinit var alertManager: GeofenceAlertManager

    @Before
    fun setup() {
        alertManager = GeofenceAlertManager()
    }

    @Test
    fun `entering 500m geofence triggers ENTERED event once`() {
        val destination = GeofenceTarget(
            id = "order-55",
            latitude = -23.5505,
            longitude = -46.6333,
            radiusMeters = 500.0,
            destinationLabel = "Apartamento 42"
        )
        alertManager.registerTarget(destination)

        // Point 1: 1.5 km away (outside)
        val outsideEvents = alertManager.evaluateCurrentLocation(-23.5650, -46.6333)
        assertTrue(outsideEvents.isEmpty())

        // Point 2: ~300 meters away (inside 500m)
        val insideEvents = alertManager.evaluateCurrentLocation(-23.5525, -46.6333)
        assertEquals(1, insideEvents.size)
        assertEquals(GeofenceTransitionType.ENTERED, insideEvents[0].transitionType)
        assertTrue(insideEvents[0].alertMessage.contains("Apartamento 42"))

        // Point 3: ~200 meters away (still inside, should not trigger duplicate ENTERED event)
        val stillInsideEvents = alertManager.evaluateCurrentLocation(-23.5515, -46.6333)
        assertTrue(stillInsideEvents.isEmpty())
    }

    @Test
    fun `moving out of geofence triggers EXITED event`() {
        val destination = GeofenceTarget(
            id = "order-60",
            latitude = -23.5505,
            longitude = -46.6333,
            radiusMeters = 500.0,
            destinationLabel = "Loja"
        )
        alertManager.registerTarget(destination)

        // Enter geofence
        alertManager.evaluateCurrentLocation(-23.5510, -46.6333)

        // Move away (> 1km)
        val exitEvents = alertManager.evaluateCurrentLocation(-23.5650, -46.6333)
        assertEquals(1, exitEvents.size)
        assertEquals(GeofenceTransitionType.EXITED, exitEvents[0].transitionType)
    }

    @Test
    fun `removeTarget cleans up monitoring state`() {
        val destination = GeofenceTarget(id = "o-remove", latitude = 0.0, longitude = 0.0)
        alertManager.registerTarget(destination)
        assertEquals(1, alertManager.getRegisteredTargets().size)

        alertManager.removeTarget("o-remove")
        assertTrue(alertManager.getRegisteredTargets().isEmpty())
    }
}
