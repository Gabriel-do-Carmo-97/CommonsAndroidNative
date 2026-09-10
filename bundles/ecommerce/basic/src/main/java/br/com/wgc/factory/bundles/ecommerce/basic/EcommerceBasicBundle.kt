package br.com.wgc.factory.bundles.ecommerce.basic

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial ecommerce (basic).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object EcommerceBasicBundle {
    const val DOMAIN: String = "ecommerce"
    const val TIER: String = "basic"

    val features: List<String> = listOf(
        "catalog",
        "cart",
        "payment"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
