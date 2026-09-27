package com.example.medicinas.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.medicinas.ui.components.QrCameraPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EscaneoScreen(
    onBackClick: () -> Unit,
    onQrScanned: (String) -> Unit
) {
    val context = LocalContext.current

    // Verificamos si ya tenemos el permiso
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Lanzador para pedir el permiso al sistema operativo
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    // Al iniciar la pantalla, si no hay permiso, lo pedimos automáticamente
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Control para evitar que dispare 50 escaneos por segundo del mismo código
    var ultimoCodigoEscaneado by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escanear Paquete") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (hasCameraPermission) {
                // Si hay permiso, mostramos la cámara que creamos en los componentes
                QrCameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    onQrScanned = { codigo ->
                        if (codigo != ultimoCodigoEscaneado) {
                            ultimoCodigoEscaneado = codigo
                            onQrScanned(codigo) // Devolvemos el código (ej. TRK-VAC-0001) a la navegación
                        }
                    }
                )

                // Overlay estético para indicarle al usuario qué hacer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Text(
                        text = "Apunta la cámara al código QR\ndel Tracking ID del paquete.",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                // Si el usuario deniega el permiso, mostramos un botón para volver a pedirlo
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Se requiere permiso de cámara para escanear.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                        Text("Conceder Permiso")
                    }
                }
            }
        }
    }
}