package br.com.wgc.factory.bundles.social.basic

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial social (basic).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object SocialBasicBundle {
    const val DOMAIN: String = "social"
    const val TIER: String = "basic"

    val features: List<String> = listOf(
        "profile",
        "message"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
