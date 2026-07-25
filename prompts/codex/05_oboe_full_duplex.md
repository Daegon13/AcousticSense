# Prompt 05 — Full-duplex con Oboe

Rama: `feat/oboe-full-duplex`

Leé las reglas de tiempo real de `AGENTS.md`.

Integrá:

- NDK;
- CMake;
- Oboe;
- JNI;
- salida;
- entrada;
- full-duplex;
- buffers preasignados;
- xruns;
- metadatos;
- cierre y desconexión seguros.

Objetivo: sincronización y estabilidad, no ranging.

Restricciones del callback:

- no I/O;
- no logs frecuentes;
- no allocations;
- no locks bloqueantes;
- no DSP pesado;
- no UI.

UI Kotlin mínima: iniciar, detener, estado, sample rate, buffer y xruns.

Documentar qué requiere teléfono físico. No avanzar a distancia.
