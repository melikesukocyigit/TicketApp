package com.turkcell.data.dto.auth

import kotlinx.serialization.Serializable

//  "user": {
//    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
//    "email": "string",
//    "role": "USER"
//  },
@Serializable
data class UserDto(val id:String, val email:String, val role: String)