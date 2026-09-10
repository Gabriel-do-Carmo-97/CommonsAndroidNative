package br.com.wgc.factory.bundles.health.standard

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial health (standard).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object HealthStandardBundle {
    const val DOMAIN: String = "health"
    const val TIER: String = "standard"

    val features: List<String> = listOf(
        "scheduling",
        "message",
        "biometric",
        "media-picker"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
