package br.com.wgc.reviews_store.reputation

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/**
 * Avaliação individual e comentário emitido por um consumidor verificado.
 *
 * @property reviewId Identificador único da avaliação.
 * @property authorName Nome público do cliente.
 * @property authorAvatarUrl URL da foto de perfil (ou null).
 * @property rating Pontuação atribuída de 1 a 5 estrelas.
 * @property comment Texto com feedback qualitativo sobre a experiência.
 * @property dateFormatted Data da avaliação (ex: "14 de Outubro de 2026").
 * @property isVerifiedPurchase Garante que o autor efetivamente contratou o serviço ou comprou o produto.
 * @property helpfulVotes Contador de votos de "Útil" recebidos de outros usuários.
 */
data class CustomerReview(
    val reviewId: String,
    val authorName: String,
    val authorAvatarUrl: String? = null,
    val rating: Int,
    val comment: String,
    val dateFormatted: String,
    val isVerifiedPurchase: Boolean = true,
    val helpfulVotes: Int = 0
)

/**
 * Síntese consolidada de reputação e distribuição de notas do prestador ou loja parceira.
 *
 * @property providerId Identificador do estabelecimento ou profissional avaliado.
 * @property averageRating Média ponderada das avaliações (escala 1.0 a 5.0).
 * @property totalReviewsCount Volume total de avaliações computadas.
 * @property fiveStarsCount Quantidade de notas 5.
 * @property fourStarsCount Quantidade de notas 4.
 * @property threeStarsCount Quantidade de notas 3.
 * @property twoStarsCount Quantidade de notas 2.
 * @property oneStarCount Quantidade de notas 1.
 * @property recommendationPercentage Percentual de clientes que recomendam o serviço (notas 4 e 5).
 */
data class ProviderReputationSummary(
    val providerId: String,
    val averageRating: Double,
    val totalReviewsCount: Int,
    val fiveStarsCount: Int,
    val fourStarsCount: Int,
    val threeStarsCount: Int,
    val twoStarsCount: Int,
    val oneStarCount: Int,
    val recommendationPercentage: Int
)

/**
 * Decisão algorítmica sobre a conveniência de disparo da Google Play In-App Review API.
 *
 * @property shouldPromptGooglePlayReview Indica se o diálogo nativo da Play Store deve ser invocado.
 * @property reason Justificativa técnica e mercadológica da decisão.
 */
data class InAppReviewPromptDecision(
    val shouldPromptGooglePlayReview: Boolean,
    val reason: String
)

/**
 * Gerenciador corporativo de Avaliações, Reputação e Integração com a Google Play In-App Review API.
 *
 * Consolida estatísticas de satisfação do cliente (NPS, distribuição de estrelas e comentários verificados)
 * e determina o momento ideal para solicitar a avaliação pública na Play Store, conforme as diretrizes oficiais
 * do Google (disparo exclusivo após satisfação elevada e com intervalo mínimo de 30 dias).
 *
 * ### 📌 Quando Usar:
 * Utilize na tela de avaliações da loja ([br.com.wgc.reviews_store.screen.ReviewsStoreScreen])
 * e após a conclusão exitosa de pedidos ou serviços no fechamento da jornada.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val reviewManager = ProviderReviewManager()
 * val summary = reviewManager.calculateReputation("loja_1", reviewsList)
 *
 * // Avaliar se dispara In-App Review após nota do cliente:
 * val decision = reviewManager.evaluateInAppReviewPrompt(userRatingGiven = 5, previousPromptTimestamp = tUltima)
 * if (decision.shouldPromptGooglePlayReview) {
 *     reviewManagerPlayStoreHelper.launchReviewFlow(activity)
 * }
 * ```
 */
@Singleton
class ProviderReviewManager @Inject constructor() {

    companion object {
        private const val MIN_DAYS_BETWEEN_PROMPTS = 30
        private const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L
    }

    /**
     * Calcula o resumo estatístico completo a partir da coleção de avaliações.
     *
     * @param providerId Identificador do prestador avaliado.
     * @param reviews Coleção de [CustomerReview] computadas.
     * @return [ProviderReputationSummary] com distribuição detalhada de estrelas e médias.
     */
    fun calculateReputation(providerId: String, reviews: List<CustomerReview>): ProviderReputationSummary {
        if (reviews.isEmpty()) {
            return ProviderReputationSummary(
                providerId = providerId,
                averageRating = 5.0,
                totalReviewsCount = 0,
                fiveStarsCount = 0,
                fourStarsCount = 0,
                threeStarsCount = 0,
                twoStarsCount = 0,
                oneStarCount = 0,
                recommendationPercentage = 100
            )
        }

        val total = reviews.size
        val count5 = reviews.count { it.rating == 5 }
        val count4 = reviews.count { it.rating == 4 }
        val count3 = reviews.count { it.rating == 3 }
        val count2 = reviews.count { it.rating == 2 }
        val count1 = reviews.count { it.rating == 1 }

        val sum = reviews.sumOf { it.rating }
        val rawAverage = sum.toDouble() / total.toDouble()
        val roundedAverage = (rawAverage * 10.0).roundToInt() / 10.0

        val positiveCount = count5 + count4
        val recPercentage = ((positiveCount.toDouble() / total.toDouble()) * 100.0).roundToInt()

        return ProviderReputationSummary(
            providerId = providerId,
            averageRating = roundedAverage,
            totalReviewsCount = total,
            fiveStarsCount = count5,
            fourStarsCount = count4,
            threeStarsCount = count3,
            twoStarsCount = count2,
            oneStarCount = count1,
            recommendationPercentage = recPercentage
        )
    }

    /**
     * Avalia se as condições são propícias para exibir o diálogo da Google Play In-App Review API.
     *
     * Condições exigidas:
     * 1. A avaliação recente do cliente deve ser positiva (>= 4 estrelas).
     * 2. O usuário não deve ter recebido o diálogo nos últimos 30 dias (respeito às quotas do Google Play).
     *
     * @param userRatingGiven Nota de 1 a 5 atribuída pelo cliente nesta interação.
     * @param previousPromptTimestamp Carimbo da última exibição da In-App Review no aparelho (ou 0 se nunca).
     * @param currentTimeMillis Carimbo de data/hora atual.
     * @return [InAppReviewPromptDecision] orientando se a chamada deve ser disparada.
     */
    fun evaluateInAppReviewPrompt(
        userRatingGiven: Int,
        previousPromptTimestamp: Long = 0L,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): InAppReviewPromptDecision {
        if (userRatingGiven < 4) {
            return InAppReviewPromptDecision(
                shouldPromptGooglePlayReview = false,
                reason = "Avaliação neutra ou negativa ($userRatingGiven estrelas). Direcionar feedback internamente ao suporte."
            )
        }

        val daysSinceLastPrompt = (currentTimeMillis - previousPromptTimestamp) / MILLIS_PER_DAY
        if (previousPromptTimestamp > 0L && daysSinceLastPrompt < MIN_DAYS_BETWEEN_PROMPTS) {
            return InAppReviewPromptDecision(
                shouldPromptGooglePlayReview = false,
                reason = "Quota de exibição ativa: última solicitação ocorreu há $daysSinceLastPrompt dias (mínimo de $MIN_DAYS_BETWEEN_PROMPTS dias)."
            )
        }

        return InAppReviewPromptDecision(
            shouldPromptGooglePlayReview = true,
            reason = "Experiência altamente positiva ($userRatingGiven estrelas) e intervalo de quota respeitado. Apto para In-App Review."
        )
    }
}
