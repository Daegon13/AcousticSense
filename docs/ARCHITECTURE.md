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

### Captura básica Android (fase 3)

```text
CaptureScreen (Compose)
        ↓ acciones / estado resumido
CaptureViewModel
        ↓ interfaz reemplazable
AudioCaptureEngine
        ↓ implementación Android en ejecutor único
AudioRecord (mono, PCM 16, 48 kHz solicitados)
        ↓ bloques reutilizables
AudioMetrics → pico, RMS, dBFS, muestras y duración
```

`MicrophonePermissionManager` separa la evaluación del permiso de la UI. El
motor mantiene un único dueño de `AudioRecord`, lee fuera del hilo principal,
limita las publicaciones a aproximadamente 10 Hz y libera el recurso al parar,
ante errores y al cerrar el `ViewModel`. Pasar la aplicación a segundo plano
detiene la sesión. La interfaz permite reemplazar el motor por uno falso en JVM.

La configuración solicitada no se presenta como garantía: se intenta 48 kHz,
mono y PCM 16; `UNPROCESSED` solo se intenta si Android declara soporte y `MIC`
es el fallback. La pantalla informa fuente finalmente seleccionada, propiedades
reales del `AudioRecord` y buffer usado. Esta capa no reproduce audio, no guarda
muestras y no realiza ranging.

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

## Laboratorio full-duplex (fase 4)

```text
DuplexScreen → DuplexViewModel → DuplexEngine → NativeDuplexEngine/JNI
                                              → DuplexEngine C++
output callback maestro → input.read(timeout=0) → agregados atómicos
                       ↘ silencio / TestSignal acotada
LaboratorySessionRunner → batería secuencial → SessionJsonSerializer
```

La salida se abre primero en Exclusive y cae a Shared; su frecuencia negociada
se solicita a la entrada. `Unprocessed` cae explícitamente a `VoiceRecognition`.
El callback reutiliza un buffer fijo, no llama a JNI y no hace I/O. El snapshot
se consulta fuera del callback. Una sola sesión posee ambos streams y se cierra
al detener, perder permiso o pasar a segundo plano.

Una única sesión posee ambos streams. Un observer propiedad de la Activity detiene en `ON_STOP`, distingue bloqueo de pantalla y background, y nunca reinicia al regresar. Se conservan la configuración solicitada, la negociada y las métricas al inicio/fin de cada prueba. El snapshot posterior al cierre reutiliza esos valores y nunca consulta un stream destruido.
