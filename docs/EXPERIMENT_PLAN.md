# Plan experimental

## Regla

Todo resultado debe relacionarse con commit, dispositivo, configuración, datos, distancia real y protocolo.

## Experimento 1 — Sintético

Variar distancia, ruido, amplitud, ecos secundarios, absorción y sample rate.

Implementado en `analysis/` con semillas configurables. La matriz automatizada
incluye 0,50 m, 1,00 m, 2,00 m, ruido moderado, baja amplitud, múltiples ecos,
trayectoria directa fuerte y ausencia de eco. La tolerancia de recuperación es
0,005 m para las distancias nominales con la configuración de prueba. Registrar
sample rate, velocidad del sonido, semilla y parámetros de cada nueva serie; los
resultados sintéticos no habilitan por sí solos pruebas de movilidad.

## Experimento 2 — Respuesta del teléfono

Registrar espectro emitido y grabado, clipping, compresión, ruido, efectos y estabilidad.

## Experimento 3 — Latencia

Medir distribución del offset entre repeticiones.

## Experimento 4 — Pared fija

Teléfono inmóvil, altura fija, perpendicular, cinta métrica, misma configuración y 30 mediciones por punto.

## Experimento 5 — Ángulo

0°, 15°, 30° y 45°.

## Experimento 6 — Material

Yeso, madera, vidrio, tela, cuerpo humano y espuma.

## Experimento 7 — Movimiento

Solo tras validar estático: movimiento lento, oscilación de mano y paso controlado, siempre con visión y acompañamiento.

## Invalidez

Invalidar si:

- hubo interrupción;
- cambió orientación o volumen sin registro;
- la distancia real es incierta;
- hubo clipping severo;
- faltan metadatos;
- el commit no está identificado.

Después de pared, registrar en `DECISIONS.md` si se continúa, reformula, restringe, rediseña o detiene.
