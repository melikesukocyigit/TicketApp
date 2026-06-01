package com.turkcell.ticketapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.turkcell.ticketapp.R
import com.turkcell.ticketapp.viewmodel.PendingPurchasesViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingPurchasesScreen(
    viewModel: PendingPurchasesViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.paymentSuccessful) {
        if (state.paymentSuccessful) {
            viewModel.onPaymentSuccessHandled()
        }
    }

    if (state.purchaseDialogVisible) {
        val purchaseToPay = state.pendingPurchases.find { it.id == state.selectedPurchaseId }
        val totalTl = (purchaseToPay?.totalCents ?: 0) / 100.0

        AlertDialog(
            onDismissRequest = viewModel::dismissDialog,
            title = { Text(stringResource(R.string.complete_payment_title)) },
            text = { Text("Yarım kalan işleminiz için toplam ₺${String.format("%.2f", totalTl)} ödemek istiyor musunuz?") },
            confirmButton = {
                Button(onClick = viewModel::confirmPayment) {
                    if (state.isPaymentLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text(stringResource(R.string.confirm_and_pay))
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDialog) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.pending_purchases_title), style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text(stringResource(R.string.back), color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else if (state.pendingPurchases.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_pending_purchases),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.pendingPurchases) { purchase ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "Sipariş: ${purchase.id.take(8)}...", style = MaterialTheme.typography.labelMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Tutar: ₺${String.format("%.2f", purchase.totalCents / 100.0)}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Button(onClick = { viewModel.showPaymentDialog(purchase.id) }) {
                                    Text(stringResource(R.string.pay_button))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}