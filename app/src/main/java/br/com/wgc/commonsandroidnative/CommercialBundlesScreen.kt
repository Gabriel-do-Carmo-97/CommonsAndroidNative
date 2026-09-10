package br.com.wgc.commonsandroidnative

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Modelo descritivo para exibição dos 42 bundles comerciais no showcase do app.
 *
 * @param domain Domínio de negócio do bundle.
 * @param tier Nível comercial (basic, standard ou pro).
 * @param title Nome amigável do bundle.
 * @param description Resumo do escopo comercial do pacote.
 * @param features Lista de módulos de feature integrados ao bundle.
 */
data class BundleShowcaseItem(
    val domain: String,
    val tier: String,
    val title: String,
    val description: String,
    val features: List<String>
) {
    /** Coordenada de publicação Maven no GitHub Packages. */
    val artifactCoordinate: String = "br.com.wgc:bundle-$domain-$tier"
}

/**
 * Tela interativa do Showcase exibindo o catálogo completo de 42 Bundles Comerciais.
 *
 * @param modifier Modificador de layout do Jetpack Compose.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CommercialBundlesScreen(modifier: Modifier = Modifier) {
    var selectedTier by rememberSaveable { mutableStateOf<String?>(null) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val allBundles = rememberBundlesCatalog()

    val filtered = allBundles.filter { item ->
        val matchesTier = selectedTier == null || item.tier.equals(selectedTier, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.domain.contains(searchQuery, ignoreCase = true) ||
                item.features.any { it.contains(searchQuery, ignoreCase = true) }
        matchesTier && matchesSearch
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "42 Bundles Comerciais WGC",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Soluções modulares prontas para aplicativos white-label e clientes.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Filtrar por domínio ou feature...") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = selectedTier == null,
                onClick = { selectedTier = null },
                label = { Text("Todos (${allBundles.size})") }
            )
            listOf("Basic", "Standard", "Pro").forEach { tier ->
                FilterChip(
                    selected = selectedTier.equals(tier, ignoreCase = true),
                    onClick = {
                        selectedTier = if (selectedTier.equals(tier, ignoreCase = true)) null else tier
                    },
                    label = { Text(tier) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filtered, key = { "${it.domain}-${it.tier}" }) { bundle ->
                BundleItemCard(bundle)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BundleItemCard(bundle: BundleShowcaseItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = bundle.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (bundle.tier.lowercase()) {
                        "pro" -> MaterialTheme.colorScheme.primaryContainer
                        "standard" -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.tertiaryContainer
                    }
                ) {
                    Text(
                        text = bundle.tier.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = bundle.artifactCoordinate,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = bundle.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Features inclusas:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                bundle.features.forEach { feat ->
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Text(
                            text = ":feature:$feat",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun rememberBundlesCatalog(): List<BundleShowcaseItem> = listOf(
    // Communication
    BundleShowcaseItem("communication", "basic", "Communication Basic", "Chat em tempo real e transbordo WhatsApp", listOf("communication:message", "communication:whatsapp-direct")),
    BundleShowcaseItem("communication", "standard", "Communication Standard", "Comunicação completa com envio de mídias e feedback", listOf("communication:message", "communication:whatsapp-direct", "system:media-picker", "customer:feedback")),
    BundleShowcaseItem("communication", "pro", "Communication Pro", "Suporte com assistente virtual de inteligência artificial", listOf("communication:message", "communication:whatsapp-direct", "system:media-picker", "customer:feedback", "platform:ai-assistant")),

    // Delivery
    BundleShowcaseItem("delivery", "basic", "Delivery Basic", "Rastreio e mapas em tempo real para o cliente", listOf("delivery:order-tracking", "delivery:maps")),
    BundleShowcaseItem("delivery", "standard", "Delivery Standard", "App do entregador e telemetria de operação", listOf("delivery:order-tracking", "delivery:maps", "delivery:driver-app", "platform:telemetry")),
    BundleShowcaseItem("delivery", "pro", "Delivery Pro", "Despacho operacional, geofencing e mapas offline", listOf("delivery:order-tracking", "delivery:maps", "delivery:driver-app", "platform:telemetry", "delivery:dispatch", "delivery:geofencing", "delivery:offline-maps")),

    // Ecommerce
    BundleShowcaseItem("ecommerce", "basic", "Ecommerce Basic", "Catálogo, carrinho persistente e gateway de pagamento", listOf("storefront:catalog", "checkout:cart", "checkout:payment")),
    BundleShowcaseItem("ecommerce", "standard", "Ecommerce Standard", "Busca avançada, promoções e avaliações da loja", listOf("storefront:catalog", "checkout:cart", "checkout:payment", "storefront:search", "storefront:promotions", "customer:reviews-store")),
    BundleShowcaseItem("ecommerce", "pro", "Ecommerce Pro", "Fidelidade, multi-lojas físicas e recomendação por IA", listOf("storefront:catalog", "checkout:cart", "checkout:payment", "storefront:search", "storefront:promotions", "customer:reviews-store", "customer:loyalty", "storefront:stores", "platform:ai-assistant")),

    // Education
    BundleShowcaseItem("education", "basic", "Education Basic", "Agendamento de aulas e perfil do estudante", listOf("services:scheduling", "account:profile")),
    BundleShowcaseItem("education", "standard", "Education Standard", "Chat com professores e upload de atividades", listOf("services:scheduling", "account:profile", "communication:message", "system:media-picker")),
    BundleShowcaseItem("education", "pro", "Education Pro", "Tutor IA integrado e canal de feedback acadêmico", listOf("services:scheduling", "account:profile", "communication:message", "system:media-picker", "account:settings", "customer:feedback", "platform:ai-assistant")),

    // Emergency
    BundleShowcaseItem("emergency", "basic", "Emergency Basic", "Alerta de pânico e mapa de socorro", listOf("platform:emergency", "delivery:maps")),
    BundleShowcaseItem("emergency", "standard", "Emergency Standard", "Telemetria de socorrista e canal direto WhatsApp", listOf("platform:emergency", "delivery:maps", "platform:telemetry", "communication:whatsapp-direct")),
    BundleShowcaseItem("emergency", "pro", "Emergency Pro", "Cercamento eletrônico crítico e suporte a mapas offline", listOf("platform:emergency", "delivery:maps", "platform:telemetry", "communication:whatsapp-direct", "delivery:geofencing", "delivery:offline-maps")),

    // Events
    BundleShowcaseItem("events", "basic", "Events Basic", "Catálogo de ingressos e checkout de pagamento", listOf("storefront:catalog", "checkout:payment")),
    BundleShowcaseItem("events", "standard", "Events Standard", "Agendamento por lotes e mapa do evento", listOf("storefront:catalog", "checkout:payment", "services:scheduling", "delivery:maps")),
    BundleShowcaseItem("events", "pro", "Events Pro", "Upload de fotos do evento e cupons promocionais", listOf("storefront:catalog", "checkout:payment", "services:scheduling", "delivery:maps", "system:media-picker", "storefront:promotions")),

    // Finance
    BundleShowcaseItem("finance", "basic", "Finance Basic", "Pagamento seguro e liquidação financeira", listOf("checkout:payment")),
    BundleShowcaseItem("finance", "standard", "Finance Standard", "Biometria de segurança e gestão de conta", listOf("checkout:payment", "auth:biometric", "account:settings")),
    BundleShowcaseItem("finance", "pro", "Finance Pro", "Assinaturas recorrentes, analytics e assistente financeiro IA", listOf("checkout:payment", "auth:biometric", "account:settings", "services:subscriptions", "platform:analytics", "platform:ai-assistant")),

    // Foundation
    BundleShowcaseItem("foundation", "basic", "Foundation Basic", "Configurações e force update remoto", listOf("account:settings", "system:force-update")),
    BundleShowcaseItem("foundation", "standard", "Foundation Standard", "Telemetria analítica e eventos comportamentais", listOf("account:settings", "system:force-update", "platform:analytics")),
    BundleShowcaseItem("foundation", "pro", "Foundation Pro", "Multi-idiomas dinâmico e sincronização offline", listOf("account:settings", "system:force-update", "platform:analytics", "system:multi-language", "platform:offline-sync")),

    // Health
    BundleShowcaseItem("health", "basic", "Health Basic", "Agendamento de consultas e chat com médico", listOf("services:scheduling", "communication:message")),
    BundleShowcaseItem("health", "standard", "Health Standard", "Biometria médica e upload de exames/receitas", listOf("services:scheduling", "communication:message", "auth:biometric", "system:media-picker")),
    BundleShowcaseItem("health", "pro", "Health Pro", "Cotação de procedimentos e suporte a emergências", listOf("services:scheduling", "communication:message", "auth:biometric", "system:media-picker", "checkout:quotation", "platform:emergency")),

    // Identity
    BundleShowcaseItem("identity", "basic", "Identity Basic", "Autenticação OmniBackend e perfil de usuário", listOf("auth:authentication", "account:profile")),
    BundleShowcaseItem("identity", "standard", "Identity Standard", "Autenticação biométrica nativa com KeyStore", listOf("auth:authentication", "account:profile", "auth:biometric")),
    BundleShowcaseItem("identity", "pro", "Identity Pro", "Onboarding completo e suporte multi-idiomas", listOf("auth:authentication", "account:profile", "auth:biometric", "account:onboarding", "system:multi-language")),

    // Real Estate
    BundleShowcaseItem("real-estate", "basic", "Real Estate Basic", "Catálogo de imóveis e busca facetada", listOf("storefront:catalog", "storefront:search")),
    BundleShowcaseItem("real-estate", "standard", "Real Estate Standard", "Mapa geolocalizado de imóveis e agendamento de visitas", listOf("storefront:catalog", "storefront:search", "delivery:maps", "services:scheduling")),
    BundleShowcaseItem("real-estate", "pro", "Real Estate Pro", "Fotos em alta resolução e proposta/cotação formal", listOf("storefront:catalog", "storefront:search", "delivery:maps", "services:scheduling", "system:media-picker", "checkout:quotation")),

    // Services
    BundleShowcaseItem("services", "basic", "Services Basic", "Agendamento de serviços e localizador de prestadores", listOf("services:scheduling", "storefront:stores")),
    BundleShowcaseItem("services", "standard", "Services Standard", "Orçamentos personalizados e canal de feedback", listOf("services:scheduling", "storefront:stores", "checkout:quotation", "customer:feedback")),
    BundleShowcaseItem("services", "pro", "Services Pro", "Avaliações com fotos e checkout integrado", listOf("services:scheduling", "storefront:stores", "checkout:quotation", "customer:feedback", "customer:reviews-store", "checkout:payment")),

    // Social
    BundleShowcaseItem("social", "basic", "Social Basic", "Perfil social de membros e mensageria direta", listOf("account:profile", "communication:message")),
    BundleShowcaseItem("social", "standard", "Social Standard", "Upload de fotos para feed e avaliações comunitárias", listOf("account:profile", "communication:message", "system:media-picker", "customer:reviews-store")),
    BundleShowcaseItem("social", "pro", "Social Pro", "Feedback social e moderação inteligente via IA", listOf("account:profile", "communication:message", "system:media-picker", "customer:reviews-store", "customer:feedback", "platform:ai-assistant")),

    // Subscriptions
    BundleShowcaseItem("subscriptions", "basic", "Subscriptions Basic", "Catálogo de assinaturas e checkout recorrente", listOf("services:subscriptions", "checkout:payment")),
    BundleShowcaseItem("subscriptions", "standard", "Subscriptions Standard", "Promoções de ciclo e cupons de retenção", listOf("services:subscriptions", "checkout:payment", "storefront:promotions")),
    BundleShowcaseItem("subscriptions", "pro", "Subscriptions Pro", "Manutenção remota e analytics avançado de churn", listOf("services:subscriptions", "checkout:payment", "storefront:promotions", "system:force-update", "platform:analytics"))
)
