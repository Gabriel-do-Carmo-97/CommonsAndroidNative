package br.com.wgc.testing.extensions

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Utilitários de extensão para asserções fluentes de fluxos reativos com Turbine.
 */

/**
 * Coleta e valida o próximo item emitido por um fluxo assíncrono com tempo limite seguro.
 *
 * @param T Tipo do dado emitido pelo fluxo.
 * @param timeout Tempo limite máximo de espera da emissão (padrão: 3 segundos).
 * @param validate Bloco lambda de validação contendo as asserções sobre o item recebido.
 */
suspend fun <T> Flow<T>.testSingle(
    timeout: Duration = 3.seconds,
    validate: suspend ReceiveTurbine<T>.(T) -> Unit
) {
    this.test(timeout = timeout) {
        val item = awaitItem()
        validate(item)
        cancelAndIgnoreRemainingEvents()
    }
}
