package br.com.wgc.factory.bundles.education.standard

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial education (standard).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object EducationStandardBundle {
    const val DOMAIN: String = "education"
    const val TIER: String = "standard"

    val features: List<String> = listOf(
        "scheduling",
        "profile",
        "message",
        "media-picker"
    )

    /**
     * Inicializa servicos e configuracoes necessarias para o bundle.
     */
    fun initialize(context: Context? = null) {
        // Inicializacao contextual do bundle comercial
    }
}
