package com.example.medicinas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicinas.data.local.dao.PaqueteDao
import com.example.medicinas.data.local.entities.PaqueteEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

enum class FiltroDashboard { TODOS, VACUNAS, CONTROLADOS, PENDIENTES }

class DashboardViewModel(private val paqueteDao: PaqueteDao) : ViewModel() {

    private val _filtroActual = MutableStateFlow(FiltroDashboard.TODOS)
    val filtroActual: StateFlow<FiltroDashboard> = _filtroActual

    private val _todosLosPaquetes = paqueteDao.obtenerTodosLosPaquetes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val paquetesFiltrados: StateFlow<List<PaqueteEntity>> = combine(
        _todosLosPaquetes, _filtroActual
    ) { paquetes, filtro ->
        when (filtro) {
            FiltroDashboard.TODOS -> paquetes
            FiltroDashboard.VACUNAS -> paquetes.filter { it.tipo.contains("Vacuna", ignoreCase = true) }
            FiltroDashboard.CONTROLADOS -> paquetes.filter { it.tipo.contains("ARM", ignoreCase = true) } // Medicamentos de Alto Riesgo
            FiltroDashboard.PENDIENTES -> paquetes.filter { it.estadoEntrega == "PENDIENTE" }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun cambiarFiltro(nuevoFiltro: FiltroDashboard) {
        _filtroActual.value = nuevoFiltro
    }

    fun evaluarEstadoPaquete(caducidadStr: String): EstadoPaquete {
        return try {
            val formatter = SimpleDateFormat("M/d/yyyy", Locale.getDefault())
            val fechaCaducidad = formatter.parse(caducidadStr)
            val hoy = Date()

            if (fechaCaducidad != null) {
                val diffEnMilisegundos = fechaCaducidad.time - hoy.time
                val diasRestantes = TimeUnit.MILLISECONDS.toDays(diffEnMilisegundos)

                when {
                    diasRestantes < 0 -> EstadoPaquete.CADUCADO
                    diasRestantes == 0L -> EstadoPaquete.CADUCADO
                    diasRestantes in 1..30 -> EstadoPaquete.PROXIMO_A_CADUCAR
                    else -> EstadoPaquete.NORMAL
                }
            } else {
                EstadoPaquete.NORMAL
            }
        } catch (e: Exception) {

            EstadoPaquete.NORMAL
        }
    }
}


enum class EstadoPaquete { NORMAL, PROXIMO_A_CADUCAR, CADUCADO }