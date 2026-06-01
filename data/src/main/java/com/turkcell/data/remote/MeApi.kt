package com.turkcell.data.remote

import com.turkcell.data.dto.event.TicketDto
import retrofit2.http.GET
import retrofit2.http.Path

interface MeApi {
    @GET("/me/tickets")
    suspend fun getMyTickets(): List<TicketDto>

    @GET("/me/tickets/{id}")
    suspend fun getTicket(@Path("id") id: String): TicketDto
}