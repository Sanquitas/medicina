package com.example.medicinas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicinas.data.local.dao.PaqueteDao

class DashboardViewModelFactory(private val paqueteDao: PaqueteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(paqueteDao) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}