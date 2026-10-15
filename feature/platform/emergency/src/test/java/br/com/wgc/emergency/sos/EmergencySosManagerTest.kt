package br.com.wgc.emergency.sos

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [EmergencySosManager] validando disparo de alerta SOS, payload telemétrico e chamada direta.
 */
class EmergencySosManagerTest {

    private lateinit var sosManager: EmergencySosManager

    @Before
    fun setup() {
        sosManager = EmergencySosManager()
    }

    @Test
    fun `triggerSosAlert creates complete telemetry payload and records active alert`() {
        val payload = sosManager.triggerSosAlert(
            driverId = "d-77",
            driverName = "Roberto Silva",
            type = EmergencyType.ROBBERY_RISK,
            latitude = -23.5505,
            longitude = -46.6333,
            batteryLevel = 38,
            networkType = "5G"
        )

        assertNotNull(payload.alertId)
        assertEquals("d-77", payload.driverId)
        assertEquals("Roberto Silva", payload.driverName)
        assertEquals(EmergencyType.ROBBERY_RISK, payload.emergencyType)
        assertEquals(38, payload.batteryPercentage)
        assertEquals("5G", payload.networkType)

        val active = sosManager.activeAlerts.value
        assertEquals(1, active.size)
        assertEquals(payload.alertId, active[0].alertId)
    }

    @Test
    fun `buildEmergencyCallSpec sanitizes phone number and creates tel intent URI`() {
        val spec = sosManager.buildEmergencyCallSpec("(11) 98765-4321")

        assertEquals("11987654321", spec.phoneNumber)
        assertEquals("tel:11987654321", spec.callIntentUri)

        val defaultSpec = sosManager.buildEmergencyCallSpec()
        assertEquals("190", defaultSpec.phoneNumber)
        assertEquals("tel:190", defaultSpec.callIntentUri)
    }

    @Test
    fun `resolveAlert removes payload from active alerts`() {
        val payload = sosManager.triggerSosAlert(
            driverId = "d-88",
            driverName = "Ana",
            type = EmergencyType.VEHICLE_BREAKDOWN,
            latitude = 0.0,
            longitude = 0.0
        )

        assertEquals(1, sosManager.activeAlerts.value.size)

        sosManager.resolveAlert(payload.alertId)
        assertTrue(sosManager.activeAlerts.value.isEmpty())
    }
}
