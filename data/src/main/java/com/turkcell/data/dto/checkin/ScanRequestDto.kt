package com.turkcell.data.dto.checkin

import kotlinx.serialization.Serializable

@Serializable
data class ScanRequestDto(
    val qrCode: String
)