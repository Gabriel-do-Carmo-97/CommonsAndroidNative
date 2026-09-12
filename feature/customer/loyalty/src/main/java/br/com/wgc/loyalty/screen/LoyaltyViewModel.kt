package br.com.wgc.loyalty.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Níveis (Tiers) do programa de fidelidade com pontuação mínima e benefícios associados.
 *
 * @property label Rótulo amigável do nível exibido na UI.
 * @property minPoints Pontuação mínima exigida para alcançar este nível.
 * @property benefit Descrição sucinta das vantagens exclusivas do nível.
 */
enum class LoyaltyTier(val label: String, val minPoints: Int, val benefit: String) {
    /** Nível inicial com 1% de cashback. */
    BRONZE("Bronze", 0, "1% de cashback em todas as compras"),
    /** Nível intermediário com 2.5% de cashback e cupom mensal. */
    SILVER("Prata", 500, "2.5% de cashback + cupom mensal"),
    /** Nível avançado com 5% de cashback e frete grátis qualificado. */
    GOLD("Ouro", 1500, "5% de cashback + frete grátis acima de R$ 50"),
    /** Nível de prestígio máximo com 10% de cashback e benefícios VIP. */
    DIAMOND("Diamante", 3000, "10% de cashback + acesso antecipado a promoções")
}

/**
 * Representa uma recompensa ou cupom resgatável com pontos acumulados.
 *
 * @param id Identificador único da recompensa.
 * @param title Nome descritivo da recompensa ou benefício.
 * @param pointsRequired Quantidade de pontos necessária para efetuar o resgate.
 * @param isRedeemed Indicador informando se a recompensa já foi resgatada pelo usuário.
 * @property id Identificador único da recompensa.
 * @property title Nome descritivo da recompensa ou benefício.
 * @property pointsRequired Quantidade de pontos necessária para efetuar o resgate.
 * @property isRedeemed Indicador informando se a recompensa já foi resgatada pelo usuário.
 */
data class LoyaltyReward(
    val id: String,
    val title: String,
    val pointsRequired: Int,
    val isRedeemed: Boolean = false
)

/**
 * Estado imutável da tela do programa de fidelidade e recompensas.
 *
 * @property title Título principal exibido na tela.
 * @property currentPoints Saldo total de pontos válidos do cliente.
 * @property currentCashback Saldo em moeda corrente acumulado via cashback.
 * @property tier Nível atual no programa de fidelidade.
 * @property currentStamps Quantidade de carimbos preenchidos na cartela atual.
 * @property maxStamps Quantidade total de carimbos para completar uma cartela.
 * @property rewards Lista de recompensas disponíveis para resgate.
 * @property feedbackMessage Mensagem contextual de feedback após ações de carimbo ou resgate.
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
        LoyaltyReward("r2", "Sobremesa Grátis no próximo pedido", 500),
        LoyaltyReward("r3", "Frete Grátis em 3 pedidos", 750),
        LoyaltyReward("r4", "Voucher R$ 50 OFF especial VIP", 1500)
    ),
    val feedbackMessage: String? = null
)

/**
 * ViewModel responsável pelas regras de fidelidade, evolução de níveis e resgate de prêmios.
 */
@HiltViewModel
class LoyaltyViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(LoyaltyUiState())

    /**
     * Fluxo reativo contendo o estado atual do clube de fidelidade do usuário.
     */
    val uiState: StateFlow<LoyaltyUiState> = _uiState.asStateFlow()

    /**
     * Adiciona um carimbo à cartela, bonificando o usuário com 50 pontos e recalculando o nível.
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
                feedbackMessage = if (nextStamps == current.maxStamps) "Parabéns! Você completou a cartela de carimbos!" else "Carimbo adicionado com sucesso!"
            )
        }
    }

    /**
     * Efetua o resgate de uma recompensa específica debitando os pontos necessários do saldo.
     *
     * @param rewardId Identificador único da recompensa desejada.
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