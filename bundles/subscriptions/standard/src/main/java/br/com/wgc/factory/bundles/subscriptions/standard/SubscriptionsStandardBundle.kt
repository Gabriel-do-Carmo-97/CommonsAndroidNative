package br.com.wgc.factory.bundles.subscriptions.standard

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial subscriptions (standard).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object SubscriptionsStandardBundle {
    const val DOMAIN: String = "subscriptions"
    const val TIER: String = "standard"

    val features: List<String> = listOf(
        "subscriptions",
        "payment",
        "promotions"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
