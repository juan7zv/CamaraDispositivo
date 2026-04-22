package com.juanv.eam.camara.screens


import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.juanv.eam.camara.ui.theme.*
import java.net.URLEncoder

// Pantalla de cámara - maneja permisos, captura de foto y navegación a edición
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(navController: NavController) {
    // Contexto necesario para acceder a permisos y almacenamiento
    val context = LocalContext.current

    // Estado para guardar la URI donde se almacenará la foto
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    // Estado para controlar mensajes al usuario
    var mensajeEstado by remember { mutableStateOf("Presiona el botón para tomar una foto") }

    // Función que crea una URI (dirección) en la galería del dispositivo
    // Aquí se define el nombre del archivo, tipo y carpeta destino
    fun crearUriImagen(context: Context): Uri? {
        val values = ContentValues().apply {
            // Nombre del archivo con timestamp para que sea único
            put(MediaStore.Images.Media.DISPLAY_NAME, "SnapNote_${System.currentTimeMillis()}.jpg")
            // Tipo de archivo: imagen JPEG
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            // Carpeta destino dentro de la galería
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SnapNotes")
        }
        // Inserta el registro en MediaStore y devuelve la URI creada
        return context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
    }

    // Launcher para abrir la cámara usando TakePicture()
    // TakePicture() guarda la foto directamente en la URI proporcionada
    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imagenUri != null) {
            // La foto se tomó correctamente - navegamos a la pantalla de edición
            // Codificamos la URI para pasarla como parámetro en la ruta
            val uriCodificada = URLEncoder.encode(imagenUri.toString(), "UTF-8")
            navController.navigate("edit/$uriCodificada")
        } else {
            // El usuario canceló o hubo un error
            imagenUri = null
            mensajeEstado = "No se tomó la foto. Intenta de nuevo."
        }
    }

    // Launcher para solicitar el permiso de cámara al usuario
    val launcherPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            // Permiso aceptado → creamos la URI y abrimos la cámara
            imagenUri = crearUriImagen(context)
            imagenUri?.let { launcherCamara.launch(it) }
        } else {
            // Permiso denegado por el usuario
            mensajeEstado = "Se necesita permiso de cámara para tomar fotos"
        }
    }

    Scaffold(
        // Barra superior con botón de regreso
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Tomar Foto",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    // Botón para regresar a la pantalla anterior
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AzulClaro,
                    titleContentColor = Blanco,
                    navigationIconContentColor = Blanco
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Ícono grande de cámara
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Cámara",
                modifier = Modifier.size(100.dp),
                tint = AzulClaro
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Mensaje de estado para el usuario
            Text(
                text = mensajeEstado,
                fontSize = 14.sp,
                color = GrisTexto,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón principal para tomar la foto
            Button(
                onClick = {
                    // Verificamos si el permiso ya fue otorgado antes
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        // Ya tenemos permiso → creamos URI y abrimos cámara
                        imagenUri = crearUriImagen(context)
                        imagenUri?.let { launcherCamara.launch(it) }
                    } else {
                        // No tenemos permiso → lo solicitamos al usuario
                        launcherPermiso.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier
                    .size(140.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulMedio
                )
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Capturar",
                    modifier = Modifier.size(56.dp),
                    tint = Blanco
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Capturar",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = AzulMedio
            )
        }
    }
}
