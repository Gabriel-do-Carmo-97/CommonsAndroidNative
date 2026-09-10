package br.com.wgc.factory.bundles.services.basic

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial services (basic).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object ServicesBasicBundle {
    const val DOMAIN: String = "services"
    const val TIER: String = "basic"

    val features: List<String> = listOf(
        "scheduling",
        "stores"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
