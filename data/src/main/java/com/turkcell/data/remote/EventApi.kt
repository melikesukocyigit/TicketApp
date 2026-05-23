package com.turkcell.data.remote

import com.turkcell.data.dto.event.EventDto
import com.turkcell.data.dto.event.TicketDto
import retrofit2.http.GET

interface EventApi {
    @GET("/events")
    suspend fun getEvents(): List<EventDto>

    @GET("/me/tickets")
    suspend fun getMyTickets(): List<TicketDto>
}