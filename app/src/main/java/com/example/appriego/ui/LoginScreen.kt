package com.example.appriego.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appriego.data.AppDatabase
import com.example.appriego.R // IMPORTANTE: Asegúrate de que apunte al R de tu proyecto
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(onLoginExitoso: () -> Unit, onNavegarRegistro: () -> Unit) {
    val context = LocalContext.current
    val db = remember { AppDatabase(context) }

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var animacionIniciada by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        animacionIniciada = true
    }

    val escalaTitulo by animateFloatAsState(
        targetValue = if (animacionIniciada) 1f else 0.8f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "EscalaTitulo"
    )

    // El Box principal ahora apila la imagen, el degradado y el contenido encima
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 1. LA IMAGEN DE FONDO (Con opacidad controlada)
        Image(
            painter = painterResource(id = R.drawable.fondo_riego), // <--- AQUÍ VA TU IMAGEN
            contentDescription = "Fondo PachaTech",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop, // Hace que cubra toda la pantalla sin deformarse
            alpha = 0.60f // <--- CONTROL DE OPACIDAD (0.0 invisible, 1.0 total). 0.25f es perfecto para que no estorbe
        )

        // 2. EL DEGRADADO encima de la imagen (para fundir el color verde estético)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // 3. EL CONTENIDO DE LA INTERFAZ
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "PachaTech",
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.scale(escalaTitulo)
            )

            Text(
                text = "Sistema de Monitoreo",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            AnimatedVisibility(
                visible = animacionIniciada,
                enter = fadeIn(animationSpec = tween(600)) +
                        slideInVertically(animationSpec = tween(600, easing = FastOutSlowInEasing)) { it / 3 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it },
                        label = { Text("Correo Electrónico") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = { contrasena = it },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            val emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()

                            if (correo.isNotEmpty() && contrasena.isNotEmpty()) {
                                if (correo.trim().matches(emailPattern)) {
                                    val esValido = db.verificarLogin(correo.trim(), contrasena)
                                    if (esValido) {
                                        val prefs = context.getSharedPreferences("PachaTechPrefs", Context.MODE_PRIVATE)

                                        // 1. OBTENEMOS EL NOMBRE DESDE LA BASE DE DATOS (O puedes simularlo leyendo el correo antes de modificar la DB)
                                        // Para hacerlo directo, guardamos una cadena o hacemos una consulta rápida.
                                        // Como tu db.verificarLogin devuelve Boolean, una forma limpia es guardar el correo o modificar el SP:
                                        prefs.edit()
                                            .putBoolean("sesionActiva", true)
                                            .putString("usuarioNombre", correo.split("@")[0].replaceFirstChar { it.uppercase() }) // Truco temporal: usa la primera parte del correo como nombre
                                            .apply()

                                        Toast.makeText(context, "¡Bienvenido a PachaTech!", Toast.LENGTH_SHORT).show()
                                        onLoginExitoso()
                                    } else {
                                        Toast.makeText(context, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Por favor, ingresa un correo electrónico válido", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Iniciar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = onNavegarRegistro) {
                        Text(
                            "¿No tienes cuenta? Regístrate aquí",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}