# Arquitectura

```text
Jetpack Compose
      ↓
Controlador Kotlin
      ↓
JNI
      ↓
Motor C++ con Oboe
      ↓
Entrada + salida full-duplex
      ↓
Buffers
      ↓
DSP
      ↓
eco + distancia + confianza
      ↓
estado Kotlin
      ↓
UI / vibración
```

## App

Permisos, lifecycle, controles, diagnóstico, TalkBack, almacenamiento y exportación.

## Motor de audio

Streams, formatos, sample rate, callbacks, timestamps, buffers, xruns y cierre.

## DSP

Chirp, ventanas, filtrado, correlación, rechazo directo, picos, compensación, estimación, confianza y suavizado.

## Python

Carga de WAV/PCM, reproducción offline, gráficas, comparación con ground truth y métricas.

## Concurrencia

- No I/O en callback.
- No actualizar Compose desde callback.
- Buffers preasignados.
- Exportación en hilo separado.
- Estados resumidos para UI.

## Perfil por dispositivo

Puede contener sample rate, burst, canales, orientación, volumen, offset, banda útil, parámetros y commit.

## Estados

- no inicializado;
- diagnosticando;
- listo;
- calibrando;
- midiendo;
- baja confianza;
- error;
- detenido.

## Decisiones diferidas

Banda final, repetición, FFT o correlación, distancia o zonas, número de micrófonos y módulos físicos.
