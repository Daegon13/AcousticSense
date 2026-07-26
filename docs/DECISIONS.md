# Decisiones

## ADR-001 — Android primero

Aceptada. Permite probar teléfonos físicos y usar audio nativo.

## ADR-002 — Kotlin y Compose

Aceptada para app, lifecycle y accesibilidad.

## ADR-003 — JDK 17

Aceptada para AGP 8.6.0 y Gradle 8.7.

## ADR-004 — C++/Oboe después del diagnóstico

Aceptada para no introducir complejidad antes de validar la base.

## ADR-005 — DSP antes de IA

Aceptada para entender el sensor.

## ADR-006 — Pared antes del MVP

Aceptada para demostrar el principio físico antes de UX final.

## ADR-007 — Procesamiento local

Aceptada durante PoC y MVP.

## ADR-008 — Distancia o zonas

Abierta. Se decidirá con evidencia.

## ADR-009 — Filtro adaptado sintético

Aceptada para la fase de DSP sintético: correlación cruzada normalizada, guard
interval para excluir la trayectoria directa y primer pico candidato que supere
un umbral experimental explícito. La confianza es un score de semejanza, no una
probabilidad calibrada. La selección para audio real permanece sujeta a evidencia.

## ADR-010 — Oboe por Prefab

Aceptada Oboe 1.10.0 (licencia Apache-2.0) desde Maven Central/Prefab. Evita
versionar una copia y ofrece una frontera estable para AAudio/OpenSL ES; su costo
es agregar descarga y toolchain NDK/CMake al build Android.

## ADR-011 — Salida como callback maestro full-duplex

Aceptada. Se abre salida antes que entrada y el callback de salida realiza una
lectura de entrada con timeout cero sobre un buffer preasignado. Exclusive cae a
Shared; `Unprocessed` cae a `VoiceRecognition`. La configuración real prevalece
sobre toda solicitud y se exporta como metadato.

## ADR-012 — Silencio y pulso de validación

Aceptada salida silenciosa por defecto. Solo una acción explícita habilita una
senoide audible de aproximadamente 1 kHz, amplitud digital 0,08 y hasta 100 ms.
No es chirp, no se repite, no representa SPL seguro y no se usa para ranging.

## ADR-013 — Batería con una única sesión

Aceptada una batería secuencial sobre una sola sesión full-duplex. Los chequeos
externos admiten confirmación manual y el JSON contiene agregados, nunca audio.

## ADR-014 — Evidencia inmutable antes del cierre

Aceptada. La configuración real y las métricas se capturan mientras los streams siguen válidos y se reutilizan tras el cierre. Assertions observables deciden resultados; una confirmación manual no convierte un fallo en aprobación. El informe evoluciona a schema 1.1.
