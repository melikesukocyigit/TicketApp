package com.turkcell.data.repository

import com.turkcell.core.domain.checkin.CheckInRepository
import com.turkcell.data.dto.checkin.ScanRequestDto
import com.turkcell.data.remote.CheckInApi
import com.turkcell.data.util.runCatchingApi

class CheckInRepositoryImpl(
    private val checkInApi: CheckInApi
) : CheckInRepository {
    override suspend fun scanTicket(ticketId: String): Result<Unit> = runCatchingApi {
        checkInApi.scanTicket(ScanRequestDto(ticketId))
    }
}