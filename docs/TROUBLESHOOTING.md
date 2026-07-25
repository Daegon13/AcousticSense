# Troubleshooting

## Gradle usa Java 8

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat --stop
.\gradlew.bat --version
```

Debe mostrar Java 17.

## No existe APK

La compilación falló antes. Ejecutar:

```powershell
.\gradlew.bat assembleDebug
```

y leer el primer error.

## ADB vacío

Revisar cable, depuración, RSA, driver y modo USB.

## No aparece `docs`

Cambiar la vista `Android` por `Project`.

## Codex hace demasiado

Detener y volver al prompt puntual: un PR, un objetivo y respetar `AGENTS.md`.
