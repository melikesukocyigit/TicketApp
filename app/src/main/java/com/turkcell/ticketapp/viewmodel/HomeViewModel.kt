package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.auth.AuthRepository
import com.turkcell.core.domain.event.Event
import com.turkcell.core.domain.event.EventRepository
import com.turkcell.core.domain.event.Ticket
import com.turkcell.core.domain.event.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class HomeTab {
    EVENTS, TICKETS
}
data class HomeUiState(
    val selectedTab: HomeTab = HomeTab.EVENTS,

    val events: List<Event> = emptyList(),
    val isEventsLoading: Boolean = false,
    val isEventsRefreshing: Boolean = false,
    val eventsError: String? = null,

    // Biletler için State
    val tickets: List<Ticket> = emptyList(),
    val isTicketsLoading: Boolean = false,
    val isTicketsRefreshing: Boolean = false,
    val ticketsError: String? = null
)

class HomeViewModel(
    private val eventRepository: EventRepository,
    private val authRepository: AuthRepository,
    private val ticketRepository: TicketRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        _state.update { it.copy(isEventsLoading = true, isTicketsLoading = true) }
        fetchEvents()
        fetchMyTickets()
    }

    fun onTabSelected(tab: HomeTab) {
        _state.update { it.copy(selectedTab = tab) }
    }

    // ETKİNLİK İŞLEMLERİ

    fun refreshEvents() {
        if (_state.value.isEventsRefreshing) return

        _state.update { it.copy(isEventsRefreshing = true, eventsError = null) }
        fetchEvents()
    }

    private fun fetchEvents() {
        viewModelScope.launch {
            eventRepository.getEvents()
                .onSuccess { data ->
                    _state.update {
                        it.copy(events = data, isEventsLoading = false, isEventsRefreshing = false, eventsError = null)
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(isEventsLoading = false, isEventsRefreshing = false, eventsError = error.message ?: "Etkinlikler yüklenemedi.")
                    }
                }
        }
    }

    //  BİLET İŞLEMLERİ

    fun refreshTickets() {
        if (_state.value.isTicketsRefreshing) return

        _state.update { it.copy(isTicketsRefreshing = true, ticketsError = null) }
        fetchMyTickets()
    }

    private fun fetchMyTickets() {
        viewModelScope.launch {
            ticketRepository.getMyTickets()
                .onSuccess { data ->
                    _state.update {
                        it.copy(tickets = data, isTicketsLoading = false, isTicketsRefreshing = false, ticketsError = null)
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(isTicketsLoading = false, isTicketsRefreshing = false, ticketsError = "Bilet Hatası: ${error.message}")
                    }
                }
        }
    }


    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }


}