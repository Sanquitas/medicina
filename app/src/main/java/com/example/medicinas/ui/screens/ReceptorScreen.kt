package com.example.medicinas.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medicinas.viewmodel.DetalleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceptorScreen(viewModel: DetalleViewModel, onBackClick: () -> Unit, onSiguienteClick: () -> Unit) {
    val nombre by viewModel.receptorNombre.collectAsState()
    val cargo by viewModel.receptorCargo.collectAsState()
    val institucion by viewModel.receptorInstitucion.collectAsState()
    val identificacion by viewModel.receptorIdentificacion.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Datos del receptor") }, navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar") } }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Información obligatoria", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("Llene los campos antes de proceder a la firma.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 16.dp))
            OutlinedTextField(value = nombre, onValueChange = { viewModel.actualizarReceptorNombre(it) }, label = { Text("Nombre completo *") }, modifier = Modifier.fillMaxWidth(), singleLine = true, isError = nombre.isBlank())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = cargo, onValueChange = { viewModel.actualizarReceptorCargo(it) }, label = { Text("Cargo *") }, modifier = Modifier.fillMaxWidth(), singleLine = true, isError = cargo.isBlank())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = identificacion, onValueChange = { viewModel.actualizarReceptorIdentificacion(it) }, label = { Text("Identificación *") }, modifier = Modifier.fillMaxWidth(), singleLine = true, isError = identificacion.isBlank())
            Spacer(modifier = Modifier.height(24.dp))
            Text("Información opcional", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = institucion, onValueChange = { viewModel.actualizarReceptorInstitucion(it) }, label = { Text("Institución") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onSiguienteClick, modifier = Modifier.fillMaxWidth().height(50.dp), enabled = viewModel.esReceptorValido()) { Text("Siguiente (Firma)") }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}