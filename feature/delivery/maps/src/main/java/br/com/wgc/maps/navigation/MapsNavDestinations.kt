package br.com.wgc.maps.navigation

/**
 * Destinos e rotas fortemente tipadas para o ecossistema de Mapas e Rastreamento.
 */
sealed interface MapsNavDestinations {

    /**
     * Rota de visualização geral e exploração do mapa.
     */
    data object MapOverview : MapsNavDestinations

    /**
     * Rota para visualização dos detalhes e geocodificação de um endereço.
     *
     * @param addressId Identificador único do endereço registrado.
     * @property addressId Identificador único do endereço registrado.
     */
    data class AddressDetails(val addressId: String) : MapsNavDestinations

    /**
     * Rota de rastreamento em tempo real de entidade parceira (motorista ou veículo).
     *
     * @param entityId Identificador único do motorista ou pedido monitorado.
     * @param entityType Coleção ou tipo de entidade rastreada (ex: `drivers`).
     * @property entityId Identificador único do motorista ou pedido monitorado.
     * @property entityType Coleção ou tipo de entidade rastreada (ex: `drivers`).
     */
    data class LiveTracking(val entityId: String, val entityType: String = "drivers") : MapsNavDestinations
}