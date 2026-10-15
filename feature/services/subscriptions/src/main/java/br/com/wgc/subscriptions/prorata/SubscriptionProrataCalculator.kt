package br.com.wgc.subscriptions.prorata

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Demonstrativo detalhado do cálculo proporcional (Pro Rata) para alteração de plano de assinatura.
 *
 * @property currentPlanPrice Preço integral do plano atual no ciclo.
 * @property newPlanPrice Preço integral do novo plano selecionado.
 * @property totalDaysInCycle Duração total do ciclo faturado em dias (ex: 30 dias).
 * @property daysRemainingInCycle Quantidade de dias restantes não usufruídos no ciclo atual.
 * @property unusedCreditAmount Crédito financeiro proporcional residual gerado pelo plano antigo.
 * @property netAmountToPayNow Valor líquido final a ser debitado imediatamente no cartão (mínimo R$ 0,00).
 * @property isUpgrade Indica se a operação representa um aumento de categoria (upgrade) ou redução (downgrade).
 */
data class ProrataCalculationResult(
    val currentPlanPrice: Double,
    val newPlanPrice: Double,
    val totalDaysInCycle: Int,
    val daysRemainingInCycle: Int,
    val unusedCreditAmount: Double,
    val netAmountToPayNow: Double,
    val isUpgrade: Boolean
)

/**
 * Calculador corporativo de Migração e Upgrade de Planos com Ajuste Proporcional (Pro Rata).
 *
 * Garante equidade financeira ao calcular com precisão diária os créditos de dias não usufruídos
 * da assinatura atual, abatendo esse valor imediatamente na primeira cobrança do novo plano escolhido.
 *
 * ### 📌 Quando Usar:
 * Acione no fluxo de Troca de Plano da tela de Assinaturas ([br.com.wgc.subscriptions.screen.SubscriptionsScreen])
 * para apresentar ao cliente o resumo financeiro transparente antes da confirmação da migração.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val prorataCalculator = SubscriptionProrataCalculator()
 * val result = prorataCalculator.calculateProrata(
 *     currentPlanPrice = 60.00,
 *     newPlanPrice = 100.00,
 *     cycleStartMillis = inicioCicloMs,
 *     cycleEndMillis = fimCicloMs
 * )
 * println("Valor a pagar agora com crédito de dias restantes: R$ ${result.netAmountToPayNow}")
 * ```
 */
@Singleton
class SubscriptionProrataCalculator @Inject constructor() {

    companion object {
        private const val MILLIS_IN_A_DAY = 24 * 60 * 60 * 1000.0
    }

    /**
     * Calcula o ajuste proporcional de crédito para transição entre planos.
     *
     * @param currentPlanPrice Valor pago pelo plano atual.
     * @param newPlanPrice Valor nominal do novo plano desejado.
     * @param cycleStartMillis Carimbo de início do ciclo atual em milissegundos.
     * @param cycleEndMillis Carimbo de fim do ciclo atual em milissegundos.
     * @param changeDateMillis Carimbo de data/hora da solicitação de troca (padrão: agora).
     * @return [ProrataCalculationResult] consolidado.
     */
    fun calculateProrata(
        currentPlanPrice: Double,
        newPlanPrice: Double,
        cycleStartMillis: Long,
        cycleEndMillis: Long,
        changeDateMillis: Long = System.currentTimeMillis()
    ): ProrataCalculationResult {
        val totalDays = max(1, ((cycleEndMillis - cycleStartMillis) / MILLIS_IN_A_DAY).roundToInt())
        val remainingMillis = max(0L, cycleEndMillis - changeDateMillis)
        val remainingDays = (remainingMillis / MILLIS_IN_A_DAY).toInt()

        // Taxa diária do plano atual
        val dailyRate = currentPlanPrice / totalDays.toDouble()
        // Crédito residual de dias não utilizados
        val rawCredit = dailyRate * remainingDays.toDouble()
        val unusedCredit = (rawCredit * 100.0).roundToInt() / 100.0

        // Valor líquido cobrado agora (com abatimento do crédito residual)
        val rawNet = max(0.0, newPlanPrice - unusedCredit)
        val netAmount = (rawNet * 100.0).roundToInt() / 100.0

        val isUpgrade = newPlanPrice > currentPlanPrice

        return ProrataCalculationResult(
            currentPlanPrice = currentPlanPrice,
            newPlanPrice = newPlanPrice,
            totalDaysInCycle = totalDays,
            daysRemainingInCycle = remainingDays,
            unusedCreditAmount = unusedCredit,
            netAmountToPayNow = netAmount,
            isUpgrade = isUpgrade
        )
    }
}
