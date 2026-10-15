package br.com.wgc.force_update.enforcer

import android.net.Uri
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Classificação do status de conformidade da versão instalada do aplicativo.
 */
enum class AppVersionStatus {
    /** A versão instalada é a mais recente disponível. */
    UP_TO_DATE,
    /** Uma versão mais recente está disponível, mas a atualização é opcional. */
    OPTIONAL_UPDATE_AVAILABLE,
    /** A versão instalada está obsoleta ou insegura; atualização obrigatória para prosseguir. */
    FORCE_UPDATE_REQUIRED,
    /** O ecossistema está em manutenção programada temporária no OmniBackend. */
    MAINTENANCE_MODE
}

/**
 * Políticas de versão e manutenção recebidas dinamicamente do Remote Config.
 *
 * @property currentVersionCode Código numérico da versão do APK/AAB atualmente em execução.
 * @property minRequiredVersionCode Código mínimo obrigatório da versão aceita pelos microsserviços.
 * @property latestAvailableVersionCode Código da versão mais recente publicada na Google Play Store.
 * @property isMaintenanceActive Flag que indica se a janela de manutenção programada está aberta.
 * @property maintenanceMessage Mensagem personalizada explicativa para o usuário durante a manutenção.
 * @property storePackageName Nome do pacote do app para redirecionamento à Play Store.
 */
data class VersionPolicy(
    val currentVersionCode: Long,
    val minRequiredVersionCode: Long,
    val latestAvailableVersionCode: Long,
    val isMaintenanceActive: Boolean = false,
    val maintenanceMessage: String? = null,
    val storePackageName: String = "br.com.wgc.commonsandroidnative"
)

/**
 * Resultado da avaliação de conformidade da versão instalada.
 *
 * @property status Status categorizado de atualização ([AppVersionStatus]).
 * @property isBlocked Indica se o fluxo de navegação do usuário deve ser bloqueado na tela de aviso.
 * @property storeIntentUri URI pronta para disparo de Intent direcionando o usuário à loja de aplicativos.
 * @property alertMessage Mensagem amigável pronta para ser renderizada na interface.
 */
data class VersionEnforcementResult(
    val status: AppVersionStatus,
    val isBlocked: Boolean,
    val storeIntentUri: Uri,
    val alertMessage: String
)

/**
 * Motor corporativo de validação e imposição remota de versões mínimas e janelas de manutenção.
 *
 * Protege a integridade do app e a retrocompatibilidade da API do OmniBackend, impedindo
 * o uso de versões descontinuadas que poderiam conter vulnerabilidades ou contratos depreciados.
 *
 * ### 📌 Quando Usar:
 * - Na inicialização do aplicativo (`MainActivity` ou Splash Screen) antes de carregar o catálogo ou autenticação.
 * - Ao receber notificações de invalidação de versão ou manutenção urgente via Push / Remote Config.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val enforcer = RemoteVersionEnforcer()
 * val policy = VersionPolicy(
 *     currentVersionCode = 10,
 *     minRequiredVersionCode = 12,
 *     latestAvailableVersionCode = 15,
 *     isMaintenanceActive = false
 * )
 * val result = enforcer.evaluate(policy)
 * if (result.isBlocked) {
 *     navController.navigate("force_update_screen")
 * }
 * ```
 */
@Singleton
class RemoteVersionEnforcer @Inject constructor() {

    /**
     * Avalia a política de versão atual e determina a ação de bloqueio ou notificação necessária.
     *
     * @param policy Diretrizes de versão e flags de manutenção recebidas do servidor.
     * @return [VersionEnforcementResult] com a resolução da conformidade e link da loja.
     */
    fun evaluate(policy: VersionPolicy): VersionEnforcementResult {
        val playStoreUri = resolveStoreUri(policy.storePackageName)

        if (policy.isMaintenanceActive) {
            val message = policy.maintenanceMessage?.takeIf { it.isNotBlank() }
                ?: "Estamos em manutenção programada para melhorias. Voltaremos em breve!"
            return VersionEnforcementResult(
                status = AppVersionStatus.MAINTENANCE_MODE,
                isBlocked = true,
                storeIntentUri = playStoreUri,
                alertMessage = message
            )
        }

        if (policy.currentVersionCode < policy.minRequiredVersionCode) {
            return VersionEnforcementResult(
                status = AppVersionStatus.FORCE_UPDATE_REQUIRED,
                isBlocked = true,
                storeIntentUri = playStoreUri,
                alertMessage = "Esta versão do aplicativo foi descontinuada. Por favor, atualize agora para continuar aproveitando nossos serviços."
            )
        }

        if (policy.currentVersionCode < policy.latestAvailableVersionCode) {
            return VersionEnforcementResult(
                status = AppVersionStatus.OPTIONAL_UPDATE_AVAILABLE,
                isBlocked = false,
                storeIntentUri = playStoreUri,
                alertMessage = "Uma nova versão com melhorias e novidades está disponível na Play Store."
            )
        }

        return VersionEnforcementResult(
            status = AppVersionStatus.UP_TO_DATE,
            isBlocked = false,
            storeIntentUri = playStoreUri,
            alertMessage = "Seu aplicativo está totalmente atualizado."
        )
    }

    /**
     * Gera a URI oficial de redirecionamento para a Google Play Store.
     *
     * @param packageName Nome do pacote da aplicação.
     * @return [Uri] formatada com esquema HTTPS da loja Google Play.
     */
    fun resolveStoreUri(packageName: String): Uri {
        return Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
    }
}
