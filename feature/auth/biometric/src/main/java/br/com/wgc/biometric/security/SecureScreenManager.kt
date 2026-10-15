package br.com.wgc.biometric.security

import android.app.Activity
import android.view.Window
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gerenciador corporativo de Proteção Anti-Captura de Tela (`FLAG_SECURE`).
 *
 * Bloqueia capturas de tela (screenshots), espelhamento não autorizado e gravação de vídeo
 * em telas confidenciais que exibam dados de cartão de crédito, senhas, biometria ou saldos bancários,
 * além de ofuscar o preview do aplicativo na tela de alternância de tarefas recentes (App Switcher).
 *
 * ### 📌 Quando Usar:
 * Adicione na raiz de telas sensíveis (como Checkout de Pagamento, Biometria ou Redefinição de Senha)
 * invocando [SecureScreenEffect] no escopo do Jetpack Compose.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun TelaDePagamentoSegura() {
 *     // Ativa FLAG_SECURE durante o ciclo de vida desta tela e remove ao sair:
 *     SecureScreenEffect()
 *
 *     // Conteúdo protegido contra prints:
 *     CreditCardForm()
 * }
 * ```
 */
@Singleton
class SecureScreenManager @Inject constructor() {

    /**
     * Aplica a flag [WindowManager.LayoutParams.FLAG_SECURE] à janela da Activity informada.
     *
     * @param window Janela do Android a ser blindada.
     */
    fun enableSecureMode(window: Window) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    /**
     * Remove a flag [WindowManager.LayoutParams.FLAG_SECURE], restaurando o comportamento normal de prints.
     *
     * @param window Janela do Android a ter a blindagem desativada.
     */
    fun disableSecureMode(window: Window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    /**
     * Verifica se o modo seguro está atualmente ativo nos atributos da janela.
     *
     * @param window Janela a ser auditada.
     * @return true se `FLAG_SECURE` estiver ativo.
     */
    fun isSecureModeActive(window: Window): Boolean {
        val flags = window.attributes.flags
        return (flags and WindowManager.LayoutParams.FLAG_SECURE) != 0
    }
}

/**
 * Efeito de ciclo de vida Compose que ativa a proteção de tela segura ao entrar na composição
 * e a restaura automaticamente ao sair (Disposed).
 *
 * ### 📌 Quando Usar:
 * Invoque no topo de qualquer Composable transacional (:feature:checkout:payment, :feature:auth:biometric).
 *
 * @param isEnabled Permite habilitar ou desabilitar condicionalmente a proteção (padrão: true).
 */
@Composable
fun SecureScreenEffect(isEnabled: Boolean = true) {
    val context = LocalContext.current
    DisposableEffect(isEnabled) {
        val activity = context as? Activity
        val window = activity?.window

        if (isEnabled && window != null) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }

        onDispose {
            if (window != null) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            }
        }
    }
}
