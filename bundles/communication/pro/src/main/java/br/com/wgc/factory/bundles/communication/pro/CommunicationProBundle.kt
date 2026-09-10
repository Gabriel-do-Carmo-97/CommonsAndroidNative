package br.com.wgc.factory.bundles.communication.pro

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial communication (pro).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object CommunicationProBundle {
    const val DOMAIN: String = "communication"
    const val TIER: String = "pro"

    val features: List<String> = listOf(
        "message",
        "whatsapp-direct",
        "media-picker",
        "feedback",
        "ai-assistant"
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
