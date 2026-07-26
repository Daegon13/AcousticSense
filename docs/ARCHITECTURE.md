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

El laboratorio modular en `analysis/` contiene generación de chirp, simulación de
trayectorias sintéticas, correlación normalizada, detección, conversión de retardo
a distancia, métricas y gráficas. El script CLI solo orquesta esos módulos. La
carga de WAV/PCM real queda diferida.

Para la fase sintética se selecciona un filtro adaptado mediante correlación
cruzada normalizada. Un guard interval configurable rechaza la trayectoria
directa; se busca el primer pico candidato y se informa explícitamente ausencia
de eco cuando no supera el umbral experimental. La confianza es semejanza
normalizada, no una probabilidad ni garantía de funcionamiento físico.

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
