package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TicketDetailUiState(
    val ticketId: String = "",
    val qrCode: String = "",
    val isLoading: Boolean = true
)

class TicketDetailViewModel : ViewModel() {

    private val _state = MutableStateFlow(TicketDetailUiState())
    val state: StateFlow<TicketDetailUiState> = _state.asStateFlow()

    fun loadTicketData(ticketId: String, qrCode: String) {
        if (_state.value.ticketId == ticketId) return

        _state.update {
            it.copy(
                ticketId = ticketId,
                qrCode = qrCode,
                isLoading = false
            )
        }
    }
}