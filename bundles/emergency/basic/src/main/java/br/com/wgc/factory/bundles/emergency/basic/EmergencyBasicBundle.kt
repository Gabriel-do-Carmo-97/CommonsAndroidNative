package br.com.wgc.factory.bundles.emergency.basic

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial emergency (basic).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object EmergencyBasicBundle {
    const val DOMAIN: String = "emergency"
    const val TIER: String = "basic"

    val features: List<String> = listOf(
        "emergency",
        "maps"
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
