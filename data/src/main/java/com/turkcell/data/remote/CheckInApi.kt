package com.turkcell.data.remote

import com.turkcell.data.dto.checkin.ScanRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface CheckInApi {
    @POST("/checkin/scan")
    suspend fun scanTicket(@Body request: ScanRequestDto)
}