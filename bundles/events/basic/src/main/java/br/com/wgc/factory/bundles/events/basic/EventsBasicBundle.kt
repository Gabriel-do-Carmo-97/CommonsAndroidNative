package br.com.wgc.factory.bundles.events.basic

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial events (basic).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object EventsBasicBundle {
    const val DOMAIN: String = "events"
    const val TIER: String = "basic"

    val features: List<String> = listOf(
        "catalog",
        "payment"
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
