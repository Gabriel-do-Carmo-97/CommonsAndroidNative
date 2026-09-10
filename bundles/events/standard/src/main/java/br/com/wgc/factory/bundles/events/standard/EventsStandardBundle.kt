package br.com.wgc.factory.bundles.events.standard

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial events (standard).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object EventsStandardBundle {
    const val DOMAIN: String = "events"
    const val TIER: String = "standard"

    val features: List<String> = listOf(
        "catalog",
        "payment",
        "scheduling",
        "maps"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
