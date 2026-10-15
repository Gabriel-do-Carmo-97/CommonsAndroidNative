package br.com.wgc.force_update.homologation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [DeviceHomologationSuite].
 */
class DeviceHomologationSuiteTest {

    private lateinit var suite: DeviceHomologationSuite

    @Before
    fun setup() {
        suite = DeviceHomologationSuite()
    }

    @Test
    fun `evaluateDevice com API 24 deve retornar incompativel`() {
        val result = suite.evaluateDevice(apiLevel = 24, manufacturerName = "samsung")

        assertFalse(result.isCompatible)
        assertTrue(result.recommendations.any { it.contains("não suportada") })
    }

    @Test
    fun `evaluateDevice com Xiaomi na API 34 deve retornar compativel e recomendacao de bateria MIUI`() {
        val result = suite.evaluateDevice(apiLevel = 34, manufacturerName = "Xiaomi")

        assertTrue(result.isCompatible)
        assertEquals(DeviceManufacturer.XIAOMI, result.manufacturer)
        assertTrue(result.recommendations.any { it.contains("MIUI") })
    }

    @Test
    fun `evaluateDevice com Samsung na API 33 deve retornar compativel e recomendacao de suspensao`() {
        val result = suite.evaluateDevice(apiLevel = 33, manufacturerName = "samsung")

        assertTrue(result.isCompatible)
        assertEquals(DeviceManufacturer.SAMSUNG, result.manufacturer)
        assertTrue(result.recommendations.any { it.contains("Aplicativos nunca suspensos") })
    }

    @Test
    fun `evaluateDevice com Pixel na API 35 deve retornar compativel sem alertas agressivos`() {
        val result = suite.evaluateDevice(apiLevel = 35, manufacturerName = "Google")

        assertTrue(result.isCompatible)
        assertEquals(DeviceManufacturer.GOOGLE, result.manufacturer)
        assertTrue(result.recommendations.isEmpty())
    }
}
