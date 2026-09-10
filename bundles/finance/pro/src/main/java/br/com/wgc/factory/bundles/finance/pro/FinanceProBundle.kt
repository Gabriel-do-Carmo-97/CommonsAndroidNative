package br.com.wgc.factory.bundles.finance.pro

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial finance (pro).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object FinanceProBundle {
    const val DOMAIN: String = "finance"
    const val TIER: String = "pro"

    val features: List<String> = listOf(
        "payment",
        "biometric",
        "settings",
        "subscriptions",
        "analytics",
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
