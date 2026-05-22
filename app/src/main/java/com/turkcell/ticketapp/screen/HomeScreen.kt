package com.turkcell.ticketapp.screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            // statusBarsPadding buraya eklenerek sekmelerin saat/şarj çubuğunun altında kalması engellendi
            Column(modifier = Modifier.statusBarsPadding()) {

                // Üst Bar ve Çıkış Butonu
                TopAppBar(
                    title = { Text("TicketApp", style = MaterialTheme.typography.titleLarge) },
                    actions = {
                        androidx.compose.material3.TextButton(onClick = viewModel::logout) {
                            Text("Çıkış", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelLarge)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Sekmeler (TabRow)
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
                // Sekmeler arası yumuşak geçiş animasyonu
                Crossfade(targetState = state.selectedTab, label = "TabTransition") { currentTab ->
                    when (currentTab) {
                        HomeTab.EVENTS -> EventList(events = state.events)
                        HomeTab.TICKETS -> TicketList(tickets = state.tickets)
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
                        event.description?.let {
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
fun TicketList(tickets: List<Ticket>) {
    if (tickets.isEmpty()) {
        Text("Henüz satın alınmış biletiniz bulunmuyor.", style = MaterialTheme.typography.bodyLarge)
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tickets) { ticket ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "🎫 Bilet ID: ${ticket.id}", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Durum: ${ticket.status}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "QR Kodu: ${ticket.qrCode}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}