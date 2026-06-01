package com.turkcell.ticketapp.util

import android.graphics.ImageFormat
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer

class QrCodeAnalyzer(
    private val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val reader = MultiFormatReader()

    override fun analyze(image: ImageProxy) {
        if (image.format in listOf(ImageFormat.YUV_420_888, ImageFormat.YUV_422_888, ImageFormat.YUV_444_888)) {
            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()

            val source = PlanarYUVLuminanceSource(
                data, image.width, image.height, 0, 0, image.width, image.height, false
            )
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

            try {
                val result = reader.decode(binaryBitmap)
                Log.d("TICKET_APP_SCAN", "QR Başarıyla Okundu: ${result.text}")
                onQrCodeScanned(result.text)
            } catch (e: Exception) {
                Log.e("TICKET_APP_SCAN", "QR okuma sırasında hata oluştu: ${e.message}")
            } finally {
                image.close()
            }
        } else {
            image.close()
        }
    }

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        val data = ByteArray(remaining())
        get(data)
        return data
    }
}