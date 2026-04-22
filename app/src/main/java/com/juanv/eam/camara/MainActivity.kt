package com.juanv.eam.camara

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.juanv.eam.camara.screens.CameraScreen
import com.juanv.eam.camara.screens.EditScreen
import com.juanv.eam.camara.screens.HomeScreen
import com.juanv.eam.camara.ui.theme.SnapNotesTheme
import java.net.URLDecoder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Envolvemos toda la app con nuestro tema personalizado
            SnapNotesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Controlador de navegación - maneja el historial de pantallas
                    val navController = rememberNavController()

                    // NavHost define todas las rutas/pantallas de la app
                    NavHost(
                        navController = navController,
                        startDestination = "home" // Pantalla inicial
                    ) {
                        // Ruta: pantalla principal
                        composable("home") {
                            HomeScreen(navController)
                        }

                        // Ruta: pantalla de cámara
                        composable("camera") {
                            CameraScreen(navController)
                        }

                        // Ruta: pantalla de edición - recibe la URI de la foto como parámetro
                        composable(
                            route = "edit/{fotoUri}",
                            arguments = listOf(
                                navArgument("fotoUri") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            // Decodificamos la URI que viene codificada en la URL
                            val fotoUri = URLDecoder.decode(
                                backStackEntry.arguments?.getString("fotoUri") ?: "",
                                "UTF-8"
                            )
                            EditScreen(navController, fotoUri)
                        }
                    }
                }
            }
        }
    }
}
