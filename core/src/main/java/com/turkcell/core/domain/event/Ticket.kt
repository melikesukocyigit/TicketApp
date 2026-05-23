package com.turkcell.core.domain.event

data class Ticket(
    val id: String,
    val qrCode: String,
    val status: String,
    val ticketTypeId: String
)