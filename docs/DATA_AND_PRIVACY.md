# Datos y privacidad

## Principio

Procesamiento local por defecto.

## Captura básica actual

`AudioRecord` entrega bloques PCM 16 únicamente mientras el usuario mantiene una
sesión activa. La aplicación calcula pico, RMS, dBFS, cantidad de muestras y
duración y reutiliza un buffer acotado. No conserva PCM, no crea WAV, no comparte
audio, no envía telemetría y no solicita almacenamiento ni acceso a Internet.

La exportación JSON existente contiene solamente el diagnóstico declarado por
Android; no incluye audio ni las muestras de la sesión.

## Datos posibles

PCM/WAV, dispositivo, configuración, distancia real, estimaciones, errores, timestamps y notas.

## No subir a Git

- grabaciones largas;
- conversaciones;
- voces identificables;
- rutas locales;
- datos personales;
- tokens;
- claves;
- `local.properties`.

## Estructura local

```text
test-data/recordings/
└── DEVICE/
    └── YYYY-MM-DD/
        └── SESSION_ID/
```

Usar identificadores sin nombres personales:

```text
pixel7_roomA_wall_100cm_run03
```

Exportar solo con confirmación explícita.

## Informes de laboratorio full-duplex

La sesión mantiene solo agregados acotados, configuraciones, estados, errores y
eventos. No conserva PCM, WAV ni muestras individuales. El informe JSON se crea
únicamente por acción explícita mediante el selector de documentos del sistema;
no hay guardado automático, telemetría, nube ni permiso de Internet.

El schema 1.1 conserva solo configuración solicitada/negociada, snapshots, agregados por ejecución y prueba, assertions, eventos, motivos y errores. No incluye PCM, WAV, audio codificado, muestras ni datos por callback. Los valores no soportados, incluido `xrunCount = -1`, se exportan como `null`.
