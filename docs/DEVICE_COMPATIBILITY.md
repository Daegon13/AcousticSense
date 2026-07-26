# Compatibilidad

No existe ningún teléfono validado todavía.

## Estados

- No evaluado.
- Diagnóstico disponible.
- Captura validada.
- Full-duplex validado.
- Pared experimental.
- Zonas experimentales.
- Rechazado.

| Fabricante | Modelo | Android | Sample rate | Canales | Raw | Estado | Notas |
|---|---|---:|---:|---:|---|---|---|
| Pendiente | Pendiente | — | — | — | — | No evaluado | — |

## Datos disponibles en el diagnóstico

La aplicación puede exportar un JSON con fabricante, modelo, producto/dispositivo,
versión Android, SDK, ABI, versión de la aplicación, sample rate de salida sugerido,
frames por buffer y los dispositivos de entrada y salida que Android declara. Para
cada dispositivo de audio se registran identificador, nombre, tipo y la cantidad
máxima de canales reportada cuando está disponible, además del timestamp.

Un valor ausente se conserva como `null`, `unknown` o `unavailable`, según el
contexto. Estos datos son inventario declarado por las APIs de Android: no prueban
captura, full-duplex, latencia, respuesta acústica ni compatibilidad experimental.

## Datos de captura básica

Durante una sesión iniciada por el usuario, la pantalla informa la fuente
solicitada y la finalmente seleccionada, sample rate real, canales, encoding,
buffer, estado, muestras y duración. Se solicitan 48.000 Hz, mono y PCM 16. La
fuente `UNPROCESSED` solo se intenta cuando Android declara soporte y se usa
`MIC` como fallback; esa declaración no garantiza ausencia total de procesamiento
del fabricante.

Para promover un modelo a “Captura validada” todavía se requiere un teléfono
físico: conceder, rechazar y revocar el permiso; comprobar inicio/parada y
background; observar sesiones prolongadas, llamadas/interrupciones, micrófono
ocupado y que la configuración mostrada corresponde al hardware. Ningún modelo
queda validado por esta implementación o por las pruebas JVM.

## Factores de incompatibilidad

Filtrado agresivo, cancelación de eco, altavoz débil, micrófono filtrado, latencia variable, un canal útil, temperatura y restricciones del fabricante.

No usar “compatible con Android” como afirmación global. Publicar lista explícita de dispositivos probados.

## Gate full-duplex

El HONOR CRT-LX3 (Android 14, SDK 34, ABI principal arm64-v8a) tiene captura básica
validada. Sus valores diagnosticados de 48 kHz, 256 frames y hasta dos canales son
expectativas, no constantes. El laboratorio solicita mono, frecuencia natural,
LowLatency y Exclusive, pero muestra los valores negociados de cada stream y sus
IDs reales. Para promoverlo a “Full-duplex validado” deben verificarse ambos
streams Started, pulso único, silencio inicial, sesiones prolongadas, lifecycle,
rutas, interrupciones y xruns en hardware.
