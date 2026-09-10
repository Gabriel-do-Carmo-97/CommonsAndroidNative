package br.com.wgc.factory.bundles.delivery.basic

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial delivery (basic).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object DeliveryBasicBundle {
    const val DOMAIN: String = "delivery"
    const val TIER: String = "basic"

    val features: List<String> = listOf(
        "order-tracking",
        "maps"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
