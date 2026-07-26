# Compatibilidad

No existe ningún teléfono validado todavía.

## Estados

- No evaluado.
- Diagnóstico disponible.
- Captura validada.
- Full-duplex validado.
- Pared experimental.
- Zonas experimentales.
- Rechazado.

| Fabricante | Modelo | Android | Sample rate | Canales | Raw | Estado | Notas |
|---|---|---:|---:|---:|---|---|---|
| Pendiente | Pendiente | — | — | — | — | No evaluado | — |

## Datos disponibles en el diagnóstico

La aplicación puede exportar un JSON con fabricante, modelo, producto/dispositivo,
versión Android, SDK, ABI, versión de la aplicación, sample rate de salida sugerido,
frames por buffer y los dispositivos de entrada y salida que Android declara. Para
cada dispositivo de audio se registran identificador, nombre, tipo y la cantidad
máxima de canales reportada cuando está disponible, además del timestamp.

Un valor ausente se conserva como `null`, `unknown` o `unavailable`, según el
contexto. Estos datos son inventario declarado por las APIs de Android: no prueban
captura, full-duplex, latencia, respuesta acústica ni compatibilidad experimental.

## Factores de incompatibilidad

Filtrado agresivo, cancelación de eco, altavoz débil, micrófono filtrado, latencia variable, un canal útil, temperatura y restricciones del fabricante.

No usar “compatible con Android” como afirmación global. Publicar lista explícita de dispositivos probados.
