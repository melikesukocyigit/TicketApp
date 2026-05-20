package com.turkcell.data.remote

import com.turkcell.data.dto.EventDto
import com.turkcell.data.dto.TicketDto
import retrofit2.http.GET

interface TicketApi {
    @GET("events")
    suspend fun getEvents(): List<EventDto>

    @GET("me/tickets") // "Me" kategorisindeki biletlerim yolu
    suspend fun getMyTickets(): List<TicketDto>
}