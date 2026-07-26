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
- Próximo paso: auditoría y diagnóstico del dispositivo.

## Todavía no realizado

- Motor acústico.
- Chirp.
- Grabación funcional.
- C++/NDK/Oboe.
- Algoritmo de distancia.
- Calibración.
- Prueba de pared.
- Feedback háptico.
- Validación con usuarios.

## Próximo PR 

`feat/device-diagnostics`

Prompt: `prompts/codex/02_device_diagnostics.md`.

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