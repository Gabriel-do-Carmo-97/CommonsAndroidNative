package br.com.wgc.factory.bundles.delivery.pro

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial delivery (pro).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object DeliveryProBundle {
    const val DOMAIN: String = "delivery"
    const val TIER: String = "pro"

    val features: List<String> = listOf(
        "order-tracking",
        "maps",
        "driver-app",
        "telemetry",
        "dispatch",
        "geofencing",
        "offline-maps"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
