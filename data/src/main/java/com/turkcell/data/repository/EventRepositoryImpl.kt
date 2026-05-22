package com.turkcell.data.repository

import com.turkcell.core.domain.event.Event
import com.turkcell.core.domain.event.EventRepository
import com.turkcell.data.mapper.toDomain
import com.turkcell.data.remote.EventApi
import com.turkcell.data.util.runCatchingApi

class EventRepositoryImpl(
    private val eventApi: EventApi
) : EventRepository {
    override suspend fun getEvents(): Result<List<Event>> = runCatchingApi { eventApi.getEvents() }.map { list -> list.map { it.toDomain() }}
}