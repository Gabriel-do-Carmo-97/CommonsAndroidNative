package br.com.wgc.order_tracking.timeline

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [OrderTrackingTimelineManager] validando evolução da timeline,
 * carimbos temporais e cálculo preditivo de ETA.
 */
class OrderTrackingTimelineManagerTest {

    private lateinit var timelineManager: OrderTrackingTimelineManager

    @Before
    fun setup() {
        timelineManager = OrderTrackingTimelineManager()
    }

    @Test
    fun `createInitialTimeline starts with CONFIRMED and 4 structured steps`() {
        val now = 1700000000000L
        val timeline = timelineManager.createInitialTimeline("ORD-1", now, baseEtaMinutes = 40)

        assertEquals("ORD-1", timeline.orderId)
        assertEquals(TimelineStage.CONFIRMED, timeline.currentStage)
        assertEquals(4, timeline.steps.size)
        assertEquals(40, timeline.estimatedMinutesRemaining)
        assertEquals(now + (40 * 60 * 1000L), timeline.estimatedDeliveryTimestamp)

        // Step 1 is completed and current
        assertTrue(timeline.steps[0].isCompleted)
        assertTrue(timeline.steps[0].isCurrent)
        assertEquals(now, timeline.steps[0].timestampMillis)

        // Step 2 is pending
        assertFalse(timeline.steps[1].isCompleted)
        assertFalse(timeline.steps[1].isCurrent)
        assertNull(timeline.steps[1].timestampMillis)
    }

    @Test
    fun `advanceToStage correctly stamps time and marks intermediate stages as completed`() {
        val t0 = 1700000000000L
        val t1 = t0 + (10 * 60 * 1000L) // 10 minutes later

        var timeline = timelineManager.createInitialTimeline("ORD-2", t0)
        timeline = timelineManager.advanceToStage(timeline, TimelineStage.PREPARING, t1)

        assertEquals(TimelineStage.PREPARING, timeline.currentStage)
        assertEquals(20, timeline.estimatedMinutesRemaining)

        // Step 1 completed, not current
        assertTrue(timeline.steps[0].isCompleted)
        assertFalse(timeline.steps[0].isCurrent)

        // Step 2 completed, is current
        assertTrue(timeline.steps[1].isCompleted)
        assertTrue(timeline.steps[1].isCurrent)
        assertEquals(t1, timeline.steps[1].timestampMillis)

        // Step 3 pending
        assertFalse(timeline.steps[2].isCompleted)
    }

    @Test
    fun `advanceToStage to DELIVERED sets 0 minutes remaining`() {
        val t0 = 1700000000000L
        val tFinal = t0 + (35 * 60 * 1000L)

        var timeline = timelineManager.createInitialTimeline("ORD-3", t0)
        timeline = timelineManager.advanceToStage(timeline, TimelineStage.DELIVERED, tFinal)

        assertEquals(TimelineStage.DELIVERED, timeline.currentStage)
        assertEquals(0, timeline.estimatedMinutesRemaining)
        assertEquals(tFinal, timeline.estimatedDeliveryTimestamp)
        assertTrue(timeline.steps.all { it.isCompleted })
    }

    @Test
    fun `calculatePredictiveEta incorporates traffic factor and prep time`() {
        // 5 km at normal traffic (factor 1.0, 25km/h = 12 mins travel) + 10 mins prep = 22 mins
        val normalEta = timelineManager.calculatePredictiveEta(distanceKm = 5.0, trafficFactor = 1.0, basePrepTimeMinutes = 10)
        assertEquals(22, normalEta)

        // 5 km with heavy traffic (factor 1.5, speed = 16.66 km/h = 18 mins travel) + 10 prep = 28 mins
        val heavyEta = timelineManager.calculatePredictiveEta(distanceKm = 5.0, trafficFactor = 1.5, basePrepTimeMinutes = 10)
        assertEquals(28, heavyEta)
    }
}
