package com.turkcell.core.domain.purchase


enum class PurchaseStatus {
    PENDING, PAID
}

data class PurchaseItem(
    val ticketTypeId: String,
    val quantity: Int,
    val priceCents: Long
)

data class Purchase(
    val id: String,
    val status: PurchaseStatus,
    val items: List<PurchaseItem>,
    val totalCents: Long
)