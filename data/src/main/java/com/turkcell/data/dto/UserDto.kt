package com.turkcell.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(val id:String, val email:String, val role: String)

//  "user": {
//    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
//    "email": "string",
//    "role": "USER"
//  },