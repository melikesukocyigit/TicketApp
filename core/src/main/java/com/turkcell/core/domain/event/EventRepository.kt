package com.turkcell.core.domain.event

interface EventRepository {
    suspend fun getEvents(): Result<List<Event>>
    suspend fun getMyTickets(): Result<List<Ticket>>
}