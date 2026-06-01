package com.turkcell.data.repository

import com.turkcell.core.domain.event.Ticket
import com.turkcell.core.domain.event.TicketRepository
import com.turkcell.data.mapper.toDomain
import com.turkcell.data.remote.MeApi
import com.turkcell.data.util.runCatchingApi

class TicketRepositoryImpl(
    private val meApi: MeApi
) : TicketRepository {
    override suspend fun getMyTickets(): Result<List<Ticket>> =
        runCatchingApi { meApi.getMyTickets() }.map { list -> list.map { it.toDomain() } }

    override suspend fun getTicket(id: String): Result<Ticket> =
        runCatchingApi { meApi.getTicket(id) }.map { it.toDomain() }
}