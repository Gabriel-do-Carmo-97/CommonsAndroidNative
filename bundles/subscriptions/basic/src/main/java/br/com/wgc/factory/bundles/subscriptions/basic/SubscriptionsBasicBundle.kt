package br.com.wgc.factory.bundles.subscriptions.basic

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial subscriptions (basic).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object SubscriptionsBasicBundle {
    const val DOMAIN: String = "subscriptions"
    const val TIER: String = "basic"

    val features: List<String> = listOf(
        "subscriptions",
        "payment"
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
