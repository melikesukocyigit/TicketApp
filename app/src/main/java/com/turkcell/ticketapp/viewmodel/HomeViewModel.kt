package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.AuthRepository
import com.turkcell.core.domain.Event
import com.turkcell.core.domain.Ticket
import com.turkcell.core.domain.TicketRepository
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
    private val ticketRepository: TicketRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        fetchEvents()
        fetchMyTickets()
    }

    // Kullanıcı sekmeler arası geçiş yaptığında State'i güncelle
    fun onTabSelected(tab: HomeTab) {
        _state.update { it.copy(selectedTab = tab) }
    }

    fun consumeError() = _state.update { it.copy(errorMessage = null) }

    private fun fetchEvents() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            ticketRepository.getEvents()
                .onSuccess { data ->
                    _state.update { it.copy(events = data, isLoading = false) }
                }
                .onFailure { error ->
                    // LoginViewModel'de yazdığımız toUserMessage() fonksiyonunu burada da kullanıyoruz
                    _state.update { it.copy(isLoading = false, errorMessage = error.toUserMessage()) }
                }
        }
    }

    private fun fetchMyTickets() {
        viewModelScope.launch {
            ticketRepository.getMyTickets()
                .onSuccess { data ->
                    _state.update { it.copy(tickets = data) }
                }
                .onFailure { error ->
                    _state.update { it.copy(errorMessage = error.toUserMessage()) }
                }
        }
    }
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}