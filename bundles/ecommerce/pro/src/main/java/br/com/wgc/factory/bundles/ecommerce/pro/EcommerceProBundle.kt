package br.com.wgc.factory.bundles.ecommerce.pro

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial ecommerce (pro).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object EcommerceProBundle {
    const val DOMAIN: String = "ecommerce"
    const val TIER: String = "pro"

    val features: List<String> = listOf(
        "catalog",
        "cart",
        "payment",
        "search",
        "promotions",
        "reviews-store",
        "loyalty",
        "stores",
        "ai-assistant"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle comercial.
     *
     * @param context Contexto de execucao do aplicativo para inicializacao de dependencias.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
