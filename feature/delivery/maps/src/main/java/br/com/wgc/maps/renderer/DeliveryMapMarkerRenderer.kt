package br.com.wgc.maps.renderer

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/**
 * Categorias operacionais de marcadores vetoriais renderizados no mapa de logística e delivery.
 */
enum class DeliveryMarkerType {
    /** Estabelecimento comercial ou ponto de coleta. */
    STORE,
    /** Destinatário final ou endereço residencial da entrega. */
    CUSTOMER,
    /** Entregador ou veículo parceiro em deslocamento ativo. */
    DRIVER
}

/**
 * Modelo de dados imutável representando um marcador vetorial configurado no mapa.
 *
 * @property id Identificador único do marcador.
 * @property title Título exibido no balão de informações do mapa.
 * @property snippet Texto complementar descritivo ou estimativa de chegada.
 * @property latitude Coordenada de latitude geográfica em graus decimais.
 * @property longitude Coordenada de longitude geográfica em graus decimais.
 * @property type Categoria funcional do marcador ([DeliveryMarkerType]).
 * @property zIndex Nível de elevação visual no mapa (garante que entregador fique acima das rotas).
 */
data class DeliveryMapMarker(
    val id: String,
    val title: String,
    val snippet: String,
    val latitude: Double,
    val longitude: Double,
    val type: DeliveryMarkerType,
    val zIndex: Float = when (type) {
        DeliveryMarkerType.DRIVER -> 3.0f
        DeliveryMarkerType.STORE -> 2.0f
        DeliveryMarkerType.CUSTOMER -> 1.0f
    }
)

/**
 * Caixa delimitadora (Bounding Box) englobando múltiplos pontos para ajuste automático de zoom na câmera do mapa.
 *
 * @property minLatitude Menor latitude encontrada.
 * @property maxLatitude Maior latitude encontrada.
 * @property minLongitude Menor longitude encontrada.
 * @property maxLongitude Maior longitude encontrada.
 * @property centerLatitude Latitude central do ponto médio.
 * @property centerLongitude Longitude central do ponto médio.
 */
data class MapBoundingBox(
    val minLatitude: Double,
    val maxLatitude: Double,
    val minLongitude: Double,
    val maxLongitude: Double,
    val centerLatitude: Double,
    val centerLongitude: Double
)

/**
 * Renderizador e orquestrador corporativo de Marcadores Vetoriais para o Mapa de Rastreamento.
 *
 * Gerencia a criação, estilização semântica e cálculo de enquadramento (bounds) para os pontos
 * de interesse do pedido (Loja, Destino e Entregador em trânsito).
 *
 * ### 📌 Quando Usar:
 * Utilize na inicialização e atualização do mapa de entrega (:feature:delivery:maps) para posicionar
 * os ícones vetoriais corretos e enquadrar a câmera de forma que o cliente visualize loja, motorista
 * e destino simultaneamente.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val renderer = DeliveryMapMarkerRenderer()
 *
 * // Criar marcadores da entrega:
 * val store = renderer.createStoreMarker("Loja Central", -23.5505, -46.6333)
 * val customer = renderer.createCustomerMarker("Residência", -23.5615, -46.6559)
 * val driver = renderer.createDriverMarker("João (Moto)", -23.5550, -46.6450, distanceMeters = 1500.0)
 *
 * // Calcular limites para ajustar a câmera do mapa:
 * val bounds = renderer.computeBoundingBox(listOf(store, customer, driver))
 * ```
 */
@Singleton
class DeliveryMapMarkerRenderer @Inject constructor() {

