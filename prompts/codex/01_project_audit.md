# Prompt 01 — Auditoría

Rama: `chore/project-audit`

Leé `AGENTS.md` y la documentación relevante. Auditá el proyecto sin implementar audio.

Objetivos:

1. Confirmar estructura Gradle, Kotlin, Compose, minSdk, targetSdk, compileSdk y versiones.
2. Confirmar JDK 17 y coherencia entre wrapper y Android Gradle Plugin.
3. Ejecutar:
   - `gradlew.bat test`
   - `gradlew.bat lint`
   - `gradlew.bat assembleDebug`
4. Revisar `.gitignore`.
5. Actualizar `docs/PROJECT_STATE.md` con el estado real.
6. No agregar dependencias.
7. No cambiar versiones salvo incompatibilidad comprobada.
8. No implementar UI, diagnóstico, micrófono, C++, NDK u Oboe.

Entregá hallazgos, cambios mínimos, pruebas, riesgos y próximos pasos.

Aceptación: build limpio, documentación fiel y sin cambios funcionales.
