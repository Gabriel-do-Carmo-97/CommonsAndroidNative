package br.com.wgc.reviews_store.reputation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [ProviderReviewManager] validando agregação de avaliações e regras da In-App Review API.
 */
class ProviderReviewManagerTest {

    private lateinit var reviewManager: ProviderReviewManager

    @Before
    fun setup() {
        reviewManager = ProviderReviewManager()
    }

    @Test
    fun `calculateReputation accurately computes average and recommendation percentage`() {
        val reviews = listOf(
            CustomerReview("r1", "Ana", rating = 5, comment = "Excelente", dateFormatted = "10/10"),
            CustomerReview("r2", "Bruno", rating = 5, comment = "Muito bom", dateFormatted = "11/10"),
            CustomerReview("r3", "Carla", rating = 4, comment = "Bom", dateFormatted = "12/10"),
            CustomerReview("r4", "Daniel", rating = 2, comment = "Atrasou", dateFormatted = "13/10")
        )

        val summary = reviewManager.calculateReputation("prov-1", reviews)

        assertEquals("prov-1", summary.providerId)
        assertEquals(4, summary.totalReviewsCount)
        assertEquals(2, summary.fiveStarsCount)
        assertEquals(1, summary.fourStarsCount)
        assertEquals(1, summary.twoStarsCount)
        // (5 + 5 + 4 + 2) / 4 = 16 / 4 = 4.0
        assertEquals(4.0, summary.averageRating, 0.01)
        // 3 positive out of 4 = 75%
        assertEquals(75, summary.recommendationPercentage)
    }

    @Test
    fun `evaluateInAppReviewPrompt allows prompt for 5 stars after 31 days`() {
        val dayMs = 24 * 60 * 60 * 1000L
        val t0 = 1000000000000L
        val tPastPrompt = t0 - (31 * dayMs) // 31 days ago

        val decision = reviewManager.evaluateInAppReviewPrompt(
            userRatingGiven = 5,
            previousPromptTimestamp = tPastPrompt,
            currentTimeMillis = t0
        )

        assertTrue(decision.shouldPromptGooglePlayReview)
        assertTrue(decision.reason.contains("positiva"))
    }

    @Test
    fun `evaluateInAppReviewPrompt blocks prompt when rating is less than 4`() {
        val decision = reviewManager.evaluateInAppReviewPrompt(userRatingGiven = 3)
        assertFalse(decision.shouldPromptGooglePlayReview)
        assertTrue(decision.reason.contains("neutra ou negativa"))
    }

    @Test
    fun `evaluateInAppReviewPrompt blocks prompt when cooldown has not elapsed`() {
        val dayMs = 24 * 60 * 60 * 1000L
        val t0 = 1000000000000L
        val tRecentPrompt = t0 - (10 * dayMs) // Only 10 days ago (quota requires 30)

        val decision = reviewManager.evaluateInAppReviewPrompt(
            userRatingGiven = 5,
            previousPromptTimestamp = tRecentPrompt,
            currentTimeMillis = t0
        )

        assertFalse(decision.shouldPromptGooglePlayReview)
        assertTrue(decision.reason.contains("Quota"))
    }
}
