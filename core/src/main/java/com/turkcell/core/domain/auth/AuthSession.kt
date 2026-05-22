package com.turkcell.core.domain.auth

import com.turkcell.core.domain.auth.User

data class AuthSession(val user: User, val accessToken: String, val refreshToken: String) {}