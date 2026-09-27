package com.example.medicinas.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

// 1. Clase nativa para dibujar
class SignaturePadView(context: Context) : View(context) {
    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 8f
        isAntiAlias = true
    }
    private var path = Path()
    var tieneTrazos = false
        private set

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> { path.moveTo(x, y); tieneTrazos = true }
            MotionEvent.ACTION_MOVE -> path.lineTo(x, y)
        }
        invalidate()
        return true
    }

    fun limpiar() {
        path.reset()
        tieneTrazos = false
        invalidate()
    }

    fun obtenerFirmaBitmap(): Bitmap? {
        if (!tieneTrazos) return null
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE) // Fondo blanco para que no sea transparente
        draw(canvas)
        return bitmap
    }
}

// 2. Envoltorio de Jetpack Compose
@Composable
fun LienzoFirma(
    modifier: Modifier = Modifier,
    onViewCreated: (SignaturePadView) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(androidx.compose.ui.graphics.Color.White)
            .border(1.dp, androidx.compose.ui.graphics.Color.Gray, RoundedCornerShape(8.dp))
    ) {
        AndroidView(
            factory = { context ->
                SignaturePadView(context).apply {
                    onViewCreated(this)
                }
            },
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )
    }
}