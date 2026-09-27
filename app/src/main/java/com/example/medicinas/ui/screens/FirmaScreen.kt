package com.example.medicinas.ui.screens

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medicinas.ui.components.LienzoFirma
import com.example.medicinas.ui.components.SignaturePadView
import com.example.medicinas.utils.FirmaUtils
import com.example.medicinas.utils.PdfUtils
import com.example.medicinas.viewmodel.DetalleViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirmaScreen(codigoTracking: String, viewModel: DetalleViewModel, onBackClick: () -> Unit, onFinalizar: () -> Unit) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose { activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE) }
    }

    val paquete by viewModel.paquete.collectAsState()
    val temperatura by viewModel.temperatura.collectAsState()
    val receptorNombre by viewModel.receptorNombre.collectAsState()
    val receptorCargo by viewModel.receptorCargo.collectAsState()
    val rutaFirma by viewModel.rutaFirma.collectAsState()

    var signaturePad by remember { mutableStateOf<SignaturePadView?>(null) }
    var mensajeError by remember { mutableStateOf("") }
    var mostrarDialogoPdf by remember { mutableStateOf(false) }
    var archivoGenerado by remember { mutableStateOf<File?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Firma Digital") }, navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar") } }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Cadena de Custodia", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("Este apartado cuenta con protección contra grabaciones o capturas de pantalla.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 24.dp))
            LienzoFirma(onViewCreated = { signaturePad = it })
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = { signaturePad?.limpiar(); viewModel.guardarRutaFirma(""); mensajeError = "" }) { Icon(Icons.Filled.Clear, contentDescription = "Limpiar"); Spacer(modifier = Modifier.width(8.dp)); Text("Limpiar lienzo") }
                Button(onClick = {
                    val bitmap = signaturePad?.obtenerFirmaBitmap()
                    if (bitmap != null) {
                        val ruta = FirmaUtils.guardarFirmaOculta(context, bitmap, codigoTracking)
                        if (ruta != null) { viewModel.guardarRutaFirma(ruta); mensajeError = "" }
                    } else { mensajeError = "Debe realizar una firma antes de guardar." }
                }) { Text("Guardar Firma") }
            }
            if (mensajeError.isNotEmpty()) Text(text = mensajeError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
            if (rutaFirma != null && rutaFirma!!.isNotEmpty()) Text(text = "✓ Firma capturada y cifrada localmente.", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    // 1. Guardamos datos en BD y pasamos el estado a "Entregado"
                    viewModel.guardarEntregaEnBaseDeDatos(codigoTracking) // <-- CAMBIO AQUÍ

                    // 2. Generamos el PDF con la firma recién hecha
                    val bitmapFirma = signaturePad?.obtenerFirmaBitmap()
                    archivoGenerado = PdfUtils.generarPdfEntrega(
                        context = context,
                        tracking = codigoTracking,
                        producto = paquete?.producto ?: "Desconocido",
                        temperatura = temperatura,
                        receptorNombre = receptorNombre,
                        receptorCargo = receptorCargo,
                        firmaBitmap = bitmapFirma
                    )

                    // 3. Mostramos la alerta de descarga
                    mostrarDialogoPdf = true
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = viewModel.puedeFinalizarEntrega()
            ) { Text("Finalizar Entrega y Sincronizar") }
        }
    }
    if (mostrarDialogoPdf) {
        AlertDialog(
            onDismissRequest = { }, icon = { Icon(Icons.Filled.PictureAsPdf, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Entrega Finalizada") }, text = { Text("El reporte PDF se ha generado correctamente.\n\nArchivo: ${archivoGenerado?.name ?: "Error"}") },
            confirmButton = { Button(onClick = { mostrarDialogoPdf = false; onFinalizar() }) { Text("Aceptar y Salir") } }
        )
    }
}