package br.com.wgc.subscriptions.catalog

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/**
 * Ciclo de faturamento e recorrência de cobrança do plano de assinatura.
 */
enum class BillingCycle(val months: Int, val label: String) {
    /** Renovação mensal automática. */
    MONTHLY(1, "Mensal"),
    /** Cobrança a cada 3 meses com desconto intermediário. */
    QUARTERLY(3, "Trimestral"),
    /** Cobrança anual única com máxima economia de custo. */
    ANNUAL(12, "Anual")
}

/**
 * Especificação e benefícios de um plano de assinatura comercial.
 *
 * @property id Identificador exclusivo do plano (ex: "plan_annual_pro").
 * @property name Nome comercial do plano (ex: "WGC Pro Anual").
 * @property billingCycle Periodicidade de faturamento ([BillingCycle]).
 * @property monthlyEquivalentPrice Preço equivalente por mês para comparação.
 * @property totalBillingPrice Valor total faturado no momento da adesão/renovação.
 * @property discountPercentage Percentual de economia em relação ao plano mensal (ex: 25 para 25% OFF).
 * @property isMostPopular Sinalizador de destaque visual para o plano mais vantajoso (badge "Mais Escolhido").
 * @property features Lista de benefícios e diferenciais inclusos no plano.
 */
data class SubscriptionPlanTier(
    val id: String,
    val name: String,
    val billingCycle: BillingCycle,
    val monthlyEquivalentPrice: Double,
    val totalBillingPrice: Double,
    val discountPercentage: Int = 0,
    val isMostPopular: Boolean = false,
    val features: List<String>
)

/**
 * Gerenciador corporativo do Catálogo de Planos de Assinatura e Cálculo de Economia.
 *
 * Estrutura a precificação comparativa entre ciclos Mensal, Trimestral e Anual, destacando
 * automaticamente descontos de fidelidade (ex: "Economize 25% no plano anual") e selos de destaque.
 *
 * ### 📌 Quando Usar:
 * Utilize na tela de vitrine de assinaturas ([br.com.wgc.subscriptions.screen.SubscriptionsScreen])
 * para renderizar os cards comparativos de planos com destaque visual de conversão.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val catalogManager = SubscriptionPlanCatalogManager()
 * val plans = catalogManager.getAvailablePlans(baseMonthlyPrice = 49.90)
 * val annualPlan = plans.first { it.billingCycle == BillingCycle.ANNUAL }
 * println("Economia do plano anual: ${annualPlan.discountPercentage}%")
 * ```
 */
@Singleton
class SubscriptionPlanCatalogManager @Inject constructor() {

    /**
     * Retorna a matriz de planos calculada dinamicamente a partir de um valor base mensal.
     *
     * @param baseMonthlyPrice Preço da assinatura mensal padrão.
     * @param quarterlyDiscountPercent Percentual de desconto do plano trimestral (padrão 10%).
     * @param annualDiscountPercent Percentual de desconto do plano anual (padrão 25%).
     * @param standardFeatures Benefícios inclusos.
     * @return Lista de [SubscriptionPlanTier] calculada para Mensal, Trimestral e Anual.
     */
    fun getAvailablePlans(
        baseMonthlyPrice: Double = 49.90,
        quarterlyDiscountPercent: Int = 10,
        annualDiscountPercent: Int = 25,
        standardFeatures: List<String> = listOf(
            "Acesso ilimitado a todos os serviços",
            "Suporte prioritário 24/7 via WhatsApp",
            "Descontos exclusivos em produtos parceiros",
            "Sem taxa de serviço ou fidelidade"
        )
    ): List<SubscriptionPlanTier> {
        // Mensal: sem desconto
        val monthlyPlan = SubscriptionPlanTier(
            id = "wgc_sub_monthly",
            name = "Plano Mensal",
            billingCycle = BillingCycle.MONTHLY,
            monthlyEquivalentPrice = baseMonthlyPrice,
            totalBillingPrice = baseMonthlyPrice,
            discountPercentage = 0,
            isMostPopular = false,
            features = standardFeatures
        )

        // Trimestral: 10% de desconto
        val quarterlyTotal = (baseMonthlyPrice * 3) * (1.0 - (quarterlyDiscountPercent / 100.0))
        val quarterlyMonthlyEq = quarterlyTotal / 3.0
        val quarterlyPlan = SubscriptionPlanTier(
            id = "wgc_sub_quarterly",
            name = "Plano Trimestral",
            billingCycle = BillingCycle.QUARTERLY,
            monthlyEquivalentPrice = quarterlyMonthlyEq,
            totalBillingPrice = quarterlyTotal,
            discountPercentage = quarterlyDiscountPercent,
            isMostPopular = false,
            features = standardFeatures + "Economia garantida a cada 3 meses"
        )

        // Anual: 25% de desconto (Mais Popular)
        val annualTotal = (baseMonthlyPrice * 12) * (1.0 - (annualDiscountPercent / 100.0))
        val annualMonthlyEq = annualTotal / 12.0
        val annualPlan = SubscriptionPlanTier(
            id = "wgc_sub_annual",
            name = "Plano Anual",
            billingCycle = BillingCycle.ANNUAL,
            monthlyEquivalentPrice = annualMonthlyEq,
            totalBillingPrice = annualTotal,
            discountPercentage = annualDiscountPercent,
            isMostPopular = true,
            features = standardFeatures + listOf(
                "Máxima economia de 25% OFF",
                "Badge exclusivo de Membro Fundador VIP"
            )
        )

        return listOf(monthlyPlan, quarterlyPlan, annualPlan)
    }

    /**
     * Calcula o percentual de desconto real obtido entre o valor mensal acumulado e o valor total cobrado.
     *
     * @param fullRegularPrice Preço integral sem desconto (ex: mensal * 12).
     * @param discountedPrice Preço promocional cobrado no ciclo.
     * @return Percentual inteiro arredondado de desconto.
     */
    fun calculateSavingsPercentage(fullRegularPrice: Double, discountedPrice: Double): Int {
        if (fullRegularPrice <= 0.0 || discountedPrice >= fullRegularPrice) return 0
        val saving = (fullRegularPrice - discountedPrice) / fullRegularPrice
        return (saving * 100.0).roundToInt()
    }
}
