package br.com.wgc.factory.bundles.health.pro

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial health (pro).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object HealthProBundle {
    const val DOMAIN: String = "health"
    const val TIER: String = "pro"

    val features: List<String> = listOf(
        "scheduling",
        "message",
        "biometric",
        "media-picker",
        "quotation",
        "emergency"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
