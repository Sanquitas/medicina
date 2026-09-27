package com.example.medicinas.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.medicinas.data.local.dao.PaqueteDao
import com.example.medicinas.data.local.entities.PaqueteEntity
import com.example.medicinas.utils.CaducidadUtils
import com.example.medicinas.utils.GpsUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChecklistEstado(
    val lote: Boolean = false, val seguimiento: Boolean = false,
    val caducidad: Boolean = false, val legible: Boolean = false, val sinDanos: Boolean = false
) { val estaCompleto get() = lote && seguimiento && caducidad && legible && sinDanos }

class DetalleViewModel(private val paqueteDao: PaqueteDao) : ViewModel() {
    private val _cargando = MutableStateFlow(true)
    val cargando = _cargando.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError = _mensajeError.asStateFlow()

    private val _paquete = MutableStateFlow<PaqueteEntity?>(null)
    val paquete: StateFlow<PaqueteEntity?> = _paquete.asStateFlow()

    private val _estadoCaducidad = MutableStateFlow(EstadoPaquete.NORMAL)
    val estadoCaducidad: StateFlow<EstadoPaquete> = _estadoCaducidad.asStateFlow()

    private val _distanciaActual = MutableStateFlow<Float?>(null)
    val distanciaActual: StateFlow<Float?> = _distanciaActual.asStateFlow()

    private val _estaEnUbicacion = MutableStateFlow(false)
    val estaEnUbicacion: StateFlow<Boolean> = _estaEnUbicacion.asStateFlow()

    val destinoLat = 18.813587
    val destinoLon =  -99.668200
    val radioPermitido = 100f // metros


    private val _temperatura = MutableStateFlow("")
    val temperatura: StateFlow<String> = _temperatura.asStateFlow()

    private val _checklist = MutableStateFlow(ChecklistEstado())
    val checklist: StateFlow<ChecklistEstado> = _checklist.asStateFlow()

    private val _fotoPaquete = MutableStateFlow<Bitmap?>(null)
    val fotoPaquete = _fotoPaquete.asStateFlow()
    private val _fotoEtiqueta = MutableStateFlow<Bitmap?>(null)
    val fotoEtiqueta = _fotoEtiqueta.asStateFlow()
    private val _fotoAdicional = MutableStateFlow<Bitmap?>(null)
    val fotoAdicional = _fotoAdicional.asStateFlow()
    private var tipoFotoPendiente = 0

    private val _videoUri = MutableStateFlow<Uri?>(null)
    val videoUri = _videoUri.asStateFlow()
    private val _videoMiniatura = MutableStateFlow<Bitmap?>(null)
    val videoMiniatura = _videoMiniatura.asStateFlow()

    private val _receptorNombre = MutableStateFlow("")
    val receptorNombre = _receptorNombre.asStateFlow()
    private val _receptorCargo = MutableStateFlow("")
    val receptorCargo = _receptorCargo.asStateFlow()
    private val _receptorInstitucion = MutableStateFlow("")
    val receptorInstitucion = _receptorInstitucion.asStateFlow()
    private val _receptorIdentificacion = MutableStateFlow("")
    val receptorIdentificacion = _receptorIdentificacion.asStateFlow()

    private val _rutaFirma = MutableStateFlow<String?>(null)
    val rutaFirma = _rutaFirma.asStateFlow()

    fun cargarPaquete(trackingId: String) {
        viewModelScope.launch {
            _cargando.value = true
            _mensajeError.value = null

            val p = paqueteDao.obtenerPaquetePorTracking(trackingId)

            if (p != null) {
                _paquete.value = p
                _estadoCaducidad.value = CaducidadUtils.evaluar(p.caducidad)
            } else {
                _paquete.value = null
                _mensajeError.value = "No se encontró el paquete con el código: $trackingId"
            }
            _cargando.value = false
        }
    }

    fun verificarUbicacion(lat: Double, lon: Double) {
        val distancia = GpsUtils.calcularDistanciaMetros(lat, lon, destinoLat, destinoLon)
        _distanciaActual.value = distancia
        _estaEnUbicacion.value = distancia <= radioPermitido
    }

    fun actualizarTemperatura(temp: String) { _temperatura.value = temp }
    fun actualizarChecklist(estado: ChecklistEstado) { _checklist.value = estado }
    fun prepararCapturaFoto(tipo: Int) { tipoFotoPendiente = tipo }
    fun guardarFoto(bitmap: Bitmap) {
        when (tipoFotoPendiente) { 1 -> _fotoPaquete.value = bitmap; 2 -> _fotoEtiqueta.value = bitmap; 3 -> _fotoAdicional.value = bitmap }
        tipoFotoPendiente = 0
    }
    fun guardarVideo(uri: Uri, miniatura: Bitmap?) { _videoUri.value = uri; _videoMiniatura.value = miniatura }
    fun eliminarVideo() { _videoUri.value = null; _videoMiniatura.value = null }

    fun actualizarReceptorNombre(valor: String) { _receptorNombre.value = valor }
    fun actualizarReceptorCargo(valor: String) { _receptorCargo.value = valor }
    fun actualizarReceptorInstitucion(valor: String) { _receptorInstitucion.value = valor }
    fun actualizarReceptorIdentificacion(valor: String) { _receptorIdentificacion.value = valor }
    fun guardarRutaFirma(ruta: String) { _rutaFirma.value = ruta }

    fun esTemperaturaValida(): Boolean {
        val t = _temperatura.value.toDoubleOrNull() ?: return false
        val p = _paquete.value ?: return false
        return t >= p.tempMin && t <= p.tempMax
    }

    fun puedeConfirmarEntrega(): Boolean {
        return esTemperaturaValida() && _estadoCaducidad.value != EstadoPaquete.CADUCADO &&
                _checklist.value.estaCompleto && _estaEnUbicacion.value &&
                _fotoPaquete.value != null && _fotoEtiqueta.value != null && _videoUri.value != null
    }

    fun esReceptorValido(): Boolean {
        return _receptorNombre.value.isNotBlank() && _receptorCargo.value.isNotBlank() && _receptorIdentificacion.value.isNotBlank()
    }

    fun puedeFinalizarEntrega(): Boolean {
        return esReceptorValido() && _rutaFirma.value != null && _rutaFirma.value!!.isNotEmpty()
    }

    fun guardarEntregaEnBaseDeDatos(trackingId: String) {
        viewModelScope.launch {
            try {
                // Cambiamos el estado a "Entregado" en la Base de Datos
                paqueteDao.marcarComoEntregado(trackingId)

                println("ENTREGA GUARDADA EXITOSAMENTE. Tracking: $trackingId")
                println("Firma cifrada en: ${_rutaFirma.value}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

class DetalleViewModelFactory(private val paqueteDao: PaqueteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetalleViewModel::class.java)) return DetalleViewModel(paqueteDao) as T
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}