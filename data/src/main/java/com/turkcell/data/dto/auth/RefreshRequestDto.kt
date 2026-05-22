package com.turkcell.data.dto.auth

import kotlinx.serialization.Serializable

// {"refreshToken":"abc"}
@Serializable
data class RefreshRequestDto(val refreshToken: String)