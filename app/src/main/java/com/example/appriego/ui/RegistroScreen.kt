package com.example.appriego.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appriego.data.AppDatabase
import com.example.appriego.R // Importante para reconocer la imagen
import kotlinx.coroutines.delay

@Composable
fun RegistroScreen(onRegistroExitoso: () -> Unit, onVolverLogin: () -> Unit) {
    val context = LocalContext.current
    val db = remember { AppDatabase(context) }

    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    // Estado para la animación de entrada
    var animacionIniciada by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(50)
        animacionIniciada = true
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 1. IMAGEN DE FONDO (Igual que en el Login)
        Image(
            painter = painterResource(id = R.drawable.fondo_riego),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.60f // Un poquito más tenue para que los 4 campos se vean bien
        )

        // 2. DEGRADADO VERDE
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        )
                    )
                )
        )

        // 3. CONTENIDO DEL REGISTRO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Crear Cuenta",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Únete a la comunidad PachaTech",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = animacionIniciada,
                enter = fadeIn(animationSpec = tween(500)) +
                        slideInVertically(animationSpec = tween(500, easing = FastOutSlowInEasing)) { it / 4 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = apellidos,
                        onValueChange = { apellidos = it },
                        label = { Text("Apellidos") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it },
                        label = { Text("Correo Electrónico") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

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

                            if (nombre.isNotEmpty() && apellidos.isNotEmpty() && correo.isNotEmpty() && contrasena.isNotEmpty()) {

                                if (correo.trim().matches(emailPattern)) {
                                    val exito = db.registrarUsuario(
                                        nombre.trim(),
                                        apellidos.trim(),
                                        correo.trim(),
                                        contrasena
                                    )
                                    if (exito) {
                                        Toast.makeText(context, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
                                        onRegistroExitoso()
                                    } else {
                                        Toast.makeText(context, "Error al guardar en base de datos", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Por favor, ingresa un correo válido", Toast.LENGTH_LONG).show()
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
                        Text("Registrarme", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = onVolverLogin) {
                        Text(
                            "¿Ya tienes cuenta? Inicia Sesión",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}