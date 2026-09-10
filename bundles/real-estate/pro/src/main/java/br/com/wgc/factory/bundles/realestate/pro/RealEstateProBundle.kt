package br.com.wgc.factory.bundles.realestate.pro

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial real-estate (pro).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object RealEstateProBundle {
    const val DOMAIN: String = "real-estate"
    const val TIER: String = "pro"

    val features: List<String> = listOf(
        "catalog",
        "search",
        "maps",
        "scheduling",
        "media-picker",
        "quotation"
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
