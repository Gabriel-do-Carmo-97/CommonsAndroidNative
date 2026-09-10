package br.com.wgc.factory.bundles.foundation.pro

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial foundation (pro).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object FoundationProBundle {
    const val DOMAIN: String = "foundation"
    const val TIER: String = "pro"

    val features: List<String> = listOf(
        "settings",
        "force-update",
        "analytics",
        "multi-language",
        "offline-sync"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
