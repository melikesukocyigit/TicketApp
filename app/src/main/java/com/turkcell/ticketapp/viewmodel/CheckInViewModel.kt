package com.turkcell.ticketapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.checkin.CheckInRepository
import com.turkcell.ticketapp.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CheckInUiState(
    val isLoading: Boolean = false,
    val scannedTicketId: String? = null,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class CheckInViewModel(
    private val checkInRepository: CheckInRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CheckInUiState())
    val state: StateFlow<CheckInUiState> = _state.asStateFlow()

    fun onQrScanned(ticketId: String) {
        Log.d("API_DEBUG", "API'ye gönderilen Bilet ID: $ticketId")

        if (_state.value.isLoading || _state.value.scannedTicketId == ticketId) return

        _state.update { it.copy(isLoading = true, scannedTicketId = ticketId, errorMessage = null, isSuccess = false) }

        viewModelScope.launch {
            checkInRepository.scanTicket(ticketId)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { error ->
                    Log.e("API_DEBUG", "API'den gelen tam hata: ${error.message}")
                    _state.update { it.copy(isLoading = false, errorMessage = error.toUserMessage()) }
                }
        }
    }

    fun resetScanner() {
        _state.update { CheckInUiState() }
    }
}