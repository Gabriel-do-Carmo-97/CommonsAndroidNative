package br.com.wgc.loyalty.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

enum class LoyaltyTier(val label: String, val minPoints: Int, val benefit: String) {
    BRONZE("Bronze", 0, "1% de cashback em todas as compras"),
    SILVER("Prata", 500, "2.5% de cashback + cupom mensal"),
    GOLD("Ouro", 1500, "5% de cashback + frete grátis acima de R$ 50"),
    DIAMOND("Diamante", 3000, "10% de cashback + acesso antecipado a promoções")
}

data class LoyaltyReward(
    val id: String,
    val title: String,
    val pointsRequired: Int,
    val isRedeemed: Boolean = false
)

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

@HiltViewModel
class LoyaltyViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(LoyaltyUiState())
    val uiState: StateFlow<LoyaltyUiState> = _uiState.asStateFlow()

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
