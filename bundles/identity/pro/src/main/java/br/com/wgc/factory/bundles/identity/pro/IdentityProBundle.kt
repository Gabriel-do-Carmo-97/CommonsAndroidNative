package br.com.wgc.factory.bundles.identity.pro

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial identity (pro).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object IdentityProBundle {
    const val DOMAIN: String = "identity"
    const val TIER: String = "pro"

    val features: List<String> = listOf(
        "authentication",
        "biometric",
        "profile",
        "onboarding",
        "multi-language"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
