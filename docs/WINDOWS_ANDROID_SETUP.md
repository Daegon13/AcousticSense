# Windows y Android Studio

## Requisitos

Windows 10/11, Android Studio, SDK, Platform Tools, JDK 17, teléfono físico, cable de datos y Git.

## Gradle JDK

```text
File > Settings > Build, Execution, Deployment > Build Tools > Gradle
```

Usar:

```text
C:\Program Files\Android\Android Studio\jbr
```

En PowerShell:

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
.\gradlew.bat --version
```

## SDK Manager

Instalar Platform, Build-Tools, Platform-Tools, Command-line Tools, NDK Side by side y CMake.

## Teléfono

Activar opciones de desarrollador y depuración USB:

```powershell
adb devices
```

## Verificación

```powershell
.\gradlew.bat test
.\gradlew.bat lint
.\gradlew.bat assembleDebug
```
