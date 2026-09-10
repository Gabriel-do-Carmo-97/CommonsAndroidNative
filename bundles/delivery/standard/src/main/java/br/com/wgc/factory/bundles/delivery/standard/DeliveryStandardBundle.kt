package br.com.wgc.factory.bundles.delivery.standard

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial delivery (standard).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object DeliveryStandardBundle {
    const val DOMAIN: String = "delivery"
    const val TIER: String = "standard"

    val features: List<String> = listOf(
        "order-tracking",
        "maps",
        "driver-app",
        "telemetry"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
