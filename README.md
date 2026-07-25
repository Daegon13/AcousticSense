# Acoustic Sense

Acoustic Sense es un proyecto de investigación para comprobar si un teléfono Android convencional puede estimar la proximidad de obstáculos frontales grandes mediante señales acústicas emitidas por su altavoz y capturadas como ecos por sus micrófonos.

## Estado

**Prueba de concepto de laboratorio.**

Todavía no es una ayuda de movilidad certificada y no reemplaza un bastón blanco, un perro guía ni el entrenamiento profesional de orientación y movilidad.

## Hipótesis

Un teléfono Android compatible podría:

1. Emitir un chirp acústico conocido.
2. Grabar simultáneamente la señal directa y sus reflexiones.
3. Detectar retrasos mediante correlación o filtro adaptado.
4. Compensar la latencia interna del dispositivo.
5. Estimar una distancia o zona de proximidad.
6. Comunicarla mediante interfaz visual y, después, vibraciones accesibles.

## Stack previsto

- Kotlin y Jetpack Compose.
- Gradle Kotlin DSL.
- JDK 17 para la configuración actual.
- Android SDK.
- C++ mediante Android NDK.
- CMake.
- Oboe para audio de baja latencia.
- Python, NumPy, SciPy y Matplotlib para análisis offline.

## Validación base

```powershell
.\gradlew.bat test
.\gradlew.bat lint
.\gradlew.bat assembleDebug
```

## Orden de desarrollo

1. Auditoría del proyecto.
2. Diagnóstico del teléfono.
3. DSP sintético.
4. Captura de micrófono.
5. Full-duplex con Oboe.
6. Chirp y exportación.
7. Calibración.
8. Prueba de pared.
9. Registro experimental.
10. Feedback háptico.
11. Accesibilidad.
12. MVP experimental cerrado.

Comenzá por `docs/PROJECT_STATE.md` y `prompts/codex/README.md`.
