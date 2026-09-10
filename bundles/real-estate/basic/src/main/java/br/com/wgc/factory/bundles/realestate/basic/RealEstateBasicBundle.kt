package br.com.wgc.factory.bundles.realestate.basic

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial real-estate (basic).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object RealEstateBasicBundle {
    const val DOMAIN: String = "real-estate"
    const val TIER: String = "basic"

    val features: List<String> = listOf(
        "catalog",
        "search"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
