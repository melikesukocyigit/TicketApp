package com.turkcell.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class TicketDto(
    val id: String,
    val qrCode: String,
    val status: String,
    val ticketTypeId: String
)