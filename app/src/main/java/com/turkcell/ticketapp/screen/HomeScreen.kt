package com.turkcell.ticketapp.screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.turkcell.core.domain.event.Event
import com.turkcell.core.domain.event.Ticket
import com.turkcell.ticketapp.R
import com.turkcell.ticketapp.components.QrCodeImage
import com.turkcell.ticketapp.viewmodel.HomeTab
import com.turkcell.ticketapp.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    // DÜZELTME 1: Artık (String, String) yani 2 parametre bekliyoruz
    onTicketClick: (String, String) -> Unit = { _, _ -> },
    onEventClick: (String) -> Unit = {},
    onPendingPurchasesClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge) },
                    actions = {
                        TextButton(onClick = onPendingPurchasesClick) {
                            Text(stringResource(R.string.pending_tab), color = MaterialTheme.colorScheme.primary)
                        }
                        TextButton(onClick = viewModel::logout) {
                            Text(stringResource(R.string.logout), color = MaterialTheme.colorScheme.error)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
                TabRow(
                    selectedTabIndex = state.selectedTab.ordinal,
                    containerColor = MaterialTheme.colorScheme.surface,
                ) {
                    Tab(
                        selected = state.selectedTab == HomeTab.EVENTS,
                        onClick = { viewModel.onTabSelected(HomeTab.EVENTS) },
                        text = { Text(stringResource(R.string.tab_events), style = MaterialTheme.typography.titleMedium) },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Tab(
                        selected = state.selectedTab == HomeTab.TICKETS,
                        onClick = { viewModel.onTabSelected(HomeTab.TICKETS) },
                        text = { Text(stringResource(R.string.tab_my_tickets), style = MaterialTheme.typography.titleMedium) },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Crossfade(targetState = state.selectedTab, label = "TabTransition") { currentTab ->
                when (currentTab) {
                    HomeTab.EVENTS -> EventList(
                        events = state.events,
                        isLoading = state.isEventsLoading,
                        isRefreshing = state.isEventsRefreshing,
                        error = state.eventsError,
                        onRefresh = viewModel::refreshEvents,
                        onEventClick = onEventClick
                    )
                    HomeTab.TICKETS -> TicketList(
                        tickets = state.tickets,
                        events = state.events,
                        isLoading = state.isTicketsLoading,
                        isRefreshing = state.isTicketsRefreshing,
                        error = state.ticketsError,
                        onRefresh = viewModel::refreshTickets,
                        onTicketClick = onTicketClick // Yeni halini aşağıya yolladık
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventList(
    events: List<Event>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    error: String?,
    onRefresh: () -> Unit,
    onEventClick: (String) -> Unit = {}
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = error, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                }
            }
            events.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_events), style = MaterialTheme.typography.bodyLarge)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(events) { event ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onEventClick(event.id) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = event.name, style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.height(6.dp))
                                event.description.let {
                                    Text(text = it, style = MaterialTheme.typography.bodyMedium)
                                    Spacer(modifier = Modifier.height(6.dp))
                                }
                                Text(text = "Mekan: ${event.venue}", style = MaterialTheme.typography.labelLarge)
                                Text(text = "Başlangıç: ${event.startsAt}", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketList(
    tickets: List<Ticket>,
    events: List<Event>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    error: String?,
    onRefresh: () -> Unit,
    onTicketClick: (String, String) -> Unit = { _, _ -> }
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = error, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                }
            }
            tickets.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_tickets), style = MaterialTheme.typography.bodyLarge)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tickets) { ticket ->
                        val relatedEvent = events.find { event ->
                            event.ticketTypes.any { it.id == ticket.ticketTypeId }
                        }
                        val ticketType = relatedEvent?.ticketTypes?.find { it.id == ticket.ticketTypeId }

                        val eventName = relatedEvent?.name ?: stringResource(R.string.unknown_event)
                        val ticketName = ticketType?.name ?: "Standart Bilet"

                        val isStatusValid = ticket.status == "VALID"
                        val statusColor = if (isStatusValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clickable { onTicketClick(ticket.id, ticket.qrCode) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.fillMaxHeight().width(6.dp).background(statusColor))
                                Column(
                                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(text = eventName.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = ticketName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier.background(color = statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = if (isStatusValid) stringResource(R.string.ticket_valid) else stringResource(R.string.ticket_invalid),
                                            color = statusColor,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier.padding(end = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    QrCodeImage(
                                        content = ticket.qrCode,
                                        modifier = Modifier.size(90.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}