# Estrategia de pruebas

## Unitarias

Diagnóstico, serialización, tiempo-distancia, ventanas, correlación, picos, confianza, suavizado y estados.

## Integración

Kotlin/JNI, streams, exportación, lifecycle, pérdida de permiso y errores.

## Regresión

Cada fallo real debe convertirse en fixture sintético o grabación mínima anonimizada.

## Físicas

Distancias conocidas, habitaciones, objetos, días, batería y temperatura.

## Accesibilidad

TalkBack, foco, etiquetas, controles, contraste, mensajes y vibraciones.

## Comandos

```powershell
.\gradlew.bat test
.\gradlew.bat lint
.\gradlew.bat assembleDebug
```

## No cuenta como prueba

- “abrió una vez”;
- “Codex dijo que funciona”;
- “compila en la nube”;
- una sola medición;
- una gráfica sin métrica.
