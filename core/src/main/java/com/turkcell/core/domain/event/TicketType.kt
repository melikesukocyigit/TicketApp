
package com.turkcell.core.domain.event

data class TicketType(
    val id: String,
    val name: String,
    val priceCents: Long,
    val capacity: Long,
    val soldCount: Long,
    val remaining: Long,

    )