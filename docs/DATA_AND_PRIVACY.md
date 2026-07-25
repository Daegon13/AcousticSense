# Datos y privacidad

## Principio

Procesamiento local por defecto.

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
