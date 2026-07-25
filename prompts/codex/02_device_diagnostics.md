# Prompt 02 — Diagnóstico

Rama: `feat/device-diagnostics`

Leé `AGENTS.md`, `PROJECT_STATE`, `ARCHITECTURE` y `DEVICE_COMPATIBILITY`.

Implementá una pantalla Compose que muestre:

- fabricante;
- modelo;
- producto/dispositivo;
- versión Android;
- SDK;
- ABI;
- sample rate de salida sugerido;
- frames por buffer;
- dispositivos de entrada y salida;
- canales reportados cuando sea posible;
- timestamp;
- versión de app.

Permitir exportar JSON por acción explícita.

Requisitos:

- separar recolección, modelo y UI;
- tests de serialización y formateo;
- estados de error;
- TalkBack;
- no pedir permiso de micrófono;
- no grabar;
- no C++;
- no Oboe;
- no inventar capacidades desconocidas.

Actualizar estado y compatibilidad.

Ejecutar test, lint y assembleDebug. Informar limitaciones.
