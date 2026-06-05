package com.turkcell.ticketapp.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView

@Composable
fun QrScannerComponent(
    modifier: Modifier = Modifier.fillMaxSize(),
    onQrCodeScanned: (String) -> Unit
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            CompoundBarcodeView(context).apply {
                decodeContinuous(object : BarcodeCallback {
                    override fun barcodeResult(result: BarcodeResult?) {
                        result?.text?.let { onQrCodeScanned(it) }
                    }
                })
                resume()
            }
        },
        onRelease = { view ->
            view.pause() // Ekran kapanırken veya alta alınırken kamerayı durdur
        }
    )
}