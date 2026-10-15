package br.com.wgc.settings.privacy

import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Perfil cadastral individual do titular para exportação de dados.
 *
 * @property userId Identificador único universal do usuário.
 * @property fullName Nome completo civil registrado.
 * @property email Endereço eletrônico validado.
 * @property phone Número de telefone celular.
 * @property createdAt Data/hora de registro da conta.
 */
data class UserPersonalProfile(
    val userId: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val createdAt: String
)

/**
 * Registro de endereço residencial ou comercial cadastrado pelo titular.
 *
 * @property id Identificador do endereço.
 * @property street Logradouro/rua.
 * @property number Número ou complemento.
 * @property zipCode Código de Endereçamento Postal (CEP).
 * @property city Município.
 * @property state Unidade Federativa (UF).
 */
data class UserAddressRecord(
    val id: String,
    val street: String,
    val number: String,
    val zipCode: String,
    val city: String,
    val state: String
)

/**
 * Registro simplificado de transação de compra passada do usuário.
 *
 * @property orderId Código identificador do pedido.
 * @property date Data formatada da transação.
 * @property totalAmount Valor total pago.
 * @property itemsCount Quantidade de itens adquiridos.
 */
data class UserOrderRecord(
    val orderId: String,
    val date: String,
    val totalAmount: String,
    val itemsCount: Int
)

/**
 * Dossiê consolidado de portabilidade e acesso aos dados do titular (Artigo 18 da LGPD).
 *
 * @property profile Dados cadastrais e biográficos.
 * @property addresses Lista de endereços registrados.
 * @property orders Histórico consolidado de pedidos.
 * @property privacyConsents Preferências de privacidade vigentes.
 * @property exportedAt Carimbo de data/hora ISO-8601 da extração.
 * @property dataController Razão social da controladora dos dados.
 */
data class UserDataExportBundle(
    val profile: UserPersonalProfile,
    val addresses: List<UserAddressRecord>,
    val orders: List<UserOrderRecord>,
    val privacyConsents: PrivacyConsentPreferences,
    val exportedAt: String,
    val dataController: String = "WGC Commons Enterprise"
)

/**
 * Exportador corporativo de Dados Pessoais do Titular (Direito de Acesso e Portabilidade - LGPD Art. 18, II e V).
 *
 * Compila todas as informações cadastrais, de entrega, histórico financeiro e consentimentos
 * em um documento JSON padronizado e auditável para download direto pelo usuário.
 *
 * ### 📌 Quando Usar:
 * Acione na opção "Baixar Meus Dados Pessoais" da tela de Privacidade & Segurança ([br.com.wgc.settings.screen.SettingsScreen]).
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val exporter = UserDataExporter()
 * val bundle = UserDataExportBundle(...)
 * val jsonFileContent = exporter.generateExportJson(bundle)
 * // Salvar em arquivo ou compartilhar via Intent
 * ```
 */
@Singleton
class UserDataExporter @Inject constructor() {

    /**
     * Serializa o dossiê completo de dados pessoais em uma String JSON estruturada e formatada.
     *
     * @param bundle Dossiê [UserDataExportBundle] compilado com os dados do usuário.
     * @return String JSON com recuo de 2 espaços para legibilidade.
     */
    fun generateExportJson(bundle: UserDataExportBundle): String {
        val root = JSONObject().apply {
            put("dataController", bundle.dataController)
            put("exportedAt", bundle.exportedAt)
            put("legalBasis", "LGPD Lei 13.709/2018 - Artigo 18 (Direito de Acesso e Portabilidade)")

            // Perfil
            put("profile", JSONObject().apply {
                put("userId", bundle.profile.userId)
                put("fullName", bundle.profile.fullName)
                put("email", bundle.profile.email)
                put("phone", bundle.profile.phone)
                put("createdAt", bundle.profile.createdAt)
            })

            // Endereços
            put("addresses", JSONArray().apply {
                bundle.addresses.forEach { addr ->
                    put(JSONObject().apply {
                        put("id", addr.id)
                        put("street", addr.street)
                        put("number", addr.number)
                        put("zipCode", addr.zipCode)
                        put("city", addr.city)
                        put("state", addr.state)
                    })
                }
            })

            // Pedidos
            put("orders", JSONArray().apply {
                bundle.orders.forEach { ord ->
                    put(JSONObject().apply {
                        put("orderId", ord.orderId)
                        put("date", ord.date)
                        put("totalAmount", ord.totalAmount)
                        put("itemsCount", ord.itemsCount)
                    })
                }
            })

            // Consentimentos
            put("privacyConsents", JSONObject().apply {
                put("essentialAccepted", bundle.privacyConsents.essentialAccepted)
                put("analyticsAccepted", bundle.privacyConsents.analyticsAccepted)
                put("marketingAccepted", bundle.privacyConsents.marketingAccepted)
                put("personalizationAccepted", bundle.privacyConsents.personalizationAccepted)
                put("policyVersion", bundle.privacyConsents.policyVersion)
            })
        }

        return root.toString(2)
    }

    /**
     * Valida a integridade do arquivo JSON de exportação conferindo chaves mandatórias.
     *
     * @param jsonContent String contendo o JSON a ser validado.
     * @return true se o payload possuir todas as seções obrigatórias da LGPD.
     */
    fun validateExportIntegrity(jsonContent: String): Boolean {
        return try {
            val root = JSONObject(jsonContent)
            root.has("profile") &&
            root.has("addresses") &&
            root.has("orders") &&
            root.has("privacyConsents") &&
            root.has("legalBasis")
        } catch (_: Exception) {
            false
        }
    }
}
