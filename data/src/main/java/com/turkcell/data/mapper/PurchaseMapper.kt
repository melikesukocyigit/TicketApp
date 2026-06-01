package com.turkcell.data.mapper

import com.turkcell.core.domain.purchase.Purchase
import com.turkcell.core.domain.purchase.PurchaseItem
import com.turkcell.core.domain.purchase.PurchaseStatus
import com.turkcell.data.dto.purchase.PurchaseDto

fun PurchaseDto.toDomain() = Purchase(
    id = id,
    status = try { PurchaseStatus.valueOf(status) } catch (e: Exception) { PurchaseStatus.PENDING },
    totalCents = totalCents,
    items = items.map { PurchaseItem(it.ticketTypeId, it.quantity, it.priceCents) }
)