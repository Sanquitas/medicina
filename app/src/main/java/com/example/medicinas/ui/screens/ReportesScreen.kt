package com.example.medicinas.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File

// NUEVA FUNCIÓN: Envía el archivo PDF de forma segura a un visor externo
fun abrirPdf(context: Context, archivo: File) {
    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            archivo
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No tienes una aplicación instalada para ver PDFs", Toast.LENGTH_LONG).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val filtros = listOf("Individual", "Por Producto", "Del Día", "De la Semana", "Del Mes", "Del Año")
    var filtroSeleccionado by remember { mutableStateOf(filtros[0]) }
    var archivosPdf by remember { mutableStateOf<List<File>>(emptyList()) }

    LaunchedEffect(Unit) {
        val directorio = File(context.filesDir, "reportes")
        if (directorio.exists()) {
            archivosPdf = directorio.listFiles { file -> file.extension == "pdf" }?.toList() ?: emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportes de Entrega") },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background)) {
            LazyRow(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filtros) { filtro ->
                    FilterChip(
                        selected = filtroSeleccionado == filtro,
                        onClick = { filtroSeleccionado = filtro },
                        label = { Text(filtro) }
                    )
                }
            }

            Text(text = "Documentos PDF Generados", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            if (archivosPdf.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay reportes generados aún.", color = Color.Gray)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(archivosPdf) { archivo ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                // NUEVO: Llamamos a la función al hacer clic
                                .clickable { abrirPdf(context, archivo) },
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.PictureAsPdf, contentDescription = "PDF", tint = Color.Red, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(text = archivo.name, fontWeight = FontWeight.Bold)
                                    Text(text = "Tamaño: ${archivo.length() / 1024} KB", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}