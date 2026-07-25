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

## Condición para avanzar

```powershell
.\gradlew.bat test
.\gradlew.bat lint
.\gradlew.bat assembleDebug
```

deben permanecer exitosos.
