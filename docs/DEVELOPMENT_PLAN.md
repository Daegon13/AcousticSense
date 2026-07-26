# Plan de desarrollo

## Principio

No construir el MVP antes de validar el sensor.

## Fase 0 — Base

Entregables: Git, documentación, JDK 17, build en verde, reglas de Codex y plantilla de PR.

Tiempo orientativo: 1 a 2 días.

## Fase 1 — Diagnóstico

Entregables:

- fabricante;
- modelo;
- Android;
- sample rate;
- frames por buffer;
- entradas y salidas;
- canales;
- JSON;
- pruebas de serialización.

No incluye micrófono, C++, Oboe, chirps ni distancia.

Tiempo: 3 a 7 días.

## Fase 2 — DSP sintético

Entregables:

- generador de chirp;
- simulador de eco;
- ruido;
- múltiples reflexiones;
- correlación;
- picos;
- distancia;
- gráficas;
- tests.

Gate: recuperar retardos sintéticos conocidos.

Tiempo: 3 a 7 días.

## Fase 3 — Captura Android

Permiso, estados, captura PCM, iniciar/detener y errores visibles.

Gate: sesiones estables y analizables.

Tiempo: 3 a 7 días.

## Fase 4 — Full-duplex nativo

NDK, CMake, Oboe, JNI, streams, callbacks, buffers, xruns y cierre seguro.

Gate: sesión de varios minutos sin fallos recurrentes.

Tiempo: 1 a 3 semanas.

## Fase 5 — Chirp y exportación

Chirp, timestamps, WAV, JSON/CSV, metadatos y análisis offline.

Gate: cada chirp aparece de forma repetible en la captura.

Tiempo: 3 a 7 días.

## Fase 6 — Calibración

Medir acople, offset, volumen, orientación, funda, sesiones y temperatura.

Gate: offset suficientemente estable o estrategia explícita para invalidar.

Tiempo: 1 a 2 semanas.

## Fase 7 — Pared

Distancias:

- 0,50 m;
- 1,00 m;
- 1,50 m;
- 2,00 m;
- 2,50 m;
- 3,00 m.

Mínimo 30 mediciones por punto.

Métricas:

- MAE;
- mediana;
- P95;
- detección;
- falsos positivos;
- estabilidad;
- latencia.

Gate inicial:

- detección ≥ 90 % entre 0,50 y 2,50 m;
- error mediano ≤ 0,15 m;
- sin saltos falsos frecuentes.

Si la distancia exacta falla pero las zonas son estables, reformular. Si la señal no supera referencias básicas, detener.

Tiempo: 1 a 3 semanas.

## Fase 8 — Objetos

Pared, puerta, armario, caja, persona, mesa, silla, sofá, cortina, objeto estrecho y superficies inclinadas.

Variar distancia, ángulo, material, ruido, movimiento, orientación, funda, batería y temperatura.

Tiempo: 1 a 3 semanas.

## Fase 9 — Háptica

Zonas lejos/medio/cerca/crítico, confianza insuficiente y parada.

Tiempo: 3 a 7 días.

## Fase 10 — Accesibilidad

TalkBack, foco, controles grandes, uso con una mano, mensajes claros y prueba supervisada.

Tiempo: 1 a 2 semanas.

## Fase 11 — MVP cerrado

Incluye Android, uno a tres modelos validados, calibración, sensor frontal, distancia o zonas, confianza, vibración, TalkBack y exportación.

Tiempo acumulado orientativo: 10 a 16 semanas a tiempo parcial.

El comportamiento del hardware domina el cronograma.

### Estado de fase 4

Implementación de Oboe/JNI, silencio, pulso audible limitado, métricas y sesiones
guiadas completada en código. El gate de varios minutos y los cambios de ruta
siguen pendientes de teléfono físico; no habilita la fase 5.

### Estado de fase 5

El generador, límites de repetición y formato de evidencia están implementados para revisión. El gate sigue siendo observar cada chirp repetidamente en ventanas exportadas de un teléfono fijo; no habilita calibración antes de esa validación.
