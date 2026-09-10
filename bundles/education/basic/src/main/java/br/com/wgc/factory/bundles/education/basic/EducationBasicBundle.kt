package br.com.wgc.factory.bundles.education.basic

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial education (basic).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object EducationBasicBundle {
    const val DOMAIN: String = "education"
    const val TIER: String = "basic"

    val features: List<String> = listOf(
        "scheduling",
        "profile"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
