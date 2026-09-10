package br.com.wgc.factory.bundles.health.basic

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial health (basic).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object HealthBasicBundle {
    const val DOMAIN: String = "health"
    const val TIER: String = "basic"

    val features: List<String> = listOf(
        "scheduling",
        "message"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
