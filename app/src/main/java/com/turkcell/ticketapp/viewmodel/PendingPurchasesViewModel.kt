package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.purchase.Purchase
import com.turkcell.core.domain.purchase.PurchaseRepository
import com.turkcell.core.domain.purchase.PurchaseStatus
import com.turkcell.ticketapp.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PendingPurchasesUiState(
    val isLoading: Boolean = true,
    val isPaymentLoading: Boolean = false,
    val errorMessage: String? = null,
    val pendingPurchases: List<Purchase> = emptyList(),
    val purchaseDialogVisible: Boolean = false,
    val selectedPurchaseId: String? = null,
    val paymentSuccessful: Boolean = false
)

class PendingPurchasesViewModel(
    private val purchaseRepository: PurchaseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PendingPurchasesUiState())
    val state: StateFlow<PendingPurchasesUiState> = _state.asStateFlow()

    init {
        loadPurchases()
    }

    private fun loadPurchases() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            purchaseRepository.getMyPurchases()
                .onSuccess { allPurchases ->
                    val pending = allPurchases.filter { it.status == PurchaseStatus.PENDING }
                    _state.update { it.copy(isLoading = false, pendingPurchases = pending) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.toUserMessage()) }
                }
        }
    }

    fun showPaymentDialog(purchaseId: String) {
        _state.update { it.copy(purchaseDialogVisible = true, selectedPurchaseId = purchaseId) }
    }

    fun dismissDialog() {
        _state.update { it.copy(purchaseDialogVisible = false, selectedPurchaseId = null) }
    }

    fun confirmPayment() {
        val purchaseId = _state.value.selectedPurchaseId ?: return
        _state.update { it.copy(isPaymentLoading = true, errorMessage = null) }

        viewModelScope.launch {
            purchaseRepository.pay(purchaseId)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isPaymentLoading = false,
                            purchaseDialogVisible = false,
                            paymentSuccessful = true
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isPaymentLoading = false, errorMessage = error.toUserMessage()) }
                }
        }
    }

    fun onPaymentSuccessHandled() {
        _state.update { it.copy(paymentSuccessful = false) }
        loadPurchases()
    }
}