    /**
     * Cria o marcador correspondente à loja/estabelecimento parceiro de coleta.
     *
     * @param title Nome comercial da loja.
     * @param latitude Latitude da loja.
     * @param longitude Longitude da loja.
     * @param snippet Descrição complementar (ex: endereço).
     * @return Instância configurada de [DeliveryMapMarker].
     */
    fun createStoreMarker(
        title: String,
        latitude: Double,
        longitude: Double,
        snippet: String = "Ponto de Coleta"
    ): DeliveryMapMarker {
        return DeliveryMapMarker(
            id = "marker_store",
            title = title,
            snippet = snippet,
            latitude = latitude,
            longitude = longitude,
            type = DeliveryMarkerType.STORE
        )
    }

    /**
     * Cria o marcador correspondente ao endereço de destino do cliente.
     *
     * @param title Rótulo do endereço (ex: "Casa", "Trabalho").
     * @param latitude Latitude de entrega.
     * @param longitude Longitude de entrega.
     * @param snippet Informações de entrega adicionais.
     * @return Instância configurada de [DeliveryMapMarker].
     */
    fun createCustomerMarker(
        title: String,
        latitude: Double,
        longitude: Double,
        snippet: String = "Destino da Entrega"
    ): DeliveryMapMarker {
        return DeliveryMapMarker(
            id = "marker_customer",
            title = title,
            snippet = snippet,
            latitude = latitude,
            longitude = longitude,
            type = DeliveryMarkerType.CUSTOMER
        )
    }

    /**
     * Cria o marcador do entregador com cálculo de estimativa em minutos restante no snippet.
     *
     * @param driverName Nome do condutor.
     * @param latitude Latitude atualizada do veículo.
     * @param longitude Longitude atualizada do veículo.
     * @param distanceMeters Distância restante em metros até o cliente.
     * @param averageSpeedKmH Velocidade média estimada em km/h (padrão: 30 km/h em trânsito urbano).
     * @return Instância configurada de [DeliveryMapMarker].
     */
    fun createDriverMarker(
        driverName: String,
        latitude: Double,
        longitude: Double,
        distanceMeters: Double = 0.0,
        averageSpeedKmH: Double = 30.0
    ): DeliveryMapMarker {
        val snippet = if (distanceMeters > 0.0) {
            val minutes = calculateEtaMinutes(distanceMeters, averageSpeedKmH)
            "A caminho • Chegada em ~$minutes min"
        } else {
            "Entregador no local"
        }

        return DeliveryMapMarker(
            id = "marker_driver",
            title = driverName,
            snippet = snippet,
            latitude = latitude,
            longitude = longitude,
            type = DeliveryMarkerType.DRIVER
        )
    }

    /**
     * Calcula o bounding box mínimo contendo todos os marcadores para enquadramento da câmera.
     *
     * @param markers Lista de marcadores a serem exibidos simultaneamente no mapa.
     * @return [MapBoundingBox] com limites geográficos calculados ou null se a lista estiver vazia.
     */
    fun computeBoundingBox(markers: List<DeliveryMapMarker>): MapBoundingBox? {
        if (markers.isEmpty()) return null

        val minLat = markers.minOf { it.latitude }
        val maxLat = markers.maxOf { it.latitude }
        val minLng = markers.minOf { it.longitude }
        val maxLng = markers.maxOf { it.longitude }

        return MapBoundingBox(
            minLatitude = minLat,
            maxLatitude = maxLat,
            minLongitude = minLng,
            maxLongitude = maxLng,
            centerLatitude = (minLat + maxLat) / 2.0,
            centerLongitude = (minLng + maxLng) / 2.0
        )
    }

    /**
     * Calcula a estimativa de tempo de chegada (ETA) em minutos.
     *
     * @param distanceMeters Distância em metros.
     * @param speedKmH Velocidade média em km/h.
     * @return Minutos estimados arredondados (mínimo de 1 minuto).
     */
    fun calculateEtaMinutes(distanceMeters: Double, speedKmH: Double = 30.0): Int {
        val speedMps = (speedKmH * 1000.0) / 3600.0
        val seconds = distanceMeters / speedMps
        val minutes = (seconds / 60.0).roundToInt()
        return if (minutes < 1) 1 else minutes
    }
}
