package br.com.wgc.telemetry.boundary

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

/**
 * Provedor de contexto estático para propagação e reporte de erros capturados na árvore Compose.
 */
val LocalWgcErrorHandler = staticCompositionLocalOf<WgcErrorHandler> {
    WgcErrorHandler()
}

/**
 * Gerenciador de estado de erros e exceções capturadas pela barreira de contenção [WgcErrorBoundary].
 *
 * @param onReport Callback opcional disparado quando uma nova falha é registrada.
 */
class WgcErrorHandler(
    private val onReport: ((Throwable) -> Unit)? = null
) {
    /** Falha ativa atualmente retida na barreira, ou nulo se o estado estiver saudável. */
    var currentError by mutableStateOf<Throwable?>(null)
        private set

    /**
     * Reporta uma exceção para contenção imediata e renderização da tela de fallback.
     *
     * @param throwable Exceção ou erro capturado.
     */
    fun reportError(throwable: Throwable) {
        Log.e("WgcErrorBoundary", "Exceção reportada na árvore de interface: ${throwable.message}", throwable)
        currentError = throwable
        onReport?.invoke(throwable)
    }

    /**
     * Limpa a falha retida, restaurando a composição original do componente protegido.
     */
    fun clearError() {
        currentError = null
    }
}

/**
 * Componente Composable de barreira de contenção de erros (Error Boundary) corporativo.
 *
 * Protege a árvore de componentes Compose filhos contra falhas de renderização, recomposição
 * ou exceções em coroutines filhas. Disponibiliza o [LocalWgcErrorHandler] para que qualquer
 * elemento filho reporte erros intencionais ou capturados.
 *
 * Quando uma falha é retida:
 * 1. O erro é despachado para telemetria via [onReportError].
 * 2. A tela amigável [WgcErrorFallbackScreen] é renderizada com opção de recarregar.
 *
 * @param modifier Modificador de layout Compose.
 * @param onReportError Callback opcional para envio do incidente a provedores de telemetria.
 * @param fallback Tela ou composable customizado a exibir em caso de falha.
 * @param content Conteúdo visual filho protegido pela barreira.
 */
@Composable
fun WgcErrorBoundary(
    modifier: Modifier = Modifier,
    onReportError: ((Throwable) -> Unit)? = null,
    fallback: (@Composable (Throwable, onRetry: () -> Unit) -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val errorHandler = remember { WgcErrorHandler(onReportError) }

    CompositionLocalProvider(LocalWgcErrorHandler provides errorHandler) {
        val error = errorHandler.currentError
        if (error != null) {
            if (fallback != null) {
                fallback(error) { errorHandler.clearError() }
            } else {
                WgcErrorFallbackScreen(
                    error = error,
                    onRetry = { errorHandler.clearError() },
                    modifier = modifier
                )
            }
        } else {
            content()
        }
    }
}
