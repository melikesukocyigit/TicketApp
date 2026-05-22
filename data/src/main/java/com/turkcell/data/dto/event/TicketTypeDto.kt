package com.turkcell.data.dto.event

import kotlinx.serialization.Serializable

@Serializable
data class TicketTypeDto(
    val id: String,
    val name: String,
    val priceCents: Long,
    val capacity: Long,
    val soldCount: Long,
    val remaining: Long,
)