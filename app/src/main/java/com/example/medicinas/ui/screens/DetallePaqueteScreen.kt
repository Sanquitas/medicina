package com.example.medicinas.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.medicinas.ui.components.BotonCapturaFoto
import com.example.medicinas.ui.components.OpcionValidacion
import com.example.medicinas.ui.components.VideoPlayerDialog
import com.example.medicinas.utils.VideoUtils
import com.example.medicinas.viewmodel.DetalleViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePaqueteScreen(
    codigoTracking: String,
    viewModel: DetalleViewModel,
    onBackClick: () -> Unit,
    onConfirmarEntrega: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(codigoTracking) { viewModel.cargarPaquete(codigoTracking) }

    val cargando by viewModel.cargando.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()
    val paquete by viewModel.paquete.collectAsState()
    val distanciaActual by viewModel.distanciaActual.collectAsState()
    val estaEnUbicacion by viewModel.estaEnUbicacion.collectAsState()
    val temperatura by viewModel.temperatura.collectAsState()
    val checklist by viewModel.checklist.collectAsState()
    val fotoPaquete by viewModel.fotoPaquete.collectAsState()
    val fotoEtiqueta by viewModel.fotoEtiqueta.collectAsState()
    val fotoAdicional by viewModel.fotoAdicional.collectAsState()
    val videoUri by viewModel.videoUri.collectAsState()
    val videoMiniatura by viewModel.videoMiniatura.collectAsState()

    var mostrarReproductor by remember { mutableStateOf(false) }
    var buscandoGPS by remember { mutableStateOf(false) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) viewModel.guardarFoto(bitmap)
    }

    val videoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val miniatura = VideoUtils.obtenerMiniaturaDeVideo(context, uri)
                viewModel.guardarVideo(uri, miniatura)
            }
        }
    }

    val obtenerUbicacion = {
        buscandoGPS = true
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener { location: Location? ->
                if (location != null) viewModel.verificarUbicacion(location.latitude, location.longitude)
                buscandoGPS = false
            }.addOnFailureListener { buscandoGPS = false }
        } catch (e: SecurityException) { buscandoGPS = false }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) obtenerUbicacion()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Validación del Paquete") },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar") } }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (cargando) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (mensajeError != null || paquete == null) {
                Column(modifier = Modifier.align(Alignment.Center).padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Filled.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = mensajeError ?: "Paquete no encontrado.", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onBackClick) { Text("Regresar") }
                }
            } else {
                val mostrarErrorTemp = temperatura.isNotEmpty() && !viewModel.esTemperaturaValida()

                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Trk: ${paquete!!.tracking}", fontWeight = FontWeight.Bold)
                            Text("Temp. Mín: ${paquete!!.tempMin}°C | Máx: ${paquete!!.tempMax}°C")
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Geolocalización", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), colors = CardDefaults.cardColors(containerColor = if (estaEnUbicacion) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant), border = if (estaEnUbicacion) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4CAF50)) else null) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Destino: Hospital General", fontWeight = FontWeight.Bold)
                            if (distanciaActual != null) Text("A ${"%.1f".format(distanciaActual)} metros", color = if (estaEnUbicacion) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) obtenerUbicacion()
                                    else permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                                }, enabled = !buscandoGPS
                            ) {
                                Icon(Icons.Filled.LocationOn, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text(if (buscandoGPS) "Buscando..." else "Verificar Ubicación")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Temperatura", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = temperatura, onValueChange = { viewModel.actualizarTemperatura(it) }, label = { Text("Temperatura (°C)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), isError = mostrarErrorTemp, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Validación de etiqueta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            OpcionValidacion("Número de lote coincide", checklist.lote) { viewModel.actualizarChecklist(checklist.copy(lote = it)) }
                            OpcionValidacion("Número de seguimiento coincide", checklist.seguimiento) { viewModel.actualizarChecklist(checklist.copy(seguimiento = it)) }
                            OpcionValidacion("Fecha de caducidad coincide", checklist.caducidad) { viewModel.actualizarChecklist(checklist.copy(caducidad = it)) }
                            OpcionValidacion("Etiqueta legible", checklist.legible) { viewModel.actualizarChecklist(checklist.copy(legible = it)) }
                            OpcionValidacion("Empaque sin daños", checklist.sinDanos) { viewModel.actualizarChecklist(checklist.copy(sinDanos = it)) }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Evidencia Fotográfica", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        BotonCapturaFoto("Paquete", fotoPaquete) { viewModel.prepararCapturaFoto(1); cameraLauncher.launch(null) }
                        BotonCapturaFoto("Etiqueta", fotoEtiqueta) { viewModel.prepararCapturaFoto(2); cameraLauncher.launch(null) }
                        BotonCapturaFoto("Adicional", fotoAdicional) { viewModel.prepararCapturaFoto(3); cameraLauncher.launch(null) }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Evidencia en Video", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            if (videoUri != null) {
                                Box(modifier = Modifier.size(200.dp, 140.dp).clip(RoundedCornerShape(8.dp)).background(Color.Black).clickable { mostrarReproductor = true }, contentAlignment = Alignment.Center) {
                                    if (videoMiniatura != null) { Image(bitmap = videoMiniatura!!.asImageBitmap(), contentDescription = "Miniatura", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop); Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f))) }
                                    Icon(Icons.Filled.PlayArrow, contentDescription = "Reproducir", tint = Color.White, modifier = Modifier.size(48.dp))
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedButton(onClick = { viewModel.eliminarVideo() }, colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Icon(Icons.Filled.Delete, contentDescription = "Eliminar"); Spacer(modifier = Modifier.width(8.dp)); Text("Eliminar y volver a grabar") }
                            } else {
                                Button(onClick = {
                                    val intent = android.content.Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE)
                                    intent.putExtra(android.provider.MediaStore.EXTRA_DURATION_LIMIT, 15)
                                    videoLauncher.launch(intent)
                                }) { Icon(Icons.Filled.Videocam, contentDescription = "Grabar"); Spacer(modifier = Modifier.width(8.dp)); Text("Grabar video (Max 15s)") }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = onConfirmarEntrega, modifier = Modifier.fillMaxWidth().height(50.dp), enabled = viewModel.puedeConfirmarEntrega()) { Text("Confirmar entrega") }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
    if (mostrarReproductor && videoUri != null) { VideoPlayerDialog(videoUri = videoUri!!, onDismiss = { mostrarReproductor = false }) }
}