package br.com.wgc.factory.bundles.identity.standard

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial identity (standard).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object IdentityStandardBundle {
    const val DOMAIN: String = "identity"
    const val TIER: String = "standard"

    val features: List<String> = listOf(
        "authentication",
        "biometric",
        "profile"
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
