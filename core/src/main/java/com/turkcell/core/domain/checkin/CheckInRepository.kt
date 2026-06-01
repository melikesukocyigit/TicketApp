package com.turkcell.core.domain.checkin

interface CheckInRepository {
    suspend fun scanTicket(ticketId: String): Result<Unit>
}