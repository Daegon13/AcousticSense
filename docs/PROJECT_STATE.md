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

## Todavía no realizado

- Motor acústico en Android.
- Chirp emitido o capturado por hardware real.
- Grabación funcional.
- C++/NDK/Oboe.
- Algoritmo de distancia.
- Calibración.
- Prueba de pared.
- Feedback háptico.
- Validación con usuarios.
- Validación del diagnóstico en teléfonos físicos.

## Próximo PR

Validar el laboratorio sintético y, solo después de mantener los gates, preparar
la fase de captura básica definida por el plan. La emisión, captura y detección
acústica en teléfonos siguen sin validación física.

Prompt actual completado: `prompts/codex/03_synthetic_dsp.md`.

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
