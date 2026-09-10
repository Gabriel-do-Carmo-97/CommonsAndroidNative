package br.com.wgc.maps.navigation

/**
 * Destinos e rotas fortemente tipadas para o ecossistema de Mapas e Rastreamento.
 */
sealed interface MapsNavDestinations {

    /**
     * Rota de visualizaÃ§Ã£o geral e exploraÃ§Ã£o do mapa.
     */
    data object MapOverview : MapsNavDestinations

    /**
     * Rota para visualizaÃ§Ã£o dos detalhes e geocodificaÃ§Ã£o de um endereÃ§o.
     *
     * @param addressId Identificador Ãºnico do endereÃ§o registrado.
     * @property addressId Identificador Ãºnico do endereÃ§o registrado.
     */
    data class AddressDetails(val addressId: String) : MapsNavDestinations

    /**
     * Rota de rastreamento em tempo real de entidade parceira (motorista ou veÃ­culo).
     *
     * @param entityId Identificador Ãºnico do motorista ou pedido monitorado.
     * @param entityType ColeÃ§Ã£o ou tipo de entidade rastreada (ex: `drivers`).
     * @property entityId Identificador Ãºnico do motorista ou pedido monitorado.
     * @property entityType ColeÃ§Ã£o ou tipo de entidade rastreada (ex: `drivers`).
     */
    data class LiveTracking(val entityId: String, val entityType: String = "drivers") : MapsNavDestinations
}