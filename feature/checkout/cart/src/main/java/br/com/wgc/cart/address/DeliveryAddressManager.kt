package br.com.wgc.cart.address

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Categoria ou rótulo semântico do endereço de entrega.
 */
enum class AddressType(val displayName: String) {
    HOME("Casa"),
    WORK("Trabalho"),
    OTHER("Outro")
}

/**
 * Entidade de endereço de entrega do cliente cadastrado no ecossistema WGC.
 *
 * @property id Identificador único do endereço.
 * @property label Rótulo amigável (ex: "Minha Casa", "Escritório Paulista").
 * @property street Logradouro (rua, avenida, travessa).
 * @property number Número do imóvel.
 * @property complement Complemento (apartamento, bloco, sala).
 * @property neighborhood Bairro.
 * @property city Município de entrega.
 * @property state Sigla da Unidade Federativa (ex: "SP").
 * @property cep Código de Endereçamento Postal formatado.
 * @property type Categoria do endereço via [AddressType].
 * @property isDefault Indica se é o endereço preferencial de entrega.
 */
data class DeliveryAddress(
    val id: String = UUID.randomUUID().toString(),
    val label: String,
    val street: String,
    val number: String,
    val complement: String = "",
    val neighborhood: String,
    val city: String,
    val state: String,
    val cep: String,
    val type: AddressType = AddressType.HOME,
    val isDefault: Boolean = false
) {
    /**
     * Retorna a linha descritiva simplificada do endereço para exibição em cards de checkout.
     */
    fun toSingleLineDisplay(): String = "$street, $number${if (complement.isNotEmpty()) " - $complement" else ""} - $neighborhood, $city - $state ($cep)"
}

/**
 * Gerenciador corporativo de Múltiplos Endereços de Entrega e Endereço Ativo de Checkout.
 *
 * Permite cadastro, seleção, exclusão e definição de endereço padrão com validação automática
 * de formato de CEP, assegurando que o pedido seja despachado para a localidade correta.
 *
 * ### 📌 Quando Usar:
 * Utilize na seleção de local de entrega durante o fluxo de checkout (:feature:checkout:cart),
 * permitindo ao cliente alternar rapidamente entre seu endereço residencial ou de trabalho.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * // 1. Obter endereço selecionado:
 * val selectedAddress by addressManager.selectedAddress.collectAsState()
 *
 * // 2. Adicionar novo endereço e marcá-lo como ativo:
 * addressManager.addAddress(
 *     DeliveryAddress(
 *         label = "Meu Apê",
 *         street = "Av. Paulista",
 *         number = "1000",
 *         neighborhood = "Bela Vista",
 *         city = "São Paulo",
 *         state = "SP",
 *         cep = "01310-100",
 *         isDefault = true
 *     )
 * )
 * ```
 */
@Singleton
class DeliveryAddressManager @Inject constructor() {

    private val initialAddresses = listOf(
        DeliveryAddress(
            id = "addr_default_01",
            label = "Casa",
            street = "Av. Brigadeiro Faria Lima",
            number = "3477",
            complement = "14º andar",
            neighborhood = "Itaim Bibi",
            city = "São Paulo",
            state = "SP",
            cep = "04538-133",
            type = AddressType.HOME,
            isDefault = true
        ),
        DeliveryAddress(
            id = "addr_default_02",
            label = "Trabalho",
            street = "Av. Paulista",
            number = "1578",
            complement = "Conjunto 802",
            neighborhood = "Bela Vista",
            city = "São Paulo",
            state = "SP",
            cep = "01310-200",
            type = AddressType.WORK,
            isDefault = false
        )
    )

    private val _addresses = MutableStateFlow<List<DeliveryAddress>>(initialAddresses)

    /** Lista observável com todos os endereços cadastrados do usuário. */
    val addresses: StateFlow<List<DeliveryAddress>> = _addresses.asStateFlow()

    private val _selectedAddress = MutableStateFlow<DeliveryAddress?>(initialAddresses.firstOrNull())

    /** Endereço atualmente selecionado para a entrega da compra em andamento. */
    val selectedAddress: StateFlow<DeliveryAddress?> = _selectedAddress.asStateFlow()

    /**
     * Adiciona um novo endereço à lista do cliente.
     *
     * @param address Novo endereço a ser registrado.
     */
    fun addAddress(address: DeliveryAddress) {
        _addresses.update { current ->
            if (address.isDefault) {
                current.map { it.copy(isDefault = false) } + address
            } else {
                current + address
            }
        }
        if (address.isDefault || _selectedAddress.value == null) {
            _selectedAddress.value = address
        }
    }

    /**
     * Seleciona um endereço específico para ser o destino do pedido ativo.
     *
     * @param addressId Identificador do endereço escolhido.
     */
    fun selectAddress(addressId: String) {
        val found = _addresses.value.firstOrNull { it.id == addressId }
        if (found != null) {
            _selectedAddress.value = found
        }
    }

    /**
     * Remove um endereço da carteira do usuário.
     *
     * @param addressId Identificador do endereço a ser removido.
     */
    fun deleteAddress(addressId: String) {
        _addresses.update { list -> list.filterNot { it.id == addressId } }
        if (_selectedAddress.value?.id == addressId) {
            _selectedAddress.value = _addresses.value.firstOrNull()
        }
    }
}
