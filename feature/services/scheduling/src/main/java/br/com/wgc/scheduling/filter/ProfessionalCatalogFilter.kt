package br.com.wgc.scheduling.filter

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Critérios de ordenação do catálogo de profissionais e prestadores de serviços.
 */
enum class ProfessionalSortOption {
    /** Maior avaliação média dos clientes primeiro (Top Rated). */
    RATING_DESCENDING,
    /** Menor preço por hora ou sessão primeiro (Mais Econômico). */
    PRICE_ASCENDING,
    /** Maior preço por hora ou sessão primeiro (Premium). */
    PRICE_DESCENDING,
    /** Maior tempo de experiência no mercado primeiro. */
    EXPERIENCE_DESCENDING
}

/**
 * Perfil público e profissional de um prestador de serviço cadastrado.
 *
 * @property id Identificador exclusivo do profissional.
 * @property name Nome civil ou comercial.
 * @property specialty Especialidade ou categoria de atuação (ex: "Barbeiro Master", "Fisioterapeuta").
 * @property rating Avaliação média (escala 0.0 a 5.0).
 * @property reviewsCount Total consolidado de avaliações recebidas.
 * @property hourlyRate Valor monetário por hora/sessão em Reais.
 * @property hasImmediateAvailability Indica se possui vagas abertas para a data de hoje.
 * @property yearsOfExperience Anos de experiência comprovada na área.
 */
data class ProfessionalProfile(
    val id: String,
    val name: String,
    val specialty: String,
    val rating: Double,
    val reviewsCount: Int,
    val hourlyRate: Double,
    val hasImmediateAvailability: Boolean,
    val yearsOfExperience: Int = 1
)

/**
 * Parâmetros de busca e filtragem dinâmica para a lista de prestadores.
 *
 * @property specialtyFilter Filtro por especialidade ou palavra-chave (ou null para todas).
 * @property minRating Avaliação mínima aceita (padrão 0.0).
 * @property maxHourlyRate Teto máximo de valor por hora aceito (ou null para sem limite).
 * @property onlyImmediateAvailability Filtra exclusivamente profissionais com vagas imediatas hoje.
 * @property sortBy Critério de ordenação da lista ([ProfessionalSortOption]).
 */
data class ProfessionalFilterCriteria(
    val specialtyFilter: String? = null,
    val minRating: Double = 0.0,
    val maxHourlyRate: Double? = null,
    val onlyImmediateAvailability: Boolean = false,
    val sortBy: ProfessionalSortOption = ProfessionalSortOption.RATING_DESCENDING
)

/**
 * Mecanismo corporativo de Busca, Filtragem e Ranqueamento de Profissionais e Prestadores.
 *
 * Permite ao usuário refinar e comparar múltiplos profissionais com base em especialidade,
 * preço da consulta/serviço, reputação média e disponibilidade imediata.
 *
 * ### 📌 Quando Usar:
 * Utilize na tela de seleção de profissional ([br.com.wgc.scheduling.screen.SchedulingScreen])
 * para atualizar a listagem de cards à medida que o usuário ajusta filtros e ordenações.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val filterEngine = ProfessionalCatalogFilter()
 * val criteria = ProfessionalFilterCriteria(
 *     minRating = 4.5,
 *     onlyImmediateAvailability = true,
 *     sortBy = ProfessionalSortOption.PRICE_ASCENDING
 * )
 * val matchedProfessionals = filterEngine.applyFilter(allProfessionals, criteria)
 * ```
 */
@Singleton
class ProfessionalCatalogFilter @Inject constructor() {

    /**
     * Aplica os filtros de busca e ordena a lista resultante de profissionais.
     *
     * @param candidates Lista completa de prestadores disponíveis.
     * @param criteria Parâmetros de filtro e ordenação configurados.
     * @return Lista filtrada e ordenada de [ProfessionalProfile].
     */
    fun applyFilter(
        candidates: List<ProfessionalProfile>,
        criteria: ProfessionalFilterCriteria
    ): List<ProfessionalProfile> {
        val filtered = candidates.filter { prof ->
            // Filtro por especialidade (case insensitive)
            val matchesSpecialty = criteria.specialtyFilter.isNullOrBlank() ||
                    prof.specialty.contains(criteria.specialtyFilter, ignoreCase = true) ||
                    prof.name.contains(criteria.specialtyFilter, ignoreCase = true)

            // Filtro por avaliação mínima
            val matchesRating = prof.rating >= criteria.minRating

            // Filtro por teto de preço
            val matchesPrice = criteria.maxHourlyRate == null || prof.hourlyRate <= criteria.maxHourlyRate

            // Filtro por disponibilidade imediata
            val matchesAvailability = !criteria.onlyImmediateAvailability || prof.hasImmediateAvailability

            matchesSpecialty && matchesRating && matchesPrice && matchesAvailability
        }

        return when (criteria.sortBy) {
            ProfessionalSortOption.RATING_DESCENDING -> filtered.sortedByDescending { it.rating }
            ProfessionalSortOption.PRICE_ASCENDING -> filtered.sortedBy { it.hourlyRate }
            ProfessionalSortOption.PRICE_DESCENDING -> filtered.sortedByDescending { it.hourlyRate }
            ProfessionalSortOption.EXPERIENCE_DESCENDING -> filtered.sortedByDescending { it.yearsOfExperience }
        }
    }
}
