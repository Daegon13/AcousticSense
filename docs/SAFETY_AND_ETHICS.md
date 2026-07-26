# Seguridad y ética

## Estado

Software experimental de laboratorio.

## Prohibiciones

No afirmar que:

- evita colisiones;
- reemplaza ayudas de movilidad;
- permite caminar sin supervisión;
- detecta tránsito;
- detecta escaleras o pozos;
- funciona con cualquier teléfono;
- tiene precisión garantizada.

## Primeras pruebas

Permitido:

- teléfono fijo;
- usuario vidente;
- pared;
- objetos acolchados;
- habitación controlada;
- acompañamiento.

No permitido:

- ojos vendados;
- calle;
- escaleras;
- tránsito;
- balcones;
- bordes;
- confianza exclusiva en el prototipo.

## Fallos

Ante incertidumbre:

- mostrar “sin medición confiable”;
- no reutilizar una distancia vieja;
- detener feedback engañoso;
- registrar el error;
- permitir parada inmediata.

## Sonido

Antes de uso prolongado estudiar volumen, banda, audibilidad, repetición, fatiga, mascotas, audífonos e interferencia ambiental.

## Privacidad

El micrófono puede capturar voces. No subir automáticamente, exportar solo mediante acción explícita, informar qué se guarda y permitir borrar sesiones.

En la captura básica, la sesión solo comienza mediante una acción explícita y se
detiene al salir de la aplicación. Las muestras se procesan en bloques acotados:
no se guardan, comparten ni exportan. Probar con visión disponible, el teléfono
fijo y sin capturar conversaciones deliberadamente. El nivel dBFS mostrado es
digital y no equivale a SPL ni demuestra detección de obstáculos.

## Usuarios

La validación con personas ciegas debe ocurrir después de gates técnicos y con especialistas en orientación y movilidad.

## Pulso de validación full-duplex

La salida permanece silenciosa hasta la acción explícita. El único estímulo es
una senoide audible aproximada de 1 kHz, amplitud digital 0,08 y duración máxima
aproximada de 100 ms; se cancela al detener y no se repite automáticamente. Esa
amplitud no es SPL ni garantiza un nivel acústico seguro. Probar con volumen bajo,
teléfono fijo, visión disponible y sin usar el prototipo como ayuda de movilidad.

La pérdida de primer plano o el bloqueo de pantalla detienen captura y emisión; volver a la aplicación nunca las reinicia. Pérdida de permiso, desconexión u otra interrupción conservan su motivo real y no cuentan como éxito de lifecycle.
