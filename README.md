# PachaTech — Riego inteligente con Arduino y app Android

Sistema de riego automático para biohuertos urbanos. Un Arduino lee la humedad del suelo y del ambiente, y enciende la bomba solo cuando la planta lo necesita. Desde una app Android, conectada por Bluetooth, el usuario elige el tipo de planta, cambia entre modo automático y manual, y revisa el estado de sus huertos.

El proyecto se desarrolló en la Universidad Nacional Federico Villarreal (2026) y se complementó con un modelo de dinámica de sistemas que simula su comportamiento.

## Qué hace

- **Riego automático:** la bomba se activa cuando la humedad del suelo baja del umbral definido para la planta.
- **Modo manual:** encender o apagar la bomba desde la app.
- **Umbral por planta:** seis tipos de plantas, cada una con su porcentaje de humedad objetivo.
- **Monitoreo en tiempo real:** temperatura, humedad del aire, humedad del suelo y estado de la bomba, actualizados cada 2 segundos.
- **App Android:** registro e inicio de sesión, varios huertos por usuario, dashboard, bitácora e historial de monitoreo.

## Estructura del repositorio

| Carpeta | Contenido |
| --- | --- |
| `arduino/` | Código del microcontrolador (lectura de sensores, control de la bomba, comunicación Bluetooth) |
| `app-android/` | App Android en Kotlin con Jetpack Compose |
| `modelado/` | Modelo de dinámica de sistemas: Vensim, simulación en Python, matrices en Excel, informe y presentación |

## Hardware

| Componente | Conexión |
| --- | --- |
| Arduino (ATmega328P) | — |
| 2 sensores de humedad del suelo | A0, A1 (se promedian) |
| Sensor DHT11 (temperatura y humedad del aire) | Pin 2 |
| Módulo relé + bomba de agua | Pin 7 |
| Módulo Bluetooth HC-05 | RX → pin 10, TX → pin 8 (con divisor de tensión en la línea RX del HC-05) |

## Comunicación Bluetooth

**De la app al Arduino:**

| Comando | Acción |
| --- | --- |
| `1` | Encender la bomba (manual) |
| `0` | Apagar la bomba (manual) |
| `2` | Modo automático |
| `U<n>` + salto de línea | Fijar el umbral de humedad en `n` % (0–100), por ejemplo `U65` |

**Del Arduino a la app (cada 2 s):**

```
temperatura,humedadAire,humedadSuelo%,estadoBomba
```

Ejemplo: `23.00,61.00,48,1`

## Tecnologías

- **Microcontrolador:** Arduino (C++), librerías `DHT` y `SoftwareSerial`
- **App móvil:** Kotlin, Jetpack Compose, arquitectura MVVM, SQLite
- **Modelado:** Vensim, Python (Jupyter), Excel

## Cómo ejecutarlo

### Arduino

1. Abre `arduino/pachatech_riego/pachatech_riego.ino` en el Arduino IDE.
2. Instala la librería **DHT sensor library** (Adafruit) desde el gestor de librerías.
3. Selecciona tu placa y puerto, y carga el programa.
4. Calibra el sensor de humedad del suelo: ajusta `SECO_LECTURA` (sensor al aire) y `MOJADO_LECTURA` (sensor en agua) según tus lecturas.

### App Android

1. Abre la carpeta `app-android/` en Android Studio y sincroniza Gradle.
2. En el celular, vincula el módulo HC-05 desde los ajustes de Bluetooth.
3. Instala la app y concédele el permiso de **dispositivos cercanos**.
4. Crea una cuenta, crea un huerto y conéctate al HC-05.

### Modelado

- `modelado/modelos/modelo_vensim.mdl`: modelo stock-flow para abrir en Vensim.
- `modelado/modelos/simulacion_python.ipynb`: simulación en Python.
- `modelado/matrices/`: variables, bucles de retroalimentación, escenarios y validación.

## Resultados del modelado

- Con riego automático, la humedad del suelo se mantiene estable alrededor del umbral, en lugar de oscilar entre la sequía y el encharcamiento del riego por horario.
- En una ola de calor simulada (34 °C), el control automático compensa la pérdida de agua activando el riego unas 3 veces más rápido. Esto también anticipa un mayor desgaste del relé y de la bomba.

## Mejoras pendientes

- Agregar histéresis al control automático para que el relé no se active y desactive seguido cuando la humedad está cerca del umbral.
- Guardar las contraseñas de los usuarios con hash en lugar de texto plano.
- Migrar a un ESP32 con Wi-Fi y MQTT para monitorear el huerto de forma remota.

## Equipo

| Integrante | Aportes |
| --- | --- |
| Toro Suyllon, Gabriel David ([@gabrielsuyllon-nt](https://github.com/gabrielsuyllon-nt)) | App: base de datos SQLite, dashboard, registro de usuarios, creación de huertos, bitácora e historial de monitoreo, flujos de navegación. Modelado: matrices de variables, bucles, escenarios y validación en Excel |
| Toccas Rojas, Luis Fernando ([@LuisFern2a](https://github.com/LuisFern2a)) | App: vinculación Bluetooth, interfaz de varios huertos, selección de plantas |
| Zapana Porras, Johan Jairo | App: cierre de sesión, ícono y nombre de la app |
| Salcedo Mego, Arnold Alonzo ([@SalcedoMegoArnoldAlonzo](https://github.com/SalcedoMegoArnoldAlonzo))([@MorganMilleralo](https://github.com/MorganMilleralo)) | App: proyecto base, conexión Bluetooth, control manual de la bomba, seis tipos de plantas y modo automático |

Código del Arduino: [Salcedo Mego, Arnold Alonzo].

Curso de Dinámica de Sistemas — docente: Mg. Maurice Frayssinet Delgado.
