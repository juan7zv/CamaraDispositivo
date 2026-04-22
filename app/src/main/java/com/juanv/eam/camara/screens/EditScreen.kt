package com.juanv.eam.camara.screens



import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.juanv.eam.camara.ui.theme.*

// Pantalla de edición - muestra la foto tomada y permite añadir una nota
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(navController: NavController, fotoUriString: String) {
    val context = LocalContext.current

    // Convertimos el String de la URI de vuelta a un objeto Uri
    val fotoUri = Uri.parse(fotoUriString)

    // Estado para el texto de la nota que escribe el usuario
    var nota by remember { mutableStateOf("") }

    // Estado para mostrar indicador de guardado
    var guardando by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Añadir Nota",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Mostramos la foto usando rememberAsyncImagePainter (Coil)
            // Carga la imagen desde la URI de forma eficiente
            Image(
                painter = rememberAsyncImagePainter(fotoUri),
                contentDescription = "Foto tomada",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, AzulClaro, RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo de texto para escribir la nota
            OutlinedTextField(
                value = nota,
                onValueChange = { nota = it },
                label = { Text("Escribe tu nota aquí...") },
                placeholder = { Text("Ej: Recordar comprar esto") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AzulMedio,
                    unfocusedBorderColor = AzulClaro,
                    cursorColor = AzulMedio
                ),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón para guardar la nota sobre la foto y regresar al inicio
            Button(
                onClick = {
                    guardando = true

                    // Si el usuario escribió una nota, la estampamos sobre la foto
                    if (nota.isNotBlank()) {
                        val exito = guardarNotaEnFoto(context, fotoUri, nota)
                        if (exito) {
                            Toast.makeText(
                                context,
                                "📸 Foto guardada con nota en galería",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "⚠️ Error al guardar la nota sobre la foto",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        // Sin nota, solo confirmamos que la foto está guardada
                        Toast.makeText(
                            context,
                            "📸 Foto guardada en galería",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    guardando = false

                    // Navegamos al inicio y limpiamos el historial de navegación
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulMedio
                ),
                enabled = !guardando // Deshabilitamos mientras guarda
            ) {
                if (guardando) {
                    // Indicador de carga mientras se guarda
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Blanco,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Guardar",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Guardar Nota",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Texto informativo
            Text(
                text = "La nota se guardará sobre la foto\nen la carpeta SnapNotes de tu galería",
                fontSize = 12.sp,
                color = GrisTexto,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

// Función que dibuja el texto de la nota sobre la foto y la guarda en galería
// Recibe el contexto, la URI de la foto y el texto de la nota
fun guardarNotaEnFoto(context: Context, uri: Uri, nota: String): Boolean {
    try {
        // Leemos la imagen original desde la URI en la galería
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmapOriginal = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        if (bitmapOriginal == null) return false

        // Creamos una copia modificable del bitmap para poder dibujar sobre ella
        val bitmapConNota = bitmapOriginal.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(bitmapConNota)

        // Configuramos el pincel para el fondo oscuro semitransparente
        val fondoPaint = Paint().apply {
            color = android.graphics.Color.argb(160, 0, 0, 0)
        }

        // Configuramos el pincel para el texto blanco con sombra
        val textoPaint = TextPaint().apply {
            color = android.graphics.Color.WHITE
            textSize = bitmapConNota.width / 18f // Tamaño proporcional al ancho de la foto
            isAntiAlias = true // Suaviza los bordes del texto
            setShadowLayer(3f, 1f, 1f, android.graphics.Color.BLACK)
        }

        // Margen interno para que el texto no toque los bordes
        val margen = 24f
        val anchoTexto = (bitmapConNota.width - margen * 2).toInt()

        // StaticLayout maneja texto largo con saltos de línea automáticos
        val staticLayout = StaticLayout.Builder
            .obtain(nota, 0, nota.length, textoPaint, anchoTexto)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(4f, 1f)
            .build()

        val alturaTexto = staticLayout.height.toFloat()

        // Dibujamos un rectángulo oscuro en la parte inferior de la foto
        val fondoTop = bitmapConNota.height - alturaTexto - margen * 3
        canvas.drawRect(
            0f, fondoTop,
            bitmapConNota.width.toFloat(),
            bitmapConNota.height.toFloat(),
            fondoPaint
        )

        // Posicionamos y dibujamos el texto sobre el fondo oscuro
        canvas.save()
        canvas.translate(margen, fondoTop + margen)
        staticLayout.draw(canvas)
        canvas.restore()

        // Sobreescribimos la imagen en la galería con la versión que tiene la nota
        // "w" significa write - reemplaza el contenido anterior
        val outputStream = context.contentResolver.openOutputStream(uri, "w")
        bitmapConNota.compress(Bitmap.CompressFormat.JPEG, 95, outputStream!!)
        outputStream.close()

        // Liberamos memoria de los bitmaps
        bitmapOriginal.recycle()
        bitmapConNota.recycle()

        return true
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}
