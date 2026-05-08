package com.turkcell.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequestDto(val refreshToken: String)

// {"refreshToken":"abc"}