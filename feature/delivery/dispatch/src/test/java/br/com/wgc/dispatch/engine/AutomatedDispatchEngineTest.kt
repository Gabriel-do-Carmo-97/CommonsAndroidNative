package br.com.wgc.dispatch.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [AutomatedDispatchEngine] validando filtros de capacidade, proximidade e ranqueamento.
 */
class AutomatedDispatchEngineTest {

    private lateinit var engine: AutomatedDispatchEngine

    @Before
    fun setup() {
        engine = AutomatedDispatchEngine()
    }

    @Test
    fun `findOptimalDrivers filters out drivers exceeding weight capacity`() {
        val pkg = PackageRequirements(
            orderId = "heavy-order",
            pickupLat = -23.5500,
            pickupLng = -46.6300,
            deliveryLat = -23.5600,
            deliveryLng = -46.6400,
            weightKg = 35.0 // Exceeds bike (5kg) and motorcycle (20kg)
        )

        val driverBike = DriverProfile("d1", "Bicicleta", -23.5501, -46.6301, VehicleType.BICYCLE)
        val driverMoto = DriverProfile("d2", "Moto", -23.5502, -46.6302, VehicleType.MOTORCYCLE)
        val driverCar = DriverProfile("d3", "Carro", -23.5520, -46.6320, VehicleType.CAR)

        val matches = engine.findOptimalDrivers(pkg, listOf(driverBike, driverMoto, driverCar))

        // Only car can carry 35kg
        assertEquals(1, matches.size)
        assertEquals("d3", matches[0].driver.id)
    }

    @Test
    fun `findOptimalDrivers ranks closest driver first when ratings are equal`() {
        val pkg = PackageRequirements(
            orderId = "pizza-order",
            pickupLat = -23.5500,
            pickupLng = -46.6300,
            deliveryLat = -23.5600,
            deliveryLng = -46.6400,
            weightKg = 3.0
        )

        val closeDriver = DriverProfile("d-close", "Perto", -23.5505, -46.6305, VehicleType.MOTORCYCLE, rating = 5.0)
        val farDriver = DriverProfile("d-far", "Longe", -23.5800, -46.6800, VehicleType.MOTORCYCLE, rating = 5.0)

        val matches = engine.findOptimalDrivers(pkg, listOf(farDriver, closeDriver))

        assertEquals(2, matches.size)
        assertEquals("d-close", matches[0].driver.id)
        assertTrue(matches[0].matchScore > matches[1].matchScore)
    }

    @Test
    fun `findOptimalDrivers excludes unavailable drivers`() {
        val pkg = PackageRequirements(
            orderId = "p-1",
            pickupLat = 0.0,
            pickupLng = 0.0,
            deliveryLat = 1.0,
            deliveryLng = 1.0,
            weightKg = 2.0
        )

        val busyDriver = DriverProfile("d-busy", "Ocupado", 0.001, 0.001, VehicleType.MOTORCYCLE, isAvailable = false)

        val matches = engine.findOptimalDrivers(pkg, listOf(busyDriver))
        assertTrue(matches.isEmpty())
    }
}
