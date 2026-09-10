package br.com.wgc.factory.bundles.subscriptions.pro

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial subscriptions (pro).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object SubscriptionsProBundle {
    const val DOMAIN: String = "subscriptions"
    const val TIER: String = "pro"

    val features: List<String> = listOf(
        "subscriptions",
        "payment",
        "promotions",
        "force-update",
        "analytics"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
