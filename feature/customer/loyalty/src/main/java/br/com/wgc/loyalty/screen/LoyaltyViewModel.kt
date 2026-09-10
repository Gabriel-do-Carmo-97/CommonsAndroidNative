package br.com.wgc.loyalty.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * NÃ­veis (Tiers) do programa de fidelidade com pontuaÃ§Ã£o mÃ­nima e benefÃ­cios associados.
 *
 * @property label RÃ³tulo amigÃ¡vel do nÃ­vel exibido na UI.
 * @property minPoints PontuaÃ§Ã£o mÃ­nima exigida para alcanÃ§ar este nÃ­vel.
 * @property benefit DescriÃ§Ã£o sucinta das vantagens exclusivas do nÃ­vel.
 */
enum class LoyaltyTier(val label: String, val minPoints: Int, val benefit: String) {
    /** NÃ­vel inicial com 1% de cashback. */
    BRONZE("Bronze", 0, "1% de cashback em todas as compras"),
    /** NÃ­vel intermediÃ¡rio com 2.5% de cashback e cupom mensal. */
    SILVER("Prata", 500, "2.5% de cashback + cupom mensal"),
    /** NÃ­vel avanÃ§ado com 5% de cashback e frete grÃ¡tis qualificado. */
    GOLD("Ouro", 1500, "5% de cashback + frete grÃ¡tis acima de R$ 50"),
    /** NÃ­vel de prestÃ­gio mÃ¡ximo com 10% de cashback e benefÃ­cios VIP. */
    DIAMOND("Diamante", 3000, "10% de cashback + acesso antecipado a promoÃ§Ãµes")
}

/**
 * Representa uma recompensa ou cupom resgatÃ¡vel com pontos acumulados.
 *
 * @param id Identificador Ãºnico da recompensa.
 * @param title Nome descritivo da recompensa ou benefÃ­cio.
 * @param pointsRequired Quantidade de pontos necessÃ¡ria para efetuar o resgate.
 * @param isRedeemed Indicador informando se a recompensa jÃ¡ foi resgatada pelo usuÃ¡rio.
 * @property id Identificador Ãºnico da recompensa.
 * @property title Nome descritivo da recompensa ou benefÃ­cio.
 * @property pointsRequired Quantidade de pontos necessÃ¡ria para efetuar o resgate.
 * @property isRedeemed Indicador informando se a recompensa jÃ¡ foi resgatada pelo usuÃ¡rio.
 */
data class LoyaltyReward(
    val id: String,
    val title: String,
    val pointsRequired: Int,
    val isRedeemed: Boolean = false
)

/**
 * Estado imutÃ¡vel da tela do programa de fidelidade e recompensas.
 *
 * @property title TÃ­tulo principal exibido na tela.
 * @property currentPoints Saldo total de pontos vÃ¡lidos do cliente.
 * @property currentCashback Saldo em moeda corrente acumulado via cashback.
 * @property tier NÃ­vel atual no programa de fidelidade.
 * @property currentStamps Quantidade de carimbos preenchidos na cartela atual.
 * @property maxStamps Quantidade total de carimbos para completar uma cartela.
 * @property rewards Lista de recompensas disponÃ­veis para resgate.
 * @property feedbackMessage Mensagem contextual de feedback apÃ³s aÃ§Ãµes de carimbo ou resgate.
 */
data class LoyaltyUiState(
    val title: String = "Clube de Fidelidade & Recompensas",
    val currentPoints: Int = 850,
    val currentCashback: String = "R$ 42,50",
    val tier: LoyaltyTier = LoyaltyTier.SILVER,
    val currentStamps: Int = 7,
    val maxStamps: Int = 10,
    val rewards: List<LoyaltyReward> = listOf(
        LoyaltyReward("r1", "Cupom R$ 15 OFF em qualquer compra", 300),
        LoyaltyReward("r2", "Sobremesa GrÃ¡tis no prÃ³ximo pedido", 500),
        LoyaltyReward("r3", "Frete GrÃ¡tis em 3 pedidos", 750),
        LoyaltyReward("r4", "Voucher R$ 50 OFF especial VIP", 1500)
    ),
    val feedbackMessage: String? = null
)

/**
 * ViewModel responsÃ¡vel pelas regras de fidelidade, evoluÃ§Ã£o de nÃ­veis e resgate de prÃªmios.
 */
@HiltViewModel
class LoyaltyViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(LoyaltyUiState())

    /**
     * Fluxo reativo contendo o estado atual do clube de fidelidade do usuÃ¡rio.
     */
    val uiState: StateFlow<LoyaltyUiState> = _uiState.asStateFlow()

    /**
     * Adiciona um carimbo Ã  cartela, bonificando o usuÃ¡rio com 50 pontos e recalculando o nÃ­vel.
     */
    fun addStamp() {
        _uiState.update { current ->
            val nextStamps = (current.currentStamps + 1).coerceAtMost(current.maxStamps)
            val addedPoints = current.currentPoints + 50
            val newTier = when {
                addedPoints >= LoyaltyTier.DIAMOND.minPoints -> LoyaltyTier.DIAMOND
                addedPoints >= LoyaltyTier.GOLD.minPoints -> LoyaltyTier.GOLD
                addedPoints >= LoyaltyTier.SILVER.minPoints -> LoyaltyTier.SILVER
                else -> LoyaltyTier.BRONZE
            }
            current.copy(
                currentStamps = nextStamps,
                currentPoints = addedPoints,
                tier = newTier,
                feedbackMessage = if (nextStamps == current.maxStamps) "ParabÃ©ns! VocÃª completou a cartela de carimbos!" else "Carimbo adicionado com sucesso!"
            )
        }
    }

    /**
     * Efetua o resgate de uma recompensa especÃ­fica debitando os pontos necessÃ¡rios do saldo.
     *
     * @param rewardId Identificador Ãºnico da recompensa desejada.
     */
    fun redeemReward(rewardId: String) {
        _uiState.update { current ->
            val target = current.rewards.firstOrNull { it.id == rewardId }
            if (target == null || current.currentPoints < target.pointsRequired) {
                current.copy(feedbackMessage = "Saldo de pontos insuficiente para resgate.")
            } else {
                val updatedRewards = current.rewards.map {
                    if (it.id == rewardId) it.copy(isRedeemed = true) else it
                }
                current.copy(
                    currentPoints = current.currentPoints - target.pointsRequired,
                    rewards = updatedRewards,
                    feedbackMessage = "Recompensa '${target.title}' resgatada com sucesso!"
                )
            }
        }
    }
}