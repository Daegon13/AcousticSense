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
