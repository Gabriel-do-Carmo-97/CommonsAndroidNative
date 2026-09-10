package br.com.wgc.factory.bundles.social.pro

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial social (pro).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object SocialProBundle {
    const val DOMAIN: String = "social"
    const val TIER: String = "pro"

    val features: List<String> = listOf(
        "profile",
        "message",
        "media-picker",
        "reviews-store",
        "feedback",
        "ai-assistant"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
