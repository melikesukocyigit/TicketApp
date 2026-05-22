
package com.turkcell.data.remote

import com.turkcell.data.dto.event.EventDto
import retrofit2.http.GET

interface EventApi {
    @GET("/events")
    suspend fun getEvents(): List<EventDto>
}
