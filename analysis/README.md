# Laboratorio DSP sintético

Este laboratorio valida solamente un modelo sintético: genera un chirp lineal,
inserta copias retardadas y ruido gaussiano, aplica un filtro adaptado y convierte
el retardo de ida y vuelta en distancia. No usa micrófono, altavoz ni hardware
Android y sus resultados no demuestran funcionamiento en un teléfono real.

## Modelo y algoritmo

```text
delay_seconds = (2 * distance_meters) / speed_of_sound_mps
delay_samples = round(delay_seconds * sample_rate_hz)
distance_meters = delay_samples * speed_of_sound_mps / (2 * sample_rate_hz)
```

La demostración usa por defecto 343 m/s. La velocidad del sonido es configurable
porque cambia con la temperatura y otras condiciones ambientales. La señal de
ejemplo (4–12 kHz, 48 kHz de sample rate) es una configuración de laboratorio,
no una selección definitiva para el producto ni para un teléfono.

La detección calcula correlación cruzada normalizada (filtro adaptado), ignora un
intervalo inicial configurable para rechazar la trayectoria directa y elige el
primer máximo local candidato. `confidence` es el coeficiente normalizado de ese
pico, entre 0 y 1: es un indicador experimental de semejanza, **no** una
probabilidad calibrada. Por debajo de 0,35 se devuelve explícitamente que no hay
eco confiable.

## Instalación y pruebas

Linux/macOS, desde la raíz del repositorio:

```bash
python -m venv .venv
source .venv/bin/activate
python -m pip install -e "./analysis[test]"
python -m pytest analysis/tests
python analysis/scripts/run_synthetic_demo.py --distance 1.0 --noise 0.02
```

Windows PowerShell, desde la raíz:

```powershell
py -m venv .venv
.\.venv\Scripts\Activate.ps1
py -m pip install -e ".\analysis[test]"
py -m pytest analysis/tests
py analysis/scripts/run_synthetic_demo.py --distance 1.0 --noise 0.02
```

`--seed` hace reproducible el ruido y `--speed-of-sound` permite ajustar el
modelo. Las gráficas se guardan en `analysis/output/`, ignorado por Git.

## Dependencias

- NumPy: arreglos, ruido determinista y métricas.
- SciPy: chirp, ventanas y correlación FFT probadas.
- Matplotlib: gráfica reproducible de emisión, recepción, correlación y pico.
- pytest (extra `test`): pruebas automatizadas; no es dependencia de ejecución.

## Límites

El simulador suma trayectorias ideales y ruido gaussiano. No modela respuesta en
frecuencia, latencia variable, reverberación real, clipping, AGC, geometría del
teléfono ni condiciones ambientales completas. La confianza y las tolerancias
sintéticas deberán contrastarse y calibrarse más adelante con mediciones físicas
controladas, con visión disponible y sin confiar en el prototipo para movilidad.
