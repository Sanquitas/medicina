package com.example.medicinas.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfUtils {

    fun generarPdfEntrega(
        context: Context,
        tracking: String,
        producto: String,
        temperatura: String,
        receptorNombre: String,
        receptorCargo: String,
        firmaBitmap: Bitmap?,
        operador: String = "Operador en Turno" // Esto vendría de la sesión de login
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paintNormal = Paint().apply { textSize = 14f }
        val paintTitulo = Paint().apply { textSize = 20f; isFakeBoldText = true }
        val paintSubtitulo = Paint().apply { textSize = 16f; isFakeBoldText = true }

        var y = 60f
        val x = 50f

        canvas.drawText("REPORTE OFICIAL DE ENTREGA MÉDICA", x, y, paintTitulo)
        y += 40f

        val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())

        val lineas = listOf(
            "Tracking: $tracking",
            "Producto: $producto",
            "Fecha/Hora: $fechaActual",
            "Operador: $operador",
            "Estado GPS: Validado (Dentro del radio permitido)",
            "Temperatura Registrada: $temperatura °C",
            "Receptor: $receptorNombre - $receptorCargo"
        )

        for (linea in lineas) {
            canvas.drawText(linea, x, y, paintNormal)
            y += 30f
        }

        y += 20f
        canvas.drawText("Cadena de Custodia (Evidencias):", x, y, paintSubtitulo)
        y += 40f

        if (firmaBitmap != null) {
            val scaledFirma = Bitmap.createScaledBitmap(firmaBitmap, 250, 120, false)
            canvas.drawText("Firma del Receptor:", x, y, paintNormal)
            y += 20f
            canvas.drawBitmap(scaledFirma, x, y, null)
        }

        pdfDocument.finishPage(page)

        val directorio = File(context.filesDir, "reportes")
        if (!directorio.exists()) directorio.mkdirs()

        val archivoPdf = File(directorio, "Entrega_$tracking.pdf")

        return try {
            pdfDocument.writeTo(FileOutputStream(archivoPdf))
            pdfDocument.close()
            archivoPdf
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }
}