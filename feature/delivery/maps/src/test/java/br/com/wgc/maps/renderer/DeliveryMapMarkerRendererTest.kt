package br.com.wgc.maps.renderer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [DeliveryMapMarkerRenderer] validando construção de marcadores,
 * cálculo de ETA e delimitação de BoundingBox.
 */
class DeliveryMapMarkerRendererTest {

    private lateinit var renderer: DeliveryMapMarkerRenderer

    @Before
    fun setup() {
        renderer = DeliveryMapMarkerRenderer()
    }

    @Test
    fun `createStoreMarker creates marker with STORE type and proper zIndex`() {
        val marker = renderer.createStoreMarker("Pizzaria Bela Vista", -23.55, -46.63)

        assertEquals("marker_store", marker.id)
        assertEquals("Pizzaria Bela Vista", marker.title)
        assertEquals(DeliveryMarkerType.STORE, marker.type)
        assertEquals(2.0f, marker.zIndex, 0.01f)
    }

    @Test
    fun `createCustomerMarker creates marker with CUSTOMER type and proper zIndex`() {
        val marker = renderer.createCustomerMarker("Casa", -23.56, -46.65)

        assertEquals("marker_customer", marker.id)
        assertEquals("Casa", marker.title)
        assertEquals(DeliveryMarkerType.CUSTOMER, marker.type)
        assertEquals(1.0f, marker.zIndex, 0.01f)
    }

    @Test
    fun `createDriverMarker formats ETA properly when distance is provided`() {
        // 1500m at 30km/h = 180s = 3 minutes
        val marker = renderer.createDriverMarker(
            driverName = "Carlos",
            latitude = -23.555,
            longitude = -46.64,
            distanceMeters = 1500.0,
            averageSpeedKmH = 30.0
        )

        assertEquals("marker_driver", marker.id)
        assertEquals(DeliveryMarkerType.DRIVER, marker.type)
        assertEquals(3.0f, marker.zIndex, 0.01f)
        assertTrue(marker.snippet.contains("3 min"))
    }

    @Test
    fun `computeBoundingBox correctly calculates bounds for multiple markers`() {
        val markers = listOf(
            renderer.createStoreMarker("Loja", -23.50, -46.60),
            renderer.createCustomerMarker("Cliente", -23.60, -46.70)
        )

        val box = renderer.computeBoundingBox(markers)
        assertNotNull(box)
        assertEquals(-23.60, box!!.minLatitude, 0.001)
        assertEquals(-23.50, box.maxLatitude, 0.001)
        assertEquals(-46.70, box.minLongitude, 0.001)
        assertEquals(-46.60, box.maxLongitude, 0.001)
        assertEquals(-23.55, box.centerLatitude, 0.001)
        assertEquals(-46.65, box.centerLongitude, 0.001)
    }

    @Test
    fun `computeBoundingBox returns null for empty list`() {
        val box = renderer.computeBoundingBox(emptyList())
        assertNull(box)
    }
}
