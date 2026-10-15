package br.com.wgc.scheduling.filter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [ProfessionalCatalogFilter] validando filtros de preço, avaliação, disponibilidade e ordenação.
 */
class ProfessionalCatalogFilterTest {

    private lateinit var filterEngine: ProfessionalCatalogFilter

    private val sampleProfessionals = listOf(
        ProfessionalProfile("p1", "Dr. Marcos", "Fisioterapia", rating = 4.9, reviewsCount = 120, hourlyRate = 180.0, hasImmediateAvailability = true, yearsOfExperience = 12),
        ProfessionalProfile("p2", "Dra. Renata", "Fisioterapia", rating = 4.6, reviewsCount = 80, hourlyRate = 140.0, hasImmediateAvailability = false, yearsOfExperience = 6),
        ProfessionalProfile("p3", "Carlos Silva", "Barbeiro", rating = 4.8, reviewsCount = 200, hourlyRate = 60.0, hasImmediateAvailability = true, yearsOfExperience = 8),
        ProfessionalProfile("p4", "Ana Paula", "Nutrição", rating = 4.2, reviewsCount = 30, hourlyRate = 100.0, hasImmediateAvailability = true, yearsOfExperience = 3)
    )

    @Before
    fun setup() {
        filterEngine = ProfessionalCatalogFilter()
    }

    @Test
    fun `applyFilter filters by specialty and orders by price ascending`() {
        val criteria = ProfessionalFilterCriteria(
            specialtyFilter = "Fisioterapia",
            sortBy = ProfessionalSortOption.PRICE_ASCENDING
        )

        val results = filterEngine.applyFilter(sampleProfessionals, criteria)

        assertEquals(2, results.size)
        assertEquals("p2", results[0].id) // Renata (140.0) before Marcos (180.0)
        assertEquals("p1", results[1].id)
    }

    @Test
    fun `applyFilter filters by immediate availability only`() {
        val criteria = ProfessionalFilterCriteria(
            onlyImmediateAvailability = true
        )

        val results = filterEngine.applyFilter(sampleProfessionals, criteria)

        assertEquals(3, results.size)
        assertTrue(results.all { it.hasImmediateAvailability })
        assertTrue(results.none { it.id == "p2" })
    }

    @Test
    fun `applyFilter filters by minimum rating and max hourly rate`() {
        val criteria = ProfessionalFilterCriteria(
            minRating = 4.5,
            maxHourlyRate = 150.0
        )

        val results = filterEngine.applyFilter(sampleProfessionals, criteria)

        // Only Carlos Silva (4.8, 60.0) and Dra. Renata (4.6, 140.0) qualify
        assertEquals(2, results.size)
        assertTrue(results.any { it.id == "p2" })
        assertTrue(results.any { it.id == "p3" })
    }
}
