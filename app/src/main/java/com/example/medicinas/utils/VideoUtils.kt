package com.example.medicinas.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri

object VideoUtils {
    /**
     * Extrae un fotograma (miniatura) del segundo 1 de un video.
     */
    fun obtenerMiniaturaDeVideo(context: Context, uri: Uri): Bitmap? {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            val bitmap = retriever.getFrameAtTime(1000000)
            retriever.release()
            bitmap
        } catch (e: Exception) {
            null
        }
    }
}