package br.com.wgc.factory.bundles.communication.standard

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial communication (standard).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object CommunicationStandardBundle {
    const val DOMAIN: String = "communication"
    const val TIER: String = "standard"

    val features: List<String> = listOf(
        "message",
        "whatsapp-direct",
        "media-picker",
        "feedback"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
