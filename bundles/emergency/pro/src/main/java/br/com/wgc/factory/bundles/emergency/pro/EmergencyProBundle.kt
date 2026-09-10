package br.com.wgc.factory.bundles.emergency.pro

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial emergency (pro).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object EmergencyProBundle {
    const val DOMAIN: String = "emergency"
    const val TIER: String = "pro"

    val features: List<String> = listOf(
        "emergency",
        "maps",
        "telemetry",
        "whatsapp-direct",
        "geofencing",
        "offline-maps"
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
