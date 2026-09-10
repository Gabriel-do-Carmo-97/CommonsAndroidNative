package br.com.wgc.factory.bundles.finance.standard

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial finance (standard).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object FinanceStandardBundle {
    const val DOMAIN: String = "finance"
    const val TIER: String = "standard"

    val features: List<String> = listOf(
        "payment",
        "biometric",
        "settings"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
