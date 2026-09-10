package br.com.wgc.factory.bundles.identity.basic

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial identity (basic).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object IdentityBasicBundle {
    const val DOMAIN: String = "identity"
    const val TIER: String = "basic"

    val features: List<String> = listOf(
        "authentication",
        "profile"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
