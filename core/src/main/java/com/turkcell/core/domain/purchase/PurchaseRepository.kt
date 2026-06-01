package com.turkcell.core.domain.purchase


interface PurchaseRepository {
    // Map<String, Int> -> ticketTypeId ve adet sayısını tutar
    suspend fun createPurchase(items: Map<String, Int>): Result<Purchase>
    suspend fun pay(purchaseId: String): Result<Unit>
    suspend fun getPurchase(purchaseId: String): Result<Purchase>

    suspend fun getMyPurchases(): Result<List<Purchase>>
}