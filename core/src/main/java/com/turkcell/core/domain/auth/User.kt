package com.turkcell.core.domain.auth

data class User(val id: String, val email:String, val role: UserRole) {}