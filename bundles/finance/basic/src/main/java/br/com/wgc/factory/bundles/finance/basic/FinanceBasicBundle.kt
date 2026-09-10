package br.com.wgc.factory.bundles.finance.basic

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial finance (basic).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object FinanceBasicBundle {
    const val DOMAIN: String = "finance"
    const val TIER: String = "basic"

    val features: List<String> = listOf(
        "payment"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
