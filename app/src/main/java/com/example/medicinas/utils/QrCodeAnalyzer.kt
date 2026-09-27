package com.example.medicinas.utils

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class QrCodeAnalyzer(
    private val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

    // NUEVO: Le decimos a ML Kit que SOLO busque Códigos QR. Esto multiplica la velocidad y precisión.
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { valor ->
                            onQrCodeScanned(valor)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Si algo falla a nivel interno, lo imprimimos para saber qué pasa
                    Log.e("QrCodeAnalyzer", "Error en ML Kit: ", e)
                }
                .addOnCompleteListener {
                    imageProxy.close() // Cerramos el frame para que no se congele la cámara
                }
        } else {
            imageProxy.close()
        }
    }
}