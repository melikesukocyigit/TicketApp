package com.turkcell.data.mapper

import com.turkcell.core.domain.event.Event
import com.turkcell.core.domain.event.Ticket
import com.turkcell.core.domain.event.TicketType
import com.turkcell.data.dto.event.EventDto
import com.turkcell.data.dto.event.TicketDto
import com.turkcell.data.dto.event.TicketTypeDto

internal fun EventDto.toDomain(): Event = Event(
    id = id,
    name = name,
    description = description,
    venue = place ?: "Mekan belirtilmemiş",
    startsAt = startsAt,
    endsAt = endsAt ?: "",
    ticketTypes = ticketTypes.map { it.toDomain() }
)

internal fun TicketTypeDto.toDomain(): TicketType = TicketType(
    id = id,
    name = name,
    priceCents = priceCents,
    capacity = capacity,
    soldCount = soldCount,
    remaining = remaining
)

internal fun TicketDto.toDomain(): Ticket = Ticket(
    id = id,
    qrCode = qrCode,
    status = status,
    ticketTypeId = ticketTypeId
)