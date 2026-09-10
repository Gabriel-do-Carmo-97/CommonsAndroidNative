package br.com.wgc.factory.bundles.foundation.basic

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial foundation (basic).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object FoundationBasicBundle {
    const val DOMAIN: String = "foundation"
    const val TIER: String = "basic"

    val features: List<String> = listOf(
        "settings",
        "force-update"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
