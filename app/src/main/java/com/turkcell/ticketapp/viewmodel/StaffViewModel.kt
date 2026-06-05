package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.checkin.CheckInRepository
import com.turkcell.ticketapp.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StaffUiState(
    val isLoading: Boolean = false,
    val scannedTicketId: String? = null,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class StaffViewModel(
    private val checkInRepository: CheckInRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StaffUiState())
    val state: StateFlow<StaffUiState> = _state.asStateFlow()

    fun onQrScanned(ticketId: String) {
        android.util.Log.e("QR_TEST", "Cihazın Okuduğu Kod: $ticketId")
        if (_state.value.isLoading || _state.value.scannedTicketId == ticketId) return

        _state.update { it.copy(isLoading = true, scannedTicketId = ticketId, errorMessage = null, isSuccess = false) }

        viewModelScope.launch {
            checkInRepository.scanTicket(ticketId)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { error ->
                    android.util.Log.e("API_ERROR", "Sunucu ne dedi: ${error.message}")

                    _state.update { it.copy(isLoading = false, errorMessage = error.message) }

                }
        }
    }

    fun resetScanner() {
        _state.update { StaffUiState() }
    }
}