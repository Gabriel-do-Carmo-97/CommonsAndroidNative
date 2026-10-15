package br.com.wgc.driver_app.queue

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

/**
 * Estado operacional da proposta de despacho enviada ao entregador.
 */
enum class OfferStatus {
    /** Aguardando decisão do motorista dentro do prazo do contador regressivo. */
    PENDING,
    /** Proposta aceita pelo condutor; corrida alocada com sucesso. */
    ACCEPTED,
    /** Proposta recusada explicitamente pelo condutor. */
    REJECTED,
    /** Tempo do contador regressivo esgotado sem resposta (recusa tácita). */
    EXPIRED
}

/**
 * Proposta de corrida ou entrega encaminhada à fila do aplicativo do motorista parceiro.
 *
 * @property id Identificador exclusivo do despacho/oferta.
 * @property orderId Código do pedido de e-commerce ou refeição.
 * @property pickupAddress Endereço do estabelecimento comercial para retirada dos produtos.
 * @property deliveryAddress Endereço residencial do cliente destinatário.
 * @property distanceKm Distância viária estimada entre coleta e entrega em km.
 * @property estimatedEarnings Valor financeiro a ser recebido pelo condutor (ex: "R$ 18,50").
 * @property timeoutSeconds Duração total da janela de decisão em segundos (padrão: 30 segundos).
 * @property createdAtMillis Carimbo de criação da oferta em milissegundos.
 * @property status Estado de resolução da oferta ([OfferStatus]).
 */
data class DispatchOffer(
    val id: String,
    val orderId: String,
    val pickupAddress: String,
    val deliveryAddress: String,
    val distanceKm: Double,
    val estimatedEarnings: String,
    val timeoutSeconds: Int = 30,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val status: OfferStatus = OfferStatus.PENDING
) {
    /**
     * Calcula o carimbo de data/hora em que a oferta expirará.
     */
    val expiresAtMillis: Long get() = createdAtMillis + (timeoutSeconds * 1000L)
}

/**
 * Gerenciador corporativo da Fila de Despacho e Aceite de Corridas com Temporizador Regressivo.
 *
 * Controla as ofertas de entregas enviadas ao entregador parceiro em tempo real, fornecendo
 * cálculo de contagem regressiva (timeout de 30s), aceites instantâneos e expiração automática.
 *
 * ### 📌 Quando Usar:
 * Utilize na tela inicial do condutor ([br.com.wgc.driver_app.screen.DriverAppScreen]) para renderizar
 * o card modal com som de alerta, contagem regressiva animada e botões "Aceitar" / "Recusar".
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val queueManager = DriverOrderQueueManager()
 *
 * // 1. Receber despacho do OmniBackend:
 * queueManager.enqueueOffer(
 *     DispatchOffer(
 *         id = "offer_10",
 *         orderId = "#WGC-882",
 *         pickupAddress = "Rua Augusta, 400",
 *         deliveryAddress = "Av. Paulista, 1000",
 *         distanceKm = 2.4,
 *         estimatedEarnings = "R$ 16,00"
 *     )
 * )
 *
 * // 2. Aceitar entrega:
 * queueManager.acceptOffer("offer_10")
 * ```
 */
@Singleton
class DriverOrderQueueManager @Inject constructor() {

    private val _offers = MutableStateFlow<List<DispatchOffer>>(emptyList())

    /**
     * Fluxo reativo da lista de propostas ativas e históricas do motorista.
     */
    val offers: StateFlow<List<DispatchOffer>> = _offers.asStateFlow()

    /**
     * Adiciona uma nova proposta de entrega à fila do motorista.
     *
     * @param offer Dados da oferta recebida da central de despacho.
     */
    fun enqueueOffer(offer: DispatchOffer) {
        _offers.update { current ->
            val updated = current.toMutableList()
            updated.removeAll { it.id == offer.id }
            updated.add(0, offer)
            updated
        }
    }

    /**
     * Registra o aceite da corrida pelo motorista.
     *
     * @param offerId Identificador da oferta.
     * @param currentTimeMillis Carimbo de tempo do aceite para validação de prazo.
     * @return true se o aceite foi efetivado com sucesso dentro do prazo; false se expirada/inexistente.
     */
    fun acceptOffer(offerId: String, currentTimeMillis: Long = System.currentTimeMillis()): Boolean {
        val target = _offers.value.find { it.id == offerId } ?: return false
        if (target.status != OfferStatus.PENDING) return false

        if (currentTimeMillis > target.expiresAtMillis) {
            expireOffer(offerId)
            return false
        }

        _offers.update { list ->
            list.map { if (it.id == offerId) it.copy(status = OfferStatus.ACCEPTED) else it }
        }
        return true
    }

    /**
     * Registra a recusa expressa da corrida pelo motorista.
     *
     * @param offerId Identificador da oferta recusada.
     * @return true se a recusa foi confirmada.
     */
    fun rejectOffer(offerId: String): Boolean {
        val target = _offers.value.find { it.id == offerId } ?: return false
        if (target.status != OfferStatus.PENDING) return false

        _offers.update { list ->
            list.map { if (it.id == offerId) it.copy(status = OfferStatus.REJECTED) else it }
        }
        return true
    }

    /**
     * Calcula os segundos restantes para expiração de uma oferta específica.
     *
     * @param offerId Identificador da oferta.
     * @param currentTimeMillis Carimbo atual de data/hora.
     * @return Quantidade de segundos restantes (mínimo 0).
     */
    fun getRemainingSeconds(offerId: String, currentTimeMillis: Long = System.currentTimeMillis()): Int {
        val offer = _offers.value.find { it.id == offerId } ?: return 0
        if (offer.status != OfferStatus.PENDING) return 0

        val remainingMillis = offer.expiresAtMillis - currentTimeMillis
        return max(0, (remainingMillis / 1000L).toInt())
    }

    /**
     * Varre as ofertas ativas e marca como [OfferStatus.EXPIRED] aquelas cujo tempo limite se esgotou.
     *
     * @param currentTimeMillis Carimbo de data/hora da verificação.
     * @return Lista de ofertas que acabaram de expirar nesta verificação.
     */
    fun checkAndExpireOffers(currentTimeMillis: Long = System.currentTimeMillis()): List<DispatchOffer> {
        val newlyExpired = mutableListOf<DispatchOffer>()

        _offers.update { list ->
            list.map { offer ->
                if (offer.status == OfferStatus.PENDING && currentTimeMillis >= offer.expiresAtMillis) {
                    val expired = offer.copy(status = OfferStatus.EXPIRED)
                    newlyExpired.add(expired)
                    expired
                } else {
                    offer
                }
            }
        }

        return newlyExpired
    }

    /**
     * Retorna a lista de ofertas atualmente pendentes de resposta do condutor.
     *
     * @return Lista de [DispatchOffer] em estado [OfferStatus.PENDING].
     */
    fun getPendingOffers(): List<DispatchOffer> {
        return _offers.value.filter { it.status == OfferStatus.PENDING }
    }

    private fun expireOffer(offerId: String) {
        _offers.update { list ->
            list.map { if (it.id == offerId) it.copy(status = OfferStatus.EXPIRED) else it }
        }
    }
}
