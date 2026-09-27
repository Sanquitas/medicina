package com.example.medicinas.navigation

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.medicinas.data.local.dao.PaqueteDao
import com.example.medicinas.data.local.dao.UsuarioDao
import com.example.medicinas.ui.screens.AcercaDeScreen
import com.example.medicinas.ui.screens.DashboardScreen
import com.example.medicinas.ui.screens.DetallePaqueteScreen
import com.example.medicinas.ui.screens.EscaneoScreen
import com.example.medicinas.ui.screens.FirmaScreen
import com.example.medicinas.ui.screens.LoginScreen
import com.example.medicinas.ui.screens.ReceptorScreen
import com.example.medicinas.ui.screens.ReportesScreen
import com.example.medicinas.viewmodel.DashboardViewModel
import com.example.medicinas.viewmodel.DashboardViewModelFactory
import com.example.medicinas.viewmodel.DetalleViewModel
import com.example.medicinas.viewmodel.DetalleViewModelFactory
import com.example.medicinas.viewmodel.LoginViewModel // Asegúrate de importar tu LoginViewModel
import com.example.medicinas.viewmodel.LoginViewModelFactory

@Composable
fun AppNavigation(paqueteDao: PaqueteDao, usuarioDao: UsuarioDao) {
    val navController = rememberNavController()
    val activity = LocalActivity.current as ComponentActivity

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            // 2. USAMOS EL FACTORY PARA CREAR EL VIEWMODEL (Igual que en Dashboard)
            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(usuarioDao)
            )

            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate("dashboard") { popUpTo("login") { inclusive = true } }
                }
            )
        }

        composable("dashboard") {
            val dashboardViewModel: DashboardViewModel = viewModel(factory = DashboardViewModelFactory(paqueteDao))
            DashboardScreen(
                viewModel = dashboardViewModel,
                rol = "Operador",
                onEscanearClick = { navController.navigate("escaneo") },
                onReportesClick = { navController.navigate("reportes") },
                onAcercaDeClick = { navController.navigate("acerca_de") },
                // NUEVO EVENTO: Cerrar sesión limpiando todo el historial
                onLogoutClick = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true } // Esto limpia absolutamente todas las pantallas anteriores
                    }
                }
            )
        }

        composable("escaneo") {
            EscaneoScreen(
                onBackClick = { navController.popBackStack() },
                onQrScanned = { codigoEscaneado ->
                    // Cuando la cámara lee el código, viaja al detalle de ESE paquete
                    navController.navigate("detalle/$codigoEscaneado")
                }
            )
        }

        composable("detalle/{trackingId}", arguments = listOf(navArgument("trackingId") { type = NavType.StringType })) { backStackEntry ->
            val trackingId = backStackEntry.arguments?.getString("trackingId") ?: ""
            val detalleViewModel: DetalleViewModel = viewModel(viewModelStoreOwner = activity, factory = DetalleViewModelFactory(paqueteDao))
            DetallePaqueteScreen(
                codigoTracking = trackingId, viewModel = detalleViewModel,
                onBackClick = { navController.popBackStack() },
                onConfirmarEntrega = { navController.navigate("receptor/$trackingId") }
            )
        }

        composable("receptor/{trackingId}", arguments = listOf(navArgument("trackingId") { type = NavType.StringType })) { backStackEntry ->
            val trackingId = backStackEntry.arguments?.getString("trackingId") ?: ""
            val detalleViewModel: DetalleViewModel = viewModel(viewModelStoreOwner = activity, factory = DetalleViewModelFactory(paqueteDao))
            ReceptorScreen(
                viewModel = detalleViewModel,
                onBackClick = { navController.popBackStack() },
                onSiguienteClick = { navController.navigate("firma/$trackingId") }
            )
        }

        composable("firma/{trackingId}", arguments = listOf(navArgument("trackingId") { type = NavType.StringType })) { backStackEntry ->
            val trackingId = backStackEntry.arguments?.getString("trackingId") ?: ""
            val detalleViewModel: DetalleViewModel = viewModel(viewModelStoreOwner = activity, factory = DetalleViewModelFactory(paqueteDao))
            FirmaScreen(
                codigoTracking = trackingId, viewModel = detalleViewModel,
                onBackClick = { navController.popBackStack() },
                onFinalizar = { navController.navigate("dashboard") { popUpTo("dashboard") { inclusive = false } } }
            )
        }

        composable("acerca_de") {
            AcercaDeScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("reportes") {
            ReportesScreen(onBackClick = { navController.popBackStack() })
        }
    }
}