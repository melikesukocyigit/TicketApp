package com.turkcell.ticketapp.screen

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.turkcell.ticketapp.R
import com.turkcell.ticketapp.components.QrScannerComponent
import com.turkcell.ticketapp.viewmodel.StaffViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffScreen(
    viewModel: StaffViewModel = koinViewModel(),
    onLogoutClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var isCameraMode by remember { mutableStateOf(false) }

    // GALERİDEN SEÇME İŞLEMİ
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                var bitmap = BitmapFactory.decodeStream(inputStream)

                val maxDimension = 1000
                if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
                    val ratio = Math.min(maxDimension.toFloat() / bitmap.width, maxDimension.toFloat() / bitmap.height)
                    val width = Math.round(ratio * bitmap.width)
                    val height = Math.round(ratio * bitmap.height)
                    bitmap = android.graphics.Bitmap.createScaledBitmap(bitmap, width, height, true)
                }

                val intArray = IntArray(bitmap.width * bitmap.height)
                bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
                val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
                val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

                val result = MultiFormatReader().decode(binaryBitmap)
                val cleanTicketId = result.text.trim()

                viewModel.onQrScanned(cleanTicketId)

            } catch (e: Exception) {
                Log.e("GALLERY_SCAN", "QR okunamadı: ${e.message}")
            }
        }
    }

    // 1. İZİN KONTROLÜ
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    // 2. TIMEOUT KONTROLÜ
    var isScanningTimeout by remember { mutableStateOf(false) }

    LaunchedEffect(isCameraMode, state.isSuccess, state.errorMessage) {
        if (isCameraMode && !state.isSuccess && state.errorMessage == null) {
            isScanningTimeout = false
            delay(15000)
            isScanningTimeout = true
            isCameraMode = false
        }
    }

    // 3. SONUÇ VE UYARI POPUP'LARI
    if (state.isSuccess || state.errorMessage != null) {
        AlertDialog(
            onDismissRequest = {
                viewModel.resetScanner()
                isCameraMode = false
            },
            title = { Text(if (state.isSuccess) stringResource(R.string.success) else stringResource(R.string.invalid_ticket_title)) },
            text = { Text(if (state.isSuccess) stringResource(R.string.ticket_verified) else state.errorMessage!!) },
            confirmButton = {
                Button(onClick = {
                    viewModel.resetScanner()
                    isCameraMode = false
                }) {
                    Text("Tamam")
                }
            }
        )
    } else if (isScanningTimeout) {
        AlertDialog(
            onDismissRequest = {
                isScanningTimeout = false
                viewModel.resetScanner()
            },
            title = { Text("Zaman Aşımı") },
            text = { Text("15 saniye boyunca QR kod okutulmadı. Kamerayı kapattık.") },
            confirmButton = {
                Button(onClick = {
                    isScanningTimeout = false
                    viewModel.resetScanner()
                }) {
                    Text("Anladım")
                }
            }
        )
    }

    // 4. EKRAN TASARIMI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personel Paneli") },
                actions = {
                    TextButton(onClick = onLogoutClick) {
                        Text(stringResource(R.string.logout), color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // DURUM 1: KAMERA AÇIK
            if (isCameraMode) {
                if (hasCameraPermission) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        QrScannerComponent(
                            modifier = Modifier.fillMaxSize(),
                            onQrCodeScanned = { qrCode ->
                                viewModel.onQrScanned(qrCode)
                            }
                        )

                        Button(
                            onClick = { isCameraMode = false },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black.copy(alpha = 0.6f)
                            )
                        ) {
                            Text("Kapat", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        if (state.isLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color.White)
                            }
                        }
                    }
                }
            }
            // DURUM 2: ANA MENÜ (Kamera Kapalı)
            else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            if (hasCameraPermission) {
                                isCameraMode = true
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Kamera İle QR Oku", fontSize = MaterialTheme.typography.titleMedium.fontSize, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Galeriden Seçerek Oku", fontSize = MaterialTheme.typography.titleMedium.fontSize, fontWeight = FontWeight.Bold)
                    }

                    if (state.isLoading) {
                        Spacer(modifier = Modifier.height(24.dp))
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}