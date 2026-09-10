package br.com.wgc.commonsandroidnative

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

enum class ShowcaseCategory(val title: String) {
    ARCHETYPES("🚀 Fluxos Ponta-a-Ponta"),
    BUNDLES("📦 Bundles Comerciais (42)"),
    ECOMMERCE("E-Commerce & Vendas"),
    SERVICES("Operações & Serviços"),
    ENGAGEMENT("Engajamento & Fidelidade"),
    SECURITY_CONFIG("Configuração, Segurança & Omni")
}

sealed class ShowcaseDestination(
    val route: String,
    val title: String,
    val description: String,
    val category: ShowcaseCategory,
    val icon: ImageVector
) {
    // E-Commerce & Vendas
    data object Catalog : ShowcaseDestination(
        route = "catalog",
        title = "Catálogo de Produtos",
        description = "EcommerceHomeScreenTemplate com OmniBackend Firestore",
        category = ShowcaseCategory.ECOMMERCE,
        icon = Icons.Default.ShoppingCart
    )

    data object Cart : ShowcaseDestination(
        route = "cart",
        title = "Carrinho de Compras",
        description = "StandardCartScreenTemplate com cálculo de frete e cupom",
        category = ShowcaseCategory.ECOMMERCE,
        icon = Icons.Default.ShoppingCart
    )

    data object Search : ShowcaseDestination(
        route = "search",
        title = "Busca & Filtros",
        description = "SearchAndFilterScreenTemplate com busca reativa e debounce",
        category = ShowcaseCategory.ECOMMERCE,
        icon = Icons.Default.Search
    )

    data object Promotions : ShowcaseDestination(
        route = "promotions",
        title = "Stories Promocionais",
        description = "InstagramStoryViewerScreenTemplate com stories dinâmicos",
        category = ShowcaseCategory.ECOMMERCE,
        icon = Icons.Default.Star
    )

    data object Payment : ShowcaseDestination(
        route = "payment",
        title = "Fintech & Pagamentos",
        description = "FintechHomeScreenTemplate com Pix, saldo e transações",
        category = ShowcaseCategory.ECOMMERCE,
        icon = Icons.Default.Lock
    )

    data object Stores : ShowcaseDestination(
        route = "stores",
        title = "Multi-Lojas & Filiais",
        description = "Seletor de lojas WGC integrado ao Firestore com status e distância",
        category = ShowcaseCategory.ECOMMERCE,
        icon = Icons.Default.Place
    )

    // Operações & Serviços
    data object Scheduling : ShowcaseDestination(
        route = "scheduling",
        title = "Agendamentos de Serviços",
        description = "Reserva de horários e gestão de prestadores",
        category = ShowcaseCategory.SERVICES,
        icon = Icons.Default.DateRange
    )

    data object Quotation : ShowcaseDestination(
        route = "quotation",
        title = "Cotação & Orçamentos",
        description = "Simulação de valores e cálculo personalizado",
        category = ShowcaseCategory.SERVICES,
        icon = Icons.Default.Build
    )

    data object OrderTracking : ShowcaseDestination(
        route = "order_tracking",
        title = "Rastreamento de Pedido",
        description = "Timeline em tempo real de status do pedido",
        category = ShowcaseCategory.SERVICES,
        icon = Icons.Default.LocationOn
    )

    data object Maps : ShowcaseDestination(
        route = "maps",
        title = "Mapas & Live Tracking",
        description = "Visualização de geolocalização e rotas WGC",
        category = ShowcaseCategory.SERVICES,
        icon = Icons.Default.LocationOn
    )

    // Engajamento & Fidelidade
    data object Loyalty : ShowcaseDestination(
        route = "loyalty",
        title = "Fidelidade & Pontos",
        description = "Extrato de cashback, pontos e recompensas",
        category = ShowcaseCategory.ENGAGEMENT,
        icon = Icons.Default.Favorite
    )

    data object Feedback : ShowcaseDestination(
        route = "feedback",
        title = "Feedback & Pesquisa NPS",
        description = "Formulário de satisfação e comentários dos clientes",
        category = ShowcaseCategory.ENGAGEMENT,
        icon = Icons.Default.Star
    )

    data object ReviewsStore : ShowcaseDestination(
        route = "reviews_store",
        title = "Avaliação na Loja",
        description = "Prompt in-app review e redirecionamento Google Play",
        category = ShowcaseCategory.ENGAGEMENT,
        icon = Icons.Default.Star
    )

    data object WhatsappDirect : ShowcaseDestination(
        route = "whatsapp_direct",
        title = "Atendimento WhatsApp",
        description = "Deep link direto para canais de suporte no WhatsApp",
        category = ShowcaseCategory.ENGAGEMENT,
        icon = Icons.AutoMirrored.Filled.Send
    )

    data object Chat : ShowcaseDestination(
        route = "chat",
        title = "Chat de Suporte",
        description = "Chat em tempo real com OmniBackend",
        category = ShowcaseCategory.ENGAGEMENT,
        icon = Icons.Default.Email
    )

    // Configuração, Segurança & Omni
    data object Auth : ShowcaseDestination(
        route = "auth",
        title = "Autenticação WGC",
        description = "Login, Registro e Sessão com WgcAuthManager",
        category = ShowcaseCategory.SECURITY_CONFIG,
        icon = Icons.Default.AccountCircle
    )

    data object Profile : ShowcaseDestination(
        route = "profile",
        title = "Perfil do Usuário",
        description = "SettingsHubScreenTemplate conectado à sessão ativa",
        category = ShowcaseCategory.SECURITY_CONFIG,
        icon = Icons.Default.AccountCircle
    )

    data object Settings : ShowcaseDestination(
        route = "settings",
        title = "Configurações Globais",
        description = "SettingsHubScreenTemplate com preferências e logout",
        category = ShowcaseCategory.SECURITY_CONFIG,
        icon = Icons.Default.Settings
    )

    data object Biometric : ShowcaseDestination(
        route = "biometric",
        title = "Biometria & App Lock",
        description = "Autenticação biométrica nativa com BiometricAuthHelper",
        category = ShowcaseCategory.SECURITY_CONFIG,
        icon = Icons.Default.Face
    )

    data object MediaPicker : ShowcaseDestination(
        route = "media_picker",
        title = "Upload de Imagens & Câmera",
        description = "Seletor de fotos integrado ao OmniBackend Storage",
        category = ShowcaseCategory.SECURITY_CONFIG,
        icon = Icons.Default.Build
    )

    data object ForceUpdate : ShowcaseDestination(
        route = "force_update",
        title = "Force Update Remoto",
        description = "Bloqueio e checagem de versão via RemoteConfigRepository",
        category = ShowcaseCategory.SECURITY_CONFIG,
        icon = Icons.Default.Info
    )

    data object Onboarding : ShowcaseDestination(
        route = "onboarding",
        title = "Onboarding Inicial",
        description = "Apresentação e boas-vindas para novos usuários",
        category = ShowcaseCategory.SECURITY_CONFIG,
        icon = Icons.Default.Info
    )

    // Archetype Flow Coordinators
    data object FoodDeliveryFlow : ShowcaseDestination(
        route = "flow_food_delivery",
        title = "Fluxo: Food Delivery Completo",
        description = "Catálogo iFood ➔ Carrinho ➔ Pix ➔ Live Tracking ➔ Avaliação",
        category = ShowcaseCategory.ARCHETYPES,
        icon = Icons.Default.ShoppingCart
    )

    data object RetailEcommerceFlow : ShowcaseDestination(
        route = "flow_retail_ecommerce",
        title = "Fluxo: Varejo / Mercado Livre",
        description = "Busca ➔ Grade Mercado Livre ➔ Carrinho ➔ Pagamento ➔ Rastreio",
        category = ShowcaseCategory.ARCHETYPES,
        icon = Icons.Default.Search
    )

    data object ServiceBookingFlow : ShowcaseDestination(
        route = "flow_service_booking",
        title = "Fluxo: Agendamento & Serviços",
        description = "Lojas ➔ Data/Hora ➔ Orçamento ➔ Confirmação no WhatsApp",
        category = ShowcaseCategory.ARCHETYPES,
        icon = Icons.Default.DateRange
    )

    data object CommercialBundles : ShowcaseDestination(
        route = "commercial_bundles",
        title = "Catálogo dos 42 Bundles",
        description = "14 Domínios × 3 Níveis (Basic, Standard, Pro) com composições completas",
        category = ShowcaseCategory.BUNDLES,
        icon = Icons.Default.Build
    )

    companion object {
        val allDestinations: List<ShowcaseDestination> = listOf(
            CommercialBundles,
            FoodDeliveryFlow, RetailEcommerceFlow, ServiceBookingFlow,
            Catalog, Cart, Search, Promotions, Payment, Stores,
            Scheduling, Quotation, OrderTracking, Maps,
            Loyalty, Feedback, ReviewsStore, WhatsappDirect, Chat,
            Auth, Profile, Settings, Biometric, MediaPicker, ForceUpdate, Onboarding
        )
    }
}

