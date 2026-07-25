# Alcance del producto

## Problema

Una persona ciega o con baja visión puede beneficiarse de información adicional sobre la proximidad de obstáculos, especialmente cuando no hay luz o no se desea depender permanentemente de una cámara.

## Propuesta

Convertir un teléfono Android compatible en un sensor acústico frontal experimental:

```text
altavoz → señal → objeto → eco → micrófono
                              ↓
                       procesamiento local
                              ↓
                    distancia o zona estimada
                              ↓
                         vibración o aviso
```

## Usuario inicial

Durante las primeras fases, el usuario es el equipo de desarrollo. No es todavía un producto para uso autónomo.

## Valor buscado

- Sin hardware externo obligatorio.
- Sin depender de la iluminación.
- Procesamiento local.
- Posible uso en teléfonos convencionales.
- Feedback háptico.

## Hipótesis

En teléfonos calibrados podría detectarse una superficie frontal grande y estimar:

- distancia aproximada; o
- zona: lejos, medio, cerca, crítico.

## Fuera del alcance inicial

- reconocer objetos;
- describir escenas;
- mapas;
- navegación;
- 360 grados;
- desniveles;
- tráfico;
- compatibilidad universal;
- IA para compensar un sensor inestable.

La primera pregunta no es si la interfaz se ve bien, sino si la señal contiene información reproducible.
