package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.event.Event
import com.turkcell.core.domain.event.EventRepository
import com.turkcell.core.domain.purchase.PurchaseRepository
import com.turkcell.ticketapp.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EventDetailUiState(
    val isLoading: Boolean = true,
    val isPurchaseLoading: Boolean = false,
    val errorMessage: String? = null,
    val event: Event? = null,
    val selectedTickets: Map<String, Int> = emptyMap(),

    val purchaseDialogVisible: Boolean = false,
    val pendingPurchaseId: String? = null,
    val navigateToTickets: Boolean = false
) {
    val totalPriceCents: Long
        get() = event?.ticketTypes?.sumOf { ticketType ->
            (selectedTickets[ticketType.id] ?: 0) * ticketType.priceCents
        } ?: 0L
}

class EventDetailViewModel(
    private val eventRepository: EventRepository,
    private val purchaseRepository: PurchaseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String = checkNotNull(savedStateHandle["id"])

    private val _state = MutableStateFlow(EventDetailUiState())
    val state: StateFlow<EventDetailUiState> = _state.asStateFlow()

    init {
        loadEvent()
    }

    fun loadEvent() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            eventRepository.getEvent(eventId)
                .onSuccess { event ->
                    _state.update { it.copy(isLoading = false, event = event) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.toUserMessage()) }
                }
        }
    }

    fun updateTicketCount(ticketTypeId: String, newCount: Int, remaining: Long) {
        val maxAllowed = minOf(20, remaining.toInt())
        val validCount = newCount.coerceIn(0, maxAllowed)

        _state.update { currentState ->
            val updatedSelection = currentState.selectedTickets.toMutableMap()
            updatedSelection[ticketTypeId] = validCount
            currentState.copy(selectedTickets = updatedSelection)
        }
    }

    // --- YENİ: SATIN ALIM (PURCHASE) AKIŞI ---

    fun startPurchase() {
        val itemsToBuy = _state.value.selectedTickets.filterValues { it > 0 }
        if (itemsToBuy.isEmpty()) return

        _state.update { it.copy(isPurchaseLoading = true, errorMessage = null) }

        viewModelScope.launch {
            purchaseRepository.createPurchase(itemsToBuy)
                .onSuccess { purchase ->
                    _state.update {
                        it.copy(
                            isPurchaseLoading = false,
                            pendingPurchaseId = purchase.id,
                            purchaseDialogVisible = true
                        )
                    }
                }
                .onFailure { error ->
                    val msg = error.toUserMessage()
                    _state.update { it.copy(isPurchaseLoading = false, errorMessage = msg) }

                    if (msg.contains("Stok yetersiz")) {
                        loadEvent()
                    }
                }
        }
    }

    fun confirmPayment() {
        val purchaseId = _state.value.pendingPurchaseId ?: return
        _state.update { it.copy(isPurchaseLoading = true, errorMessage = null) }

        viewModelScope.launch {
            purchaseRepository.pay(purchaseId)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isPurchaseLoading = false,
                            purchaseDialogVisible = false,
                            navigateToTickets = true
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isPurchaseLoading = false, errorMessage = error.toUserMessage()) }
                }
        }
    }

    fun dismissDialog() {
        _state.update { it.copy(purchaseDialogVisible = false, pendingPurchaseId = null) }
    }

    fun onNavigatedToTickets() {
        _state.update { it.copy(navigateToTickets = false) }
    }
}