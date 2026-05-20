package com.turkcell.data.repository

import com.turkcell.core.domain.Event
import com.turkcell.core.domain.Ticket
import com.turkcell.core.domain.TicketRepository
import com.turkcell.data.remote.TicketApi
import com.turkcell.data.util.runCatchingApi

class TicketRepositoryImpl(
    private val ticketApi: TicketApi
) : TicketRepository {

    override suspend fun getEvents(): Result<List<Event>> = runCatchingApi {
        ticketApi.getEvents().map { dto ->
            Event(
                id = dto.id,
                name = dto.name,
                description = dto.description,
                venue = dto.venue,
                startsAt = dto.startsAt,
                endsAt = dto.endsAt
            )
        }
    }

    override suspend fun getMyTickets(): Result<List<Ticket>> = runCatchingApi {
        ticketApi.getMyTickets().map { dto ->
            Ticket(
                id = dto.id,
                qrCode = dto.qrCode,
                status = dto.status,
                ticketTypeId = dto.ticketTypeId
            )
        }
    }
}