package br.com.wgc.telemetry.leak

import java.lang.ref.WeakReference
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Informações diagnósticas de uma referência monitorada para detecção de retenção de memória.
 *
 * @property key Identificador único da observação.
 * @property className Nome canônico da classe do objeto monitorado.
 * @property tag Rótulo contextual (ex: "MainActivity", "CatalogFragment").
 * @property watchedAtMillis Timestamp epoch de quando o objeto entrou em observação.
 */
data class TrackedReference(
    val key: String,
    val className: String,
    val tag: String,
    val watchedAtMillis: Long
)

/**
 * Parecer de diagnóstico de vazamento de memória.
 *
 * @property retainedCount Quantidade de instâncias que ainda não foram coletadas pelo Garbage Collector.
 * @property retainedReferences Lista de referências suspeitas retidas em memória.
 */
data class MemoryLeakReport(
    val retainedCount: Int,
    val retainedReferences: List<TrackedReference>
)

/**
 * Detector corporativo de vazamento de memória e retenção indesejada de Activities, Fragments e ViewModels.
 *
 * Utiliza [WeakReference] para rastrear o ciclo de vida de componentes Android após sua destruição.
 * Caso um objeto continue retido após invocação do GC ou tempo limite, é classificado como suspeito de leak.
 *
 * ### 📌 Quando Usar:
 * - Em ambientes de teste e debug (`Activity.onDestroy` / `Fragment.onDestroyView`).
 * - Em esteiras de profiling contínuo com LeakCanary para prevenção de OutOfMemoryError (OOM).
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val detector = MemoryLeakDetector()
 * detector.watch(activity, tag = "MainActivity")
 *
 * // Após algum tempo ou execução de testes:
 * val report = detector.generateReport()
 * if (report.retainedCount > 0) {
 *     Log.w("MemoryLeak", "Objetos retidos: ${report.retainedCount}")
 * }
 * ```
 */
@Singleton
class MemoryLeakDetector @Inject constructor() {

    private val trackedObjects = ConcurrentHashMap<String, Pair<WeakReference<Any>, TrackedReference>>()

    /**
     * Registra um objeto para monitoramento de descarte e coleta de lixo.
     *
     * @param target Objeto que deveria ser desalocado da memória (ex: Activity finalizada).
     * @param tag Descrição contextual ou identificador do fluxo de tela.
     * @return Chave única de rastreamento gerada.
     */
    fun watch(target: Any, tag: String = ""): String {
        val key = UUID.randomUUID().toString()
        val info = TrackedReference(
            key = key,
            className = target.javaClass.name,
            tag = tag.ifBlank { target.javaClass.simpleName },
            watchedAtMillis = System.currentTimeMillis()
        )
        trackedObjects[key] = Pair(WeakReference(target), info)
        return key
    }

    /**
     * Remove referências que já foram limpas pelo Garbage Collector.
     */
    fun purgeCollected() {
        val iterator = trackedObjects.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.value.first.get() == null) {
                iterator.remove()
            }
        }
    }

    /**
     * Gera relatório detalhado contendo todos os objetos que continuam retidos em memória.
     *
     * @return [MemoryLeakReport] com a contagem e lista de referências ativas.
     */
    fun generateReport(): MemoryLeakReport {
        purgeCollected()
        val retained = trackedObjects.values.map { it.second }
        return MemoryLeakReport(
            retainedCount = retained.size,
            retainedReferences = retained
        )
    }

    /**
     * Limpa todo o histórico de rastreamento em memória.
     */
    fun clear() {
        trackedObjects.clear()
    }
}
