package com.turkcell.data.remote

import com.turkcell.data.dto.purchase.CreatePurchaseRequestDto
import com.turkcell.data.dto.purchase.PurchaseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PurchaseApi {
    @POST("/purchases")
    suspend fun createPurchase(@Body request: CreatePurchaseRequestDto): PurchaseDto

    @POST("/purchases/{id}/pay")
    suspend fun pay(@Path("id") id: String)

    @GET("/purchases/{id}")
    suspend fun getPurchase(@Path("id") id: String): PurchaseDto

    @GET("/me/purchases")
    suspend fun getMyPurchases(): List<PurchaseDto>
}