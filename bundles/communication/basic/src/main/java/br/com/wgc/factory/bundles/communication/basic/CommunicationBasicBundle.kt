package br.com.wgc.factory.bundles.communication.basic

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial communication (basic).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object CommunicationBasicBundle {
    const val DOMAIN: String = "communication"
    const val TIER: String = "basic"

    val features: List<String> = listOf(
        "message",
        "whatsapp-direct"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
