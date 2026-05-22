package com.turkcell.data.dto.auth

import kotlinx.serialization.Serializable

// {
//  "user": {
//    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
//    "email": "string",
//    "role": "USER"
//  },
//  "accessToken": "string",
//  "refreshToken": "string"
//}
@Serializable
data class TokenPairDto(val user: UserDto, val accessToken:String, val refreshToken: String)