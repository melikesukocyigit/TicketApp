package com.turkcell.core.domain

interface TicketRepository {
    suspend fun getEvents(): Result<List<Event>>
    suspend fun getMyTickets(): Result<List<Ticket>>
}