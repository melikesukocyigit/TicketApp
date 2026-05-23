package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.auth.AuthRepository
import com.turkcell.core.domain.event.Event
import com.turkcell.core.domain.event.EventRepository
import com.turkcell.core.domain.event.Ticket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class HomeTab {
    EVENTS, TICKETS
}

data class HomeUiState(
    val events: List<Event> = emptyList(),
    val tickets: List<Ticket> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedTab: HomeTab = HomeTab.EVENTS
)

class HomeViewModel(
    private val eventRepository: EventRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        fetchEvents()
        fetchMyTickets()
    }

    fun onTabSelected(tab: HomeTab) {
        _state.update { it.copy(selectedTab = tab) }
    }

    fun consumeError() = _state.update { it.copy(errorMessage = null) }

    private fun fetchEvents() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            eventRepository.getEvents()
                .onSuccess { data ->
                    _state.update { it.copy(events = data, isLoading = false) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.message ?: "Etkinlikler yüklenemedi") }
                }
        }
    }

    private fun fetchMyTickets() {
        viewModelScope.launch {
            eventRepository.getMyTickets()
                .onSuccess { data ->
                    android.util.Log.d("BiletTesti", "Sunucudan gelen bilet sayısı: ${data.size}")
                    _state.update { it.copy(tickets = data) }
                }
                .onFailure { error ->
                    android.util.Log.e("BiletTesti", "Bilet çekerken hata: ", error)
                    _state.update { it.copy(errorMessage = "Bilet Hatası: ${error.message}") }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}