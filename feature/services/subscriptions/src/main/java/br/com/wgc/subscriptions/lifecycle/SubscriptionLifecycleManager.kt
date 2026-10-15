package br.com.wgc.subscriptions.lifecycle

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

/**
 * Estados do ciclo de vida contratual e operacional da assinatura do usuário.
 */
enum class SubscriptionStatus(val label: String) {
    /** Assinatura adimplente com renovação automática regular. */
    ACTIVE("Ativa"),
    /** Cobrança falhou; acesso temporário garantido durante período de tolerância (Grace Period). */
    GRACE_PERIOD("Período de Graça"),
    /** Assinatura cancelada voluntariamente, ativa até o final do ciclo já pago. */
    CANCELLED("Cancelada"),
    /** Pagamento inadimplente; acesso aos recursos premium bloqueado. */
    DELINQUENT("Inadimplente / Suspensa")
}

/**
 * Snapshot do estado vigente da assinatura do titular.
 *
 * @property subscriptionId Identificador único do contrato de assinatura.
 * @property planId Código do plano associado.
 * @property planName Nome comercial exibido.
 * @property status Estado de vigência contratual ([SubscriptionStatus]).
 * @property currentPeriodStartMillis Início do ciclo de faturamento atual em milissegundos.
 * @property currentPeriodEndMillis Término do ciclo pago em milissegundos.
 * @property gracePeriodEndMillis Término do período de tolerância em milissegundos (ou null se adimplente).
 * @property autoRenew Sinalizador de renovação automática no final do ciclo.
 */
data class UserSubscriptionState(
    val subscriptionId: String,
    val planId: String,
    val planName: String,
    val status: SubscriptionStatus,
    val currentPeriodStartMillis: Long,
    val currentPeriodEndMillis: Long,
    val gracePeriodEndMillis: Long? = null,
    val autoRenew: Boolean = true
)

/**
 * Alerta e banner informativo renderizado nas telas principais sobre a situação da assinatura.
 *
 * @property title Título do aviso.
 * @property message Mensagem orientativa detalhada.
 * @property actionLabel Rótulo do botão de ação rápida (ex: "Atualizar Cartão").
 * @property isCritical Indica se é um alerta vermelho de bloqueio imediato ou informativo amarelo.
 */
data class SubscriptionBannerAlert(
    val title: String,
    val message: String,
    val actionLabel: String,
    val isCritical: Boolean
)

/**
 * Gerenciador corporativo do Ciclo de Vida da Assinatura e Banners de Cobrança.
 *
 * Audita as transições de estado contratuais (Ativa -> Período de Graça -> Inadimplente)
 * e gera os banners visuais informativos correspondentes para instruir o cliente a regularizar
 * seu meio de pagamento antes do bloqueio definitivo de serviços.
 *
 * ### 📌 Quando Usar:
 * Injete nas telas principais ([br.com.wgc.subscriptions.screen.SubscriptionsScreen]) e na home do app
 * para exibir faixas de alerta preventivas caso ocorra falha no cartão de crédito cadastrado.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val lifecycleManager = SubscriptionLifecycleManager()
 * val banner = lifecycleManager.generateBannerAlert(userSubscription, System.currentTimeMillis())
 * if (banner != null) {
 *     exibirBannerNoTopo(banner.title, banner.message, banner.actionLabel)
 * }
 * ```
 */
@Singleton
class SubscriptionLifecycleManager @Inject constructor() {

    /**
     * Avalia e transiciona o estado da assinatura com base nos carimbos de data/hora atuais.
     *
     * @param subscription Estado registrado no banco/storage.
     * @param currentTimeMillis Carimbo de data/hora atual em milissegundos.
     * @return [UserSubscriptionState] com status recalculado conforme expirações.
     */
    fun evaluateSubscriptionState(
        subscription: UserSubscriptionState,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): UserSubscriptionState {
        return when (subscription.status) {
            SubscriptionStatus.ACTIVE -> {
                if (currentTimeMillis > subscription.currentPeriodEndMillis) {
                    if (subscription.autoRenew) {
                        // Tentativa de cobrança falhou -> entra em Período de Graça de 3 dias
                        val graceEnd = subscription.currentPeriodEndMillis + (3 * 24 * 60 * 60 * 1000L)
                        subscription.copy(
                            status = SubscriptionStatus.GRACE_PERIOD,
                            gracePeriodEndMillis = graceEnd
                        )
                    } else {
                        subscription.copy(status = SubscriptionStatus.CANCELLED)
                    }
                } else {
                    subscription
                }
            }
            SubscriptionStatus.GRACE_PERIOD -> {
                val graceEnd = subscription.gracePeriodEndMillis ?: subscription.currentPeriodEndMillis
                if (currentTimeMillis > graceEnd) {
                    // Tolerância esgotada -> Inadimplente
                    subscription.copy(status = SubscriptionStatus.DELINQUENT)
                } else {
                    subscription
                }
            }
            SubscriptionStatus.CANCELLED, SubscriptionStatus.DELINQUENT -> subscription
        }
    }

    /**
     * Gera o banner informativo ou crítico com base na situação contratual da assinatura.
     *
     * @param subscription Assinatura do cliente.
     * @param currentTimeMillis Carimbo de data/hora atual.
     * @return [SubscriptionBannerAlert] ou null se a assinatura estiver ativa e sem pendências.
     */
    fun generateBannerAlert(
        subscription: UserSubscriptionState,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): SubscriptionBannerAlert? {
        val evaluated = evaluateSubscriptionState(subscription, currentTimeMillis)

        return when (evaluated.status) {
            SubscriptionStatus.ACTIVE -> null

            SubscriptionStatus.GRACE_PERIOD -> {
                val graceEnd = evaluated.gracePeriodEndMillis ?: evaluated.currentPeriodEndMillis
                val remainingDays = max(1, (((graceEnd - currentTimeMillis) / (24 * 60 * 60 * 1000L)).toInt()))
                SubscriptionBannerAlert(
                    title = "Falha na renovação da assinatura",
                    message = "Não conseguimos debitar seu plano. Seu acesso continuará liberado por mais $remainingDays dia(s). Atualize seu cartão para evitar bloqueio.",
                    actionLabel = "Atualizar Pagamento",
                    isCritical = false
                )
            }

            SubscriptionStatus.DELINQUENT -> {
                SubscriptionBannerAlert(
                    title = "Assinatura suspensa por inadimplência",
                    message = "O período de tolerância do plano ${evaluated.planName} terminou. Regularize sua fatura para reativar todos os benefícios.",
                    actionLabel = "Regularizar Agora",
                    isCritical = true
                )
            }

            SubscriptionStatus.CANCELLED -> {
                if (currentTimeMillis < evaluated.currentPeriodEndMillis) {
                    val remainingDays = max(1, (((evaluated.currentPeriodEndMillis - currentTimeMillis) / (24 * 60 * 60 * 1000L)).toInt()))
                    SubscriptionBannerAlert(
                        title = "Cancelamento programado",
                        message = "Sua assinatura não será renovada, mas você tem acesso garantido por mais $remainingDays dia(s).",
                        actionLabel = "Reativar Plano",
                        isCritical = false
                    )
                } else null
            }
        }
    }
}
