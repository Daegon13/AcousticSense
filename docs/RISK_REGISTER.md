# Registro de riesgos

| Riesgo | Impacto | Probabilidad | Mitigación |
|---|---|---:|---|
| Java/Gradle | Medio | Baja | JDK 17 y CI |
| Cancelación elimina ecos | Alto | Alta | diagnóstico y pruebas |
| Latencia variable | Alto | Alta | timestamps, calibración, zonas |
| Diferencias entre teléfonos | Alto | Alta | lista cerrada |
| Acople directo | Alto | Alta | calibración y rechazo |
| Objetos blandos | Alto | Alta | matriz y límites |
| Objetos pequeños | Alto | Alta | exclusión |
| Movimiento | Alto | Media | estático primero |
| Ruido | Medio | Alta | filtros y confianza |
| Batería/temperatura | Medio | Media | perfiles |
| Señal molesta | Alto | Media | medición y revisión |
| Mascotas | Alto | Desconocida | prudencia y límites |
| Falsa seguridad | Crítico | Alta | UX y lenguaje |
| Voces grabadas | Alto | Media | local y exportación explícita |
| Codex amplía alcance | Medio | Alta | AGENTS y PRs pequeños |
