package br.com.wgc.factory.bundles.emergency.standard

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial emergency (standard).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object EmergencyStandardBundle {
    const val DOMAIN: String = "emergency"
    const val TIER: String = "standard"

    val features: List<String> = listOf(
        "emergency",
        "maps",
        "telemetry",
        "whatsapp-direct"
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
