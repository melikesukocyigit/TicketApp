package com.turkcell.ticketapp.screen

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.turkcell.ticketapp.R
import com.turkcell.ticketapp.components.QrCodeImage
import com.turkcell.ticketapp.viewmodel.TicketDetailViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(
    ticketId: String,
    qrCode: String,
    viewModel: TicketDetailViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(ticketId, qrCode) {
        viewModel.loadTicketData(ticketId, qrCode)
    }

    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        val originalBrightness = window?.attributes?.screenBrightness

        window?.attributes = window?.attributes?.apply {
            screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        }

        onDispose {
            window?.attributes = window?.attributes?.apply {
                screenBrightness = originalBrightness ?: WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ticket_detail_title), style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text(stringResource(R.string.back), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.entrance_ticket),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 2.sp
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Box(
                            modifier = Modifier
                                .size(220.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            QrCodeImage(
                                content = state.qrCode,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                            thickness = 2.dp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = stringResource(R.string.ticket_id),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = state.ticketId,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}