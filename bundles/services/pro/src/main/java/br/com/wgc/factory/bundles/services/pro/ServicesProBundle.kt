package br.com.wgc.factory.bundles.services.pro

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial services (pro).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object ServicesProBundle {
    const val DOMAIN: String = "services"
    const val TIER: String = "pro"

    val features: List<String> = listOf(
        "scheduling",
        "stores",
        "quotation",
        "feedback",
        "reviews-store",
        "payment"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
