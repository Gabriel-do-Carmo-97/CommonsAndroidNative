package br.com.wgc.maps.interpolator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [PositionInterpolator] validando interpolação espacial, azimute e menor arco angular.
 */
class PositionInterpolatorTest {

    private lateinit var interpolator: PositionInterpolator

    @Before
    fun setup() {
        interpolator = PositionInterpolator()
    }

    @Test
    fun `interpolate returns exact mid-point at fraction 0_5`() {
        val start = LatLngPoint(-20.0, -40.0)
        val end = LatLngPoint(-22.0, -42.0)

        val mid = interpolator.interpolate(start, end, 0.5f)

        assertEquals(-21.0, mid.latitude, 0.0001)
        assertEquals(-41.0, mid.longitude, 0.0001)
    }

    @Test
    fun `interpolate clamps fraction between 0 and 1`() {
        val start = LatLngPoint(-20.0, -40.0)
        val end = LatLngPoint(-22.0, -42.0)

        val beforeStart = interpolator.interpolate(start, end, -0.5f)
        assertEquals(-20.0, beforeStart.latitude, 0.0001)

        val afterEnd = interpolator.interpolate(start, end, 1.5f)
        assertEquals(-22.0, afterEnd.latitude, 0.0001)
    }

    @Test
    fun `computeBearing computes correct compass directions`() {
        val p1 = LatLngPoint(0.0, 0.0)
        val pNorth = LatLngPoint(1.0, 0.0)
        val pEast = LatLngPoint(0.0, 1.0)
        val pSouth = LatLngPoint(-1.0, 0.0)
        val pWest = LatLngPoint(0.0, -1.0)

        assertEquals(0.0f, interpolator.computeBearing(p1, pNorth), 0.5f)
        assertEquals(90.0f, interpolator.computeBearing(p1, pEast), 0.5f)
        assertEquals(180.0f, interpolator.computeBearing(p1, pSouth), 0.5f)
        assertEquals(270.0f, interpolator.computeBearing(p1, pWest), 0.5f)
    }

    @Test
    fun `interpolateRotation takes shortest angular path across 0 and 360 boundary`() {
        // Transitioning from 350° to 10° should go through 0° (20° total delta), not the long way (340°)
        val mid = interpolator.interpolateRotation(startAngle = 350.0f, endAngle = 10.0f, fraction = 0.5f)
        // Midpoint of 350° and 10° via shortest path is 0° (or 360°)
        assertTrue(mid in 359.0f..360.0f || mid in 0.0f..1.0f)
    }

    @Test
    fun `computeDistanceMeters calculates accurate distance`() {
        val spCenter = LatLngPoint(-23.550520, -46.633308)
        val spPaulista = LatLngPoint(-23.561414, -46.655881)

        val distance = interpolator.computeDistanceMeters(spCenter, spPaulista)
        // Distance between Praça da Sé and Paulista is approx ~2.6 km (2600m)
        assertTrue(distance in 2400.0..2800.0)
    }
}
