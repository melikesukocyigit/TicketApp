package com.turkcell.ticketapp.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import qrcode.QRCode
import qrcode.raw.ErrorCorrectionLevel

@Composable
fun QrCodeImage(
    content: String,
    modifier: Modifier = Modifier
) {
    val bitmap = remember(content) { generateQRBitmap(content) }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Bilet QR Kodu",
            modifier = modifier,
            filterQuality = FilterQuality.None
        )
    }
}

private fun generateQRBitmap(content: String): Bitmap? = runCatching {
    val png = QRCode.ofSquares()
        .withErrorCorrectionLevel(ErrorCorrectionLevel.HIGH)
        .withSize(20)
        .build(content)
        .renderToBytes()
    BitmapFactory.decodeByteArray(png, 0, png.size)
}.getOrNull()