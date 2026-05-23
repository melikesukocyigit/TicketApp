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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.turkcell.core.domain.event.Event
import com.turkcell.core.domain.event.Ticket
import com.turkcell.ticketapp.viewmodel.HomeTab
import com.turkcell.ticketapp.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onTicketClick: (String) -> Unit = {} // NAVİGASYON İÇİN GEREKEN PARAMETRE EKLENDİ
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                TopAppBar(
                    title = { Text("TicketApp", style = MaterialTheme.typography.titleLarge) },
                    actions = {
                        TextButton(onClick = viewModel::logout) {
                            Text("Çıkış", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelLarge)
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
                        text = { Text("Etkinlikler", style = MaterialTheme.typography.titleMedium) },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Tab(
                        selected = state.selectedTab == HomeTab.TICKETS,
                        onClick = { viewModel.onTabSelected(HomeTab.TICKETS) },
                        text = { Text("Biletlerim", style = MaterialTheme.typography.titleMedium) },
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
            if (state.isLoading) {
                CircularProgressIndicator()
            } else if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Crossfade(targetState = state.selectedTab, label = "TabTransition") { currentTab ->
                    when (currentTab) {
                        HomeTab.EVENTS -> EventList(events = state.events)
                        HomeTab.TICKETS -> TicketList(
                            tickets = state.tickets,
                            events = state.events, // İSİM EŞLEŞTİRMESİ İÇİN EKLENDİ
                            onTicketClick = onTicketClick // TIKLAMA BİLGİSİ EKLENDİ
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EventList(events: List<Event>) {
    if (events.isEmpty()) {
        Text("Gösterilecek etkinlik bulunamadı.", style = MaterialTheme.typography.bodyLarge)
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(events) { event ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                        Text(text = "📍 Mekan: ${event.venue}", style = MaterialTheme.typography.labelLarge)
                        Text(text = "📅 Başlangıç: ${event.startsAt}", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun TicketList(tickets: List<Ticket>, events: List<Event>, onTicketClick: (String) -> Unit = {}) {
    if (tickets.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Henüz satın alınmış biletiniz bulunmuyor.", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tickets) { ticket ->

                // Gerçek İsimleri Bulma İşlemi
                val relatedEvent = events.find { event ->
                    event.ticketTypes.any { it.id == ticket.ticketTypeId }
                }
                val ticketType = relatedEvent?.ticketTypes?.find { it.id == ticket.ticketTypeId }

                val eventName = relatedEvent?.name ?: "BİLİNMEYEN ETKİNLİK"
                val ticketName = ticketType?.name ?: "Standart Bilet"

                val isStatusValid = ticket.status == "VALID"
                val statusColor = if (isStatusValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(86.dp)
                        .clickable { onTicketClick(ticket.id) },
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
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(6.dp)
                                .background(statusColor)
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = eventName.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = ticketName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .background(
                                    color = statusColor.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isStatusValid) "Geçerli" else "Geçersiz",
                                color = statusColor,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}