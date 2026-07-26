# Métricas y aceptación

## Exactitud

MAE, error mediano, RMSE cuando aporte, P95 y sesgo.

## Detección

Precisión, recall, falsos positivos, falsos negativos y tasa válida.

## Estabilidad

Desviación entre repeticiones, saltos falsos, deriva térmica y entre reinicios.

## Rendimiento

Latencia, xruns, CPU, memoria, batería y temperatura.

## Gate inicial de pared

Entre 0,50 y 2,50 m:

- detección ≥ 90 %;
- error mediano ≤ 0,15 m;
- P95 documentado;
- sin saltos críticos frecuentes.

## Alternativa de zonas

Usar matriz de confusión para lejos, medio, cerca y crítico. Los límites se deciden con datos reales.

La confianza debe correlacionarse con error; no usar porcentajes arbitrarios.

## Gate de chirp y captura

Por emisión registrar peak, RMS, dBFS, mínimo, máximo, media, muestras, clipping, frames esperados/reales, interrupciones y xruns. Aceptar esta fase solo si el chirp aparece repetiblemente en la captura exportada, sin overflow ni clipping relevante y con metadatos completos. dBFS es nivel digital, no SPL; no se evalúa eco ni distancia.
