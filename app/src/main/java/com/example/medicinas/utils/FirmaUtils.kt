package com.example.medicinas.utils

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

object FirmaUtils {
    /**
     * Guarda la firma en el almacenamiento INTERNO PRIVADO de la app.
     * Esto garantiza que la imagen NO aparezca en la galería del teléfono.
     */
    fun guardarFirmaOculta(context: Context, bitmap: Bitmap, trackingId: String): String? {
        return try {
            // El directorio 'filesDir' es exclusivo y cifrado por el sistema operativo
            val archivoFirma = File(context.filesDir, "firma_segura_$trackingId.png")

            FileOutputStream(archivoFirma).use { salida ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, salida)
                salida.flush()
            }

            archivoFirma.absolutePath // Devolvemos la ruta local para guardarla en la BD
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}