package br.com.wgc.factory.bundles.social.standard

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial social (standard).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object SocialStandardBundle {
    const val DOMAIN: String = "social"
    const val TIER: String = "standard"

    val features: List<String> = listOf(
        "profile",
        "message",
        "media-picker",
        "reviews-store"
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
