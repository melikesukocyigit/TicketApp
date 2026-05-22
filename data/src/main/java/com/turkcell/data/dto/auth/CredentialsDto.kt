package com.turkcell.data.dto.auth

import kotlinx.serialization.Serializable

// /auth/login ve /auth/register'a atılan istek
@Serializable
data class CredentialsDto(val email: String, val password: String) {}