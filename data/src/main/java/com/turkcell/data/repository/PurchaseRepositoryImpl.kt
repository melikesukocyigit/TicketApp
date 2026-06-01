package com.turkcell.data.repository

import com.turkcell.core.domain.purchase.Purchase
import com.turkcell.core.domain.purchase.PurchaseRepository
import com.turkcell.data.dto.purchase.CreatePurchaseRequestDto
import com.turkcell.data.dto.purchase.PurchaseItemRequestDto
import com.turkcell.data.mapper.toDomain
import com.turkcell.data.remote.PurchaseApi
import com.turkcell.data.util.runCatchingApi

class PurchaseRepositoryImpl(
    private val purchaseApi: PurchaseApi
) : PurchaseRepository {

    override suspend fun createPurchase(items: Map<String, Int>): Result<Purchase> = runCatchingApi {
        val requestItems = items.map { PurchaseItemRequestDto(it.key, it.value) }
        purchaseApi.createPurchase(CreatePurchaseRequestDto(requestItems)).toDomain()
    }

    override suspend fun pay(purchaseId: String): Result<Unit> = runCatchingApi {
        purchaseApi.pay(purchaseId)
    }

    override suspend fun getPurchase(purchaseId: String): Result<Purchase> = runCatchingApi {
        purchaseApi.getPurchase(purchaseId).toDomain()
    }

    override suspend fun getMyPurchases(): Result<List<Purchase>> = runCatchingApi {
        purchaseApi.getMyPurchases().map { it.toDomain() }
    }
}