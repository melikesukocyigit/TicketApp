package com.turkcell.ticketapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.turkcell.core.domain.event.TicketType
import com.turkcell.ticketapp.R
import com.turkcell.ticketapp.viewmodel.EventDetailViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    viewModel: EventDetailViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onNavigateToTickets: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.navigateToTickets) {
        if (state.navigateToTickets) {
            viewModel.onNavigatedToTickets()
            onNavigateToTickets()
        }
    }

    if (state.purchaseDialogVisible) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDialog() },
            title = { Text(stringResource(R.string.payment_confirm_title)) },
            text = { Text("Seçtiğiniz biletleri satın almak istediğinize emin misiniz? Toplam Tutar: ₺${String.format("%.2f", state.totalPriceCents / 100.0)}") },
            confirmButton = {
                Button(onClick = { viewModel.confirmPayment() }) {
                    if (state.isPurchaseLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text(stringResource(R.string.confirm_and_pay))
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDialog() }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.event_detail_title), style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text(stringResource(R.string.back), color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        bottomBar = {
            if (state.event != null) {
                BottomAppBar(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val totalTl = state.totalPriceCents / 100.0
                        Text(
                            text = "Toplam: ₺${String.format("%.2f", totalTl)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Button(
                            onClick = { viewModel.startPurchase() },
                            enabled = state.totalPriceCents > 0 && !state.isPurchaseLoading
                        ) {
                            if (state.isPurchaseLoading && !state.purchaseDialogVisible) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                            } else {
                                Text(stringResource(R.string.buy_button))
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.errorMessage != null) {
                Text(text = state.errorMessage!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center).padding(16.dp))
            } else if (state.event != null) {
                val event = state.event!!

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(text = event.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = event.description, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = " Tarih: ${event.startsAt}", style = MaterialTheme.typography.labelLarge)
                        Text(text = " Mekan: ${event.venue}", style = MaterialTheme.typography.labelLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = stringResource(R.string.ticket_types), style = MaterialTheme.typography.titleLarge)
                    }

                    items(event.ticketTypes) { ticketType ->
                        val currentCount = state.selectedTickets[ticketType.id] ?: 0
                        TicketTypeRow(
                            ticketType = ticketType,
                            currentCount = currentCount,
                            onCountChange = { newCount ->
                                viewModel.updateTicketCount(ticketType.id, newCount, ticketType.remaining)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TicketTypeRow(
    ticketType: TicketType,
    currentCount: Int,
    onCountChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = ticketType.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Kalan: ${ticketType.remaining} / ${ticketType.capacity}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val priceTl = ticketType.priceCents / 100.0
                Text(text = "₺${String.format("%.2f", priceTl)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                FilledTonalIconButton(
                    onClick = { onCountChange(currentCount - 1) },
                    enabled = currentCount > 0,
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("-", fontWeight = FontWeight.Bold)
                }

                Text(
                    text = currentCount.toString(),
                    modifier = Modifier.padding(horizontal = 12.dp),
                    style = MaterialTheme.typography.titleMedium
                )

                FilledTonalIconButton(
                    onClick = { onCountChange(currentCount + 1) },
                    enabled = currentCount < 20 && currentCount < ticketType.remaining.toInt(),
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("+", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}