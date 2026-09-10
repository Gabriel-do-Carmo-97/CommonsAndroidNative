package br.com.wgc.factory.bundles.events.pro

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial events (pro).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object EventsProBundle {
    const val DOMAIN: String = "events"
    const val TIER: String = "pro"

    val features: List<String> = listOf(
        "catalog",
        "payment",
        "scheduling",
        "maps",
        "media-picker",
        "promotions"
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
