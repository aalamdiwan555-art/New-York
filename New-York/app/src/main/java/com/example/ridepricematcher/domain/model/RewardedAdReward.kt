package com.example.ridepricematcher.domain.model

data class RewardedAdReward(
    val id: String,
    val userId: String,
    val provider: String,
    val providerRewardId: String,
    val rewardValue: Int,
    val createdAt: String = ""
)
