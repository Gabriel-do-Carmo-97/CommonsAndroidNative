package br.com.wgc.maps.navigation

/**
 * Destinos e rotas fortemente tipadas para o ecossistema de Mapas, Geocodificação e Rastreamento em Tempo Real.
 *
 * Provê rotas seguras para navegação Compose dentro dos fluxos de entrega e logística urbana.
 *
 * ### 📌 Quando Usar:
 * Utilize para transição entre a visão geral do mapa, detalhes do endereço ou acompanhamento
 * ativo de um entregador ao vivo.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val route = MapsNavDestinations.LiveTracking(entityId = "driver_102", entityType = "drivers")
 * ```
 */
sealed interface MapsNavDestinations {

    /**
     * Rota de visualização geral e exploração do mapa.
     *
     * ### 📌 Quando Usar:
     * Para exibir a área geográfica geral com raio de cobertura de estabelecimentos parceiros.
     */
    data object MapOverview : MapsNavDestinations

    /**
     * Rota para visualização dos detalhes e geocodificação de um endereço específico.
     *
     * ### 📌 Quando Usar:
     * Quando o usuário clica em um endereço salvo para edição ou confirmação de coordenadas no mapa.
     *
     * @property addressId Identificador único do endereço registrado.
     */
    data class AddressDetails(val addressId: String) : MapsNavDestinations

    /**
     * Rota de rastreamento em tempo real de entidade parceira (motorista ou veículo).
     *
     * ### 📌 Quando Usar:
     * Disparada a partir da tela de pedido para abrir o mapa dinâmico com telemetria GPS ao vivo.
     *
     * ### 🛠️ Modo de Usar:
     * ```kotlin
     * val dest = MapsNavDestinations.LiveTracking("mot_881")
     * ```
     *
     * @property entityId Identificador único do motorista ou pedido monitorado.
     * @property entityType Coleção ou tipo de entidade rastreada no OmniBackend (padrão: "drivers").
     */
    data class LiveTracking(val entityId: String, val entityType: String = "drivers") : MapsNavDestinations
}