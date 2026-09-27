package com.example.medicinas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.medicinas.data.local.AppDatabase
import com.example.medicinas.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Obtenemos la conexión a la base de datos
        val database = AppDatabase.getDatabase(this)

        // 2. Extraemos AMBOS DAOs (Paquetes y Usuarios)
        val paqueteDao = database.paqueteDao()
        val usuarioDao = database.usuarioDao()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 3. Le pasamos AMBOS parámetros a la navegación
                    AppNavigation(
                        paqueteDao = paqueteDao,
                        usuarioDao = usuarioDao
                    )
                }
            }
        }
    }
}