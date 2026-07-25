# AGENTS.md

## Identidad

- Proyecto: Acoustic Sense.
- Etapa: prueba de concepto de laboratorio.
- Plataforma inicial: Android.
- Objetivo inmediato: validar si un teléfono compatible puede detectar de forma repetible la proximidad de superficies frontales grandes mediante sonido reflejado.
- Este repositorio no contiene todavía un dispositivo de movilidad certificado.

## Fuentes de verdad

Antes de modificar código, leer:

1. `docs/PROJECT_STATE.md`
2. `docs/PRODUCT_SCOPE.md`
3. `docs/DEVELOPMENT_PLAN.md`
4. `docs/ARCHITECTURE.md`
5. `docs/EXPERIMENT_PLAN.md`
6. `docs/SAFETY_AND_ETHICS.md`
7. El prompt puntual del PR.

`AGENTS.md` define reglas generales. `docs/DECISIONS.md` registra decisiones aceptadas. El prompt actual limita el alcance.

## Tecnologías

- Kotlin.
- Jetpack Compose.
- Gradle Kotlin DSL.
- Android nativo.
- C++, Android NDK, CMake y Oboe cuando corresponda.
- JNI como frontera Kotlin/C++.
- Python, NumPy, SciPy y Matplotlib para análisis fuera de la app.

## Orden obligatorio

1. Base y calidad.
2. Diagnóstico.
3. DSP sintético.
4. Captura básica.
5. Full-duplex.
6. Exportación.
7. Calibración.
8. Pared.
9. Objetos.
10. Háptica.
11. Accesibilidad.
12. MVP cerrado.

No saltar a mapas, SLAM, navegación, clasificación, IA, cámara, LiDAR, backend, cuentas, nube o publicación abierta.

## Ingeniería

- Un objetivo medible por PR.
- No mezclar UI, audio, DSP y refactors amplios sin necesidad.
- No reescribir archivos no relacionados.
- No agregar dependencias sin justificar propósito y costo.
- Preferir algoritmos deterministas antes de IA.
- Toda corrección DSP debe incluir prueba sintética o fixture de regresión.
- No ocultar baja confianza.
- No silenciar errores de build, lint o tests.
- No introducir optimizaciones prematuras.
- Mantener procesamiento local durante PoC y MVP.

## Audio en tiempo real

Dentro del callback:

- no hacer I/O;
- no bloquear;
- no reservar memoria;
- no registrar logs frecuentes;
- no usar locks bloqueantes;
- no actualizar UI;
- usar buffers preasignados.

## Seguridad

Nunca afirmar que la aplicación:

- reemplaza bastón o perro guía;
- permite navegación independiente segura;
- detecta todos los obstáculos;
- detecta escaleras descendentes o pozos;
- funciona en todos los teléfonos;
- tiene precisión garantizada;
- está certificada.

Las primeras pruebas deben ser con visión disponible, teléfono fijo, entorno controlado, distancias medidas y sin caminar a ciegas.

## Datos

- No subir grabaciones personales.
- No versionar WAV/PCM grandes.
- No capturar conversaciones deliberadamente.
- No incluir claves, tokens, rutas locales ni `local.properties`.
- Usar datos mínimos y anonimizados.

## Validaciones

```powershell
.\gradlew.bat test
.\gradlew.bat lint
.\gradlew.bat assembleDebug
```

Para cambios nativos, compilar el target y reportar ABI, warnings y verificaciones no ejecutadas.

## Entrega de Codex

Al terminar:

1. Resumen.
2. Archivos modificados.
3. Decisiones.
4. Pruebas y resultados.
5. Riesgos.
6. Qué no fue probado físicamente.
7. Próximo paso sugerido, sin implementarlo.
