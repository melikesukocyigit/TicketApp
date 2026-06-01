package com.turkcell.data.dto.purchase

import kotlinx.serialization.Serializable

@Serializable
data class CreatePurchaseRequestDto(
    val items: List<PurchaseItemRequestDto>
)

@Serializable
data class PurchaseItemRequestDto(
    val ticketTypeId: String,
    val quantity: Int
)

@Serializable
data class PurchaseDto(
    val id: String,
    val status: String,
    val totalCents: Long = 0L,
    val items: List<PurchaseItemDto> = emptyList()
)

@Serializable
data class PurchaseItemDto(
    val ticketTypeId: String,
    val quantity: Int,
    val priceCents: Long = 0L
)