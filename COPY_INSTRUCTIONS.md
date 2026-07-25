# Cómo instalar este paquete

Extraé el contenido del ZIP directamente en la raíz del repositorio Android:

```text
C:\Users\Usuario\Documents\GitHub\AcousticSense
```

Es la carpeta donde ya existen `app`, `gradle`, `settings.gradle.kts`, `gradlew` y `gradlew.bat`.

## Explorador de Windows

1. Cerrá los archivos de documentación abiertos en Android Studio.
2. Abrí el ZIP y copiá todo su contenido.
3. Pegalo dentro de la raíz de `AcousticSense`.
4. Cuando Windows pregunte, reemplazá los archivos vacíos que creaste anteriormente.
5. En Android Studio cambiá la vista `Android` por `Project`.
6. Usá `File > Synchronize` o `Reload from Disk`.

## PowerShell

```powershell
$Zip = "$HOME\Downloads\AcousticSense_Documentacion_Completa.zip"
$Project = "$HOME\Documents\GitHub\AcousticSense"

Expand-Archive -Path $Zip -DestinationPath $Project -Force
Set-Location $Project
git status
```

No reemplaces el `.gitignore` generado por Android Studio. Copiá el contenido de `.gitignore.append.txt` al final del `.gitignore` real.
