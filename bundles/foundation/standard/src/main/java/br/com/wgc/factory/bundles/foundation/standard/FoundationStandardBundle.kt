package br.com.wgc.factory.bundles.foundation.standard

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial foundation (standard).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object FoundationStandardBundle {
    const val DOMAIN: String = "foundation"
    const val TIER: String = "standard"

    val features: List<String> = listOf(
        "settings",
        "force-update",
        "analytics"
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
