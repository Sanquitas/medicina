package com.example.medicinas.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medicinas.data.local.entities.PaqueteEntity
import com.example.medicinas.viewmodel.DashboardViewModel
import com.example.medicinas.viewmodel.EstadoPaquete
import com.example.medicinas.viewmodel.FiltroDashboard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    rol: String,
    onEscanearClick: () -> Unit,
    onReportesClick: () -> Unit,
    onAcercaDeClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val paquetes by viewModel.paquetesFiltrados.collectAsState()
    val filtroActual by viewModel.filtroActual.collectAsState()

    var paqueteSeleccionado by remember { mutableStateOf<PaqueteEntity?>(null) }

    // NUEVAS VARIABLES: Para el mensaje y el cuadro de diálogo
    val context = LocalContext.current
    var mostrarDialogoLogout by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Operador") },
                actions = {
                    IconButton(onClick = onAcercaDeClick) {
                        Icon(Icons.Filled.Info, contentDescription = "Acerca de")
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FloatingActionButton(
                    onClick = onReportesClick,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(Icons.Filled.ListAlt, contentDescription = "Reportes")
                }

                ExtendedFloatingActionButton(
                    onClick = onEscanearClick,
                    icon = { Icon(Icons.Filled.QrCodeScanner, contentDescription = "Escanear") },
                    text = { Text("Escanear paquete") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(
                text = "Bienvenido, $rol",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            // CAMBIO: Ahora el botón abre el diálogo en lugar de cerrar sesión de golpe
            TextButton(
                onClick = { mostrarDialogoLogout = true },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Icon(Icons.Filled.ExitToApp, contentDescription = "Cerrar sesión", tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar sesión", color = MaterialTheme.colorScheme.error)
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(FiltroDashboard.entries) { filtro ->
                    FilterChip(
                        selected = filtroActual == filtro,
                        onClick = { viewModel.cambiarFiltro(filtro) },
                        label = {
                            Text(filtro.name.lowercase().replaceFirstChar { it.uppercase() })
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(paquetes) { paquete ->
                    val estado = viewModel.evaluarEstadoPaquete(paquete.caducidad)
                    TarjetaPaquete(
                        paquete = paquete,
                        estado = estado,
                        onClick = { paqueteSeleccionado = paquete }
                    )
                }
            }
        }
    }

    // ==========================================
    // NUEVO: CUADRO DE DIÁLOGO PARA CERRAR SESIÓN
    // ==========================================
    if (mostrarDialogoLogout) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoLogout = false },
            title = { Text(text = "Cerrar sesión", fontWeight = FontWeight.Bold) },
            text = { Text("¿Deseas cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoLogout = false
                        // Mostramos el mensaje de despedida
                        Toast.makeText(context, "¡Hasta pronto! Sesión cerrada correctamente.", Toast.LENGTH_SHORT).show()
                        // Ejecutamos la navegación hacia el login
                        onLogoutClick()
                    }
                ) {
                    Text("Cerrar sesión", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoLogout = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    paqueteSeleccionado?.let { paquete ->
        DetallePaqueteDialog(
            paquete = paquete,
            estado = viewModel.evaluarEstadoPaquete(paquete.caducidad),
            onDismiss = { paqueteSeleccionado = null }
        )
    }
}

@Composable
fun DetallePaqueteDialog(
    paquete: PaqueteEntity,
    estado: EstadoPaquete,
    onDismiss: () -> Unit
) {
    var mostrarInstrucciones by remember { mutableStateOf(false) }

    val colorCaducidad = when (estado) {
        EstadoPaquete.NORMAL -> Color(0xFF4CAF50)
        EstadoPaquete.PROXIMO_A_CADUCAR -> Color(0xFFF57C00)
        EstadoPaquete.CADUCADO -> MaterialTheme.colorScheme.error
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Detalles del Paquete", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(text = "Trk: ${paquete.tracking}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text(text = "Producto: ${paquete.producto}", fontWeight = FontWeight.Bold)
                Text(text = "Tipo: ${paquete.tipo}")
                Text(text = "Lote: ${paquete.lote}")
                Text(
                    text = "Caducidad: ${paquete.caducidad} (${estado.name.replace("_", " ")})",
                    color = colorCaducidad,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Temperatura: ${paquete.tempMin}°C a ${paquete.tempMax}°C")

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { mostrarInstrucciones = !mostrarInstrucciones },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Filled.HelpOutline, contentDescription = "Instrucciones", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Instrucciones especiales")
                }

                if (mostrarInstrucciones) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Manipulación especial", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))

                            val instrucciones = when {
                                paquete.tempMax < 0 ->
                                    "• ULTRACONGELACIÓN ESTRICTA.\n• Peligro: Riesgo de quemaduras por frío. Usar guantes térmicos.\n• No abrir el contenedor térmico durante el traslado.\n• Entregar inmediatamente."

                                paquete.tipo.equals("ARM", ignoreCase = true) ->
                                    "• MEDICAMENTO DE ALTO RIESGO (ARM).\n• Requiere doble verificación al entregar.\n• Manejar con extrema precaución para evitar rupturas.\n• En caso de derrame, aplicar protocolo de bioseguridad."

                                paquete.tipo.equals("Biológico", ignoreCase = true) ->
                                    "• MATERIAL BIOLÓGICO SENSIBLE.\n• NO AGITAR (riesgo de desnaturalización de proteínas).\n• Mantener en posición vertical en todo momento.\n• Conservar red de frío estricta."

                                paquete.tipo.equals("Vacuna", ignoreCase = true) && paquete.tempMin == 2 && paquete.tempMax == 8 ->
                                    "• Mantener entre 2 °C y 8 °C.\n• NO CONGELAR bajo ninguna circunstancia (invalida la vacuna).\n• Mantener protegido de la luz solar directa.\n• Transportar en hielera pre-enfriada."

                                paquete.tempMin >= 15 && paquete.tempMax <= 25 ->
                                    "• Temperatura ambiente controlada (15 °C a 25 °C).\n• Proteger de la humedad extrema.\n• No dejar en el vehículo expuesto al sol.\n• Evitar aplastamiento."

                                else ->
                                    "• Manipular con cuidado.\n• Proteger de cambios bruscos de temperatura.\n• Revisar que los sellos de garantía estén intactos antes de entregar."
                            }

                            Text(text = instrucciones, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun TarjetaPaquete(paquete: PaqueteEntity, estado: EstadoPaquete, onClick: () -> Unit) {
    val (colorFondo, textoPrioridad, iconoPrioridad) = when (estado) {
        EstadoPaquete.NORMAL -> Triple(Color(0xFFE8F5E9), "Normal", Icons.Filled.CheckCircle)
        EstadoPaquete.PROXIMO_A_CADUCAR -> Triple(Color(0xFFFFF9C4), "Próximo a caducar", Icons.Filled.Warning)
        EstadoPaquete.CADUCADO -> Triple(Color(0xFFFFEBEE), "Caducado / Alerta", Icons.Filled.Error)
    }

    val colorBorde = when (estado) {
        EstadoPaquete.NORMAL -> Color(0xFF4CAF50)
        EstadoPaquete.PROXIMO_A_CADUCAR -> Color(0xFFFFEB3B)
        EstadoPaquete.CADUCADO -> Color(0xFFF44336)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        border = BorderStroke(1.dp, colorBorde),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Trk: ${paquete.tracking}", fontWeight = FontWeight.Bold)

                Surface(
                    color = colorBorde,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = iconoPrioridad,
                            contentDescription = "Icono de estado",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = textoPrioridad,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Tipo: ${paquete.tipo}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Estado: ${paquete.estadoEntrega}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "Producto: ${paquete.producto}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Caducidad: ${paquete.caducidad}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "Temp Mín: ${paquete.tempMin}°C", style = MaterialTheme.typography.bodySmall)
                Text(text = "Temp Máx: ${paquete.tempMax}°C", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}