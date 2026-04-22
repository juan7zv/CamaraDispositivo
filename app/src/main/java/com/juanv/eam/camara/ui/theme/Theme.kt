package com.juanv.eam.camara.ui.theme


import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Esquema de colores claro personalizado
private val LightColorScheme = lightColorScheme(
    primary = AzulMedio,
    onPrimary = Blanco,
    primaryContainer = AzulClaro,
    secondary = AzulClaro,
    background = Blanco,
    surface = Blanco,
    onBackground = Negro,
    onSurface = Negro
)

@Composable
fun SnapNotesTheme(
    content: @Composable () -> Unit
) {
    // Aplicamos el esquema de colores claro a toda la app
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
