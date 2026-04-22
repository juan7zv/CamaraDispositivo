package com.juanv.eam.camara.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.juanv.eam.camara.ui.theme.*

// Pantalla principal de SnapNotes - muestra el botón para tomar foto
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        // Barra superior con el nombre de la app
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "SnapNotes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AzulClaro,
                    titleContentColor = Blanco
                )
            )
        }
    ) { paddingValues ->
        // Contenido principal centrado verticalmente
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Ícono decorativo de cámara grande
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Ícono de cámara",
                modifier = Modifier.size(80.dp),
                tint = AzulMedio
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Título principal
            Text(
                text = "Captura el momento",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Negro
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción
            Text(
                text = "Toma una foto y añade una nota rápida\npara recordar lo importante",
                fontSize = 14.sp,
                color = GrisTexto,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Botón principal de cámara - grande y redondeado
            Button(
                onClick = {
                    // Navegamos a la pantalla de cámara
                    navController.navigate("camera")
                },
                modifier = Modifier
                    .size(120.dp)
                    .shadow(8.dp, CircleShape),
                shape = CircleShape, // Botón completamente circular
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulMedio
                )
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Tomar foto",
                    modifier = Modifier.size(48.dp),
                    tint = Blanco
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Texto indicativo bajo el botón
            Text(
                text = "Tomar foto",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = AzulMedio
            )
        }
    }
}

