package br.com.wgc.factory.bundles.services.standard

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial services (standard).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object ServicesStandardBundle {
    const val DOMAIN: String = "services"
    const val TIER: String = "standard"

    val features: List<String> = listOf(
        "scheduling",
        "stores",
        "quotation",
        "feedback"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
