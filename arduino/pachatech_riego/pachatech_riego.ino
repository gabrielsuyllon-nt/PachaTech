#include <SoftwareSerial.h>
#include "DHT.h"

// --- CONFIGURACIÓN DE PINES ---
#define DHTPIN 2
#define DHTTYPE DHT11

#define RELAY_PIN 7
#define SOIL1_PIN A0
#define SOIL2_PIN A1

SoftwareSerial BT(10, 8);  // RX, TX (HC-05)
DHT dht(DHTPIN, DHTTYPE);

unsigned long lastTime = 0;
int mode = 2;               // 0 = Apagado, 1 = Encendido, 2 = Automático
bool bombaEncendida = false;

// --- NUEVO: UMBRAL DE HUMEDAD RECIBIDO DE LA APP (en porcentaje 0-100) ---
int umbralHumedad = 50;     // Valor por defecto (50% = punto medio)

void setup() {
  Serial.begin(9600);
  BT.begin(9600);
  dht.begin();

  pinMode(RELAY_PIN, OUTPUT);
  digitalWrite(RELAY_PIN, HIGH);  // Relé apagado (ajusta según tu módulo)

  Serial.println("Sistema listo - Esperando comandos Bluetooth...");
}

void loop() {
  // ==========================================
  // 1. RECIBIR COMANDOS DESDE LA APP
  // ==========================================
  if (BT.available()) {
    char comando = BT.read();

    if (comando == 'U') {
      // La app va a enviar "U" seguido del número (ej: U65) y un '\n'
      String umbralStr = BT.readStringUntil('\n');
      umbralStr.trim();
      int nuevoUmbral = umbralStr.toInt();
      if (nuevoUmbral >= 0 && nuevoUmbral <= 100) {
        umbralHumedad = nuevoUmbral;
        Serial.print("Umbral actualizado a: ");
        Serial.print(umbralHumedad);
        Serial.println("%");
      }
    } 
    else if (comando == '1') {
      mode = 1; // Manual Encender
    } else if (comando == '0') {
      mode = 0; // Manual Apagar
    } else if (comando == '2') {
      mode = 2; // Automático
    }
  }

  // ==========================================
  // 2. LEER SENSORES Y CALCULAR PORCENTAJE
  // ==========================================
  int lectura1 = analogRead(SOIL1_PIN);
  int lectura2 = analogRead(SOIL2_PIN);
  int humSueloPromedio = (lectura1 + lectura2) / 2;

  // Mapeo a porcentaje (ajusta SECO_LECTURA y MOJADO_LECTURA según calibración)
  const int SECO_LECTURA = 1023;   // valor con sensor al aire
  const int MOJADO_LECTURA = 300;  // valor con sensor en agua (ajústalo)
  int porcentajeHumedad = map(humSueloPromedio, SECO_LECTURA, MOJADO_LECTURA, 0, 100);
  porcentajeHumedad = constrain(porcentajeHumedad, 0, 100);

  // ==========================================
  // 3. LÓGICA DE CONTROL DE LA BOMBA
  // ==========================================
  if (mode == 1) {
    bombaEncendida = true;
  } else if (mode == 0) {
    bombaEncendida = false;
  } else if (mode == 2) {
    // MODO AUTOMÁTICO: usa el umbral recibido de la app (porcentaje)
    if (porcentajeHumedad < umbralHumedad) {
      bombaEncendida = true;   // Si la humedad es menor que el umbral, regar
    } else {
      bombaEncendida = false;
    }
  }

  // Activar relé (ajusta el nivel lógico según tu módulo)
  digitalWrite(RELAY_PIN, bombaEncendida ? LOW : HIGH);

  // ==========================================
  // 4. ENVIAR DATOS A LA APP CADA 2 SEGUNDOS
  // ==========================================
  if (millis() - lastTime > 2000) {
    float temp = dht.readTemperature();
    float humAire = dht.readHumidity();

    if (isnan(temp) || isnan(humAire)) {
      temp = 0.0;
      humAire = 0.0;
    }

    // Enviamos: temperatura,humedadAire,porcentajeHumedad,bombaEstado
    BT.print(temp);
    BT.print(",");
    BT.print(humAire);
    BT.print(",");
    BT.print(porcentajeHumedad);   // <-- ahora es porcentaje 0-100
    BT.print(",");
    BT.print(bombaEncendida ? 1 : 0);
    BT.println();

    // Monitor serie para depuración
    Serial.print("Enviado: ");
    Serial.print(temp); Serial.print(",");
    Serial.print(humAire); Serial.print(",");
    Serial.print(porcentajeHumedad); Serial.print("%, Bomba:");
    Serial.println(bombaEncendida);

    lastTime = millis();
  }
}