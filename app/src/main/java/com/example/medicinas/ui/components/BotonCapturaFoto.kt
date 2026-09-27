package com.example.medicinas.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun RowScope.BotonCapturaFoto(titulo: String, bitmap: Bitmap?, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f).padding(4.dp)) {
        Box(
            modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant).clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) Image(bitmap = bitmap.asImageBitmap(), contentDescription = titulo, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            else Icon(imageVector = Icons.Filled.CameraAlt, contentDescription = "Tomar foto", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = titulo, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
    }
}