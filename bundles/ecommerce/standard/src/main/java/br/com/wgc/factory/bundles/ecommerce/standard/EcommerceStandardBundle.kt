package br.com.wgc.factory.bundles.ecommerce.standard

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial ecommerce (standard).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object EcommerceStandardBundle {
    const val DOMAIN: String = "ecommerce"
    const val TIER: String = "standard"

    val features: List<String> = listOf(
        "catalog",
        "cart",
        "payment",
        "search",
        "promotions",
        "reviews-store"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
