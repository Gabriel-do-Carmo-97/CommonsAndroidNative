package br.com.wgc.factory.bundles.education.pro

import android.content.Context

/**
 * Ponto de entrada e inicializacao do bundle comercial education (pro).
 * Agrega e expoe as features necessarias para esta solucao.
 */
object EducationProBundle {
    const val DOMAIN: String = "education"
    const val TIER: String = "pro"

    val features: List<String> = listOf(
        "scheduling",
        "profile",
        "message",
        "media-picker",
        "settings",
        "feedback",
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
