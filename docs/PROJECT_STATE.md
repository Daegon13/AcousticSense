# Estado actual del proyecto

Fecha de auditoría: 26 de julio de 2026.

## Resultado de la auditoría base

- Proyecto Android nativo de un único módulo `app`, con Kotlin y Jetpack Compose.
- `compileSdk = 34`, `targetSdk = 34` y `minSdk = 26`.
- Android Gradle Plugin 8.6.0, Kotlin 1.9.0 y Compose Compiler 1.5.1.
- Gradle Wrapper 8.7, coherente con Android Gradle Plugin 8.6.0.
- JDK 17 requerido. La ruta local de Android Studio fue retirada de
  `gradle.properties` para que cada entorno use su `JAVA_HOME`.
- Código Java/Kotlin generado para bytecode 1.8, independientemente del JDK 17
  usado para ejecutar Gradle.
- El wrapper de Unix está marcado como ejecutable.
- `.gitignore` cubre configuración local, salidas de Gradle/Android, artefactos
  nativos y capturas experimentales WAV/PCM.
- No hay permisos de micrófono, código nativo ni funciones acústicas.

## Validación en el entorno de auditoría

- JDK 17 disponible y confirmado al ejecutar el wrapper.
- Android SDK no disponible: no están definidos `ANDROID_HOME` ni
  `ANDROID_SDK_ROOT`, no existe `local.properties` y no se encontró una
  instalación del SDK.
- `./gradlew test`, `./gradlew lint` y `./gradlew assembleDebug` fueron
  iniciados, pero el wrapper no pudo descargar Gradle 8.7 porque el proxy del
  entorno respondió HTTP 403. Por lo tanto, ninguna de las tres validaciones
  llegó a ejecutar Gradle ni se considera aprobada. Aun con la distribución
  disponible, este entorno seguiría necesitando un Android SDK para compilar.
- No se ejecutaron pruebas instrumentadas ni pruebas físicas en un teléfono.

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

## Próximo paso condicionado

`feat/device-diagnostics`

Prompt: `prompts/codex/02_device_diagnostics.md`.

No debe comenzar hasta configurar JDK 17 y Android SDK en una computadora local
y obtener resultados exitosos para las tres validaciones base.

## Condición para avanzar

```powershell
.\gradlew.bat test
.\gradlew.bat lint
.\gradlew.bat assembleDebug
```

deben permanecer exitosos.
