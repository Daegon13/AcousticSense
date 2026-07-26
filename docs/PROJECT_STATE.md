# Estado actual del proyecto

Fecha base: 25 de julio de 2026.

## Ya realizado

- Proyecto Android nativo creado.
- Módulo `app` generado.
- Gradle Wrapper disponible.
- Android Gradle Plugin 8.6.0.
- Gradle 8.7.
- JDK 17 configurado.
- Estructura documental creada.
- Auditoría inicial completada.
- Pantalla Android de diagnóstico implementada con estados de carga, éxito y error.
- Recolección de datos de sistema, aplicación y dispositivos de audio declarados por Android.
- Exportación explícita del diagnóstico como JSON mediante el selector de documentos del sistema.
- Pruebas unitarias de serialización y formateo agregadas.
- Laboratorio Python de DSP sintético agregado: chirp configurable, simulación de
  múltiples ecos y ruido, filtro adaptado, estimación de distancia, métricas,
  gráfica reproducible y pruebas automatizadas.
- Captura básica Android implementada con permiso en runtime, `AudioRecord`,
  inicio y parada explícitos, métricas PCM acotadas y cierre por lifecycle.

## Todavía no realizado

- Motor acústico en Android.
- Chirp emitido o capturado por hardware real.
- Validación física de la captura en teléfonos reales.
- C++/NDK/Oboe.
- Algoritmo de distancia.
- Calibración.
- Prueba de pared.
- Feedback háptico.
- Validación con usuarios.
- Validación del diagnóstico en teléfonos físicos.

## Próximo PR

Validar en teléfonos físicos que la captura básica sea estable y que la
configuración real coincida con la informada. No avanzar a full-duplex hasta
completar ese gate; todavía no hay emisión ni detección acústica real.

Prompt actual completado: `prompts/codex/04_microphone_capture.md`.

## Condición para avanzar habilitada

```powershell
.\gradlew.bat test
.\gradlew.bat lint
.\gradlew.bat assembleDebug
```

deben permanecer exitosos.


## Validación

### Entorno de Codex

- JDK 17 disponible.
- Android SDK no disponible.
- El wrapper no pudo descargar Gradle 8.7 porque el proxy respondió HTTP 403.
- `test`, `lint` y `assembleDebug` no se consideran ejecutados en ese entorno.

### Entorno local Windows

- Android Studio y Android SDK configurados.
- Gradle ejecutado con JDK 17.
- `.\gradlew.bat test`: exitoso.
- `.\gradlew.bat lint`: exitoso.
- `.\gradlew.bat assembleDebug`: exitoso.
- APK generado en `app/build/outputs/apk/debug/app-debug.apk`.
- No se realizaron todavía pruebas físicas del subsistema acústico.

## Fase 4 — Full-duplex implementado (corrección pendiente de revalidación física)

Se integró Oboe 1.10.0 mediante Prefab, NDK/CMake, C++17 y JNI. El laboratorio
abre salida e entrada, conserva silencio por defecto, ofrece un pulso explícito
de 1 kHz/100 ms con amplitud digital 0,08 y exporta solo configuración y métricas.
En un HONOR CRT-LX3/Android 14 se comprobaron entrada, salida y pulso audible. Esa
prueba detectó que el motor seguía activo en segundo plano y que la exportación
consultaba streams ya destruidos. La implementación conserva ahora snapshots
antes del cierre, detiene en `ON_STOP` y evalúa resultados mediante assertions;
estos cambios todavía requieren revalidación física. No se habilita chirp ni ranging.
