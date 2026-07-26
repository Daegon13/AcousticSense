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

La captura básica ya permite observar métricas digitales en vivo sin guardar
audio: fuente solicitada/seleccionada, configuración real, pico, RMS, dBFS,
muestras y duración. Antes de estudiar respuesta emitida, verificar en un
teléfono fijo sesiones de captura, permiso, parada por lifecycle y estabilidad.
Esta fase no reproduce señales, no exporta PCM y no permite inferir ecos ni
distancias; por eso no completa todavía este experimento.

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

## Protocolo full-duplex previo a latencia

Con teléfono apoyado, visión disponible, Bluetooth/auriculares desconectados,
volumen bajo y ambiente silencioso: ejecutar en orden `silent_session`,
`audible_pulse`, `repeated_start_stop`, `stability_session` y
`lifecycle_manual_check`. Las acciones externas se confirman manualmente. Exportar
el JSON por selector de documentos y registrar build, configuración negociada,
xruns, frames, callbacks y eventos; nunca PCM. Un fallo no cancela los resultados
anteriores, pero desconexión detiene los streams y requiere reintento manual.

La estabilidad dura 300.000 ms y no admite aprobación anticipada. El chequeo de lifecycle solo pasa si se observa `APP_BACKGROUND`, el motor queda detenido, ambos streams están cerrados y no hubo reinicio. Una confirmación manual no puede contradecir estas assertions. Abortar queda registrado y no invalida resultados anteriores.

## Protocolo de chirp y captura acotada

Teléfono fijo, visión disponible, ambiente silencioso, volumen bajo, Bluetooth apagado, auriculares desconectados y sin hablar: iniciar streams, revisar 4–12 kHz/10 ms/Hann/0,05, emitir manualmente y exportar explícitamente. La ventana objetivo es 50 ms de pre-roll y 250 ms de post-roll. Registrar frames y timestamps crudos; no tratarlos como relojes sincronizados ni convertirlos en distancia.
