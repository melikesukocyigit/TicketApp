package com.turkcell.data.remote

import com.turkcell.data.dto.event.EventDto
import com.turkcell.data.dto.event.TicketDto
import retrofit2.http.GET
import retrofit2.http.Path

interface EventApi {
    @GET("/events")
    suspend fun getEvents(): List<EventDto>

    @GET("/me/tickets")
    suspend fun getMyTickets(): List<TicketDto>

    @GET("/events/{id}")
    suspend fun getEvent(@Path("id") id: String): EventDto
}