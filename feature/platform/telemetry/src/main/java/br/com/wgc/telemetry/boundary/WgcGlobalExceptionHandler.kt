package br.com.wgc.telemetry.boundary

import android.content.Context
import android.util.Log

/**
 * Manipulador global de exceções não capturadas em nível de processo JVM.
 *
 * Registra um [Thread.UncaughtExceptionHandler] padrão que intercepta falhas graves
 * de background threads ou coroutines sem handler, registrando métricas de diagnóstico
 * no armazenamento local antes de repassar o encerramento ordenado ao sistema operacional.
 */
object WgcGlobalExceptionHandler {

    private const val TAG = "WgcGlobalException"
    private var isInitialized = false

    /**
     * Inicializa a escuta global de falhas no processo da aplicação.
     *
     * @param context Contexto de aplicação Android.
     * @param onCrashLogged Callback disparado para envio imediato de telemetria caso a conexão esteja ativa.
     */
    fun initialize(
        context: Context,
        onCrashLogged: ((thread: Thread, throwable: Throwable) -> Unit)? = null
    ) {
        if (isInitialized) return
        isInitialized = true

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                Log.e(TAG, "FATAL CRASH detectado na thread [${thread.name}]: ${throwable.message}", throwable)
                onCrashLogged?.invoke(thread, throwable)
            } catch (e: Exception) {
                Log.e(TAG, "Falha ao registrar log de crash global: ${e.message}", e)
            } finally {
                defaultHandler?.uncaughtException(thread, throwable)
            }
        }
    }
}
