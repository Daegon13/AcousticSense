# Estrategia de pruebas

## Unitarias

Diagnóstico, serialización, tiempo-distancia, ventanas, correlación, picos, confianza, suavizado y estados.

## Integración

Kotlin/JNI, streams, exportación, lifecycle, pérdida de permiso y errores.

## Regresión

Cada fallo real debe convertirse en fixture sintético o grabación mínima anonimizada.

## Físicas

Distancias conocidas, habitaciones, objetos, días, batería y temperatura.

## Accesibilidad

TalkBack, foco, etiquetas, controles, contraste, mensajes y vibraciones.

## Comandos

```powershell
.\gradlew.bat test
.\gradlew.bat lint
.\gradlew.bat assembleDebug
```

## No cuenta como prueba

- “abrió una vez”;
- “Codex dijo que funciona”;
- “compila en la nube”;
- una sola medición;
- una gráfica sin métrica.

## Pruebas de fase full-duplex

Las pruebas JVM cubren permiso, idempotencia, error de apertura, estado, pulso,
orden secuencial, cancelación, resultado inconcluso, resumen y JSON sin audio.
El build nativo debe compilar `arm64-v8a`, `armeabi-v7a`, `x86` y `x86_64`.
Solo un teléfono permite confirmar API, modos, frecuencia, canales, burst,
buffers, IDs, preset efectivo, xruns, desconexión, estabilidad y audibilidad.
La prueba física no debe afirmar que solicitar `Unprocessed` elimina procesamiento.
