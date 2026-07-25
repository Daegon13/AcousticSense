# Flujo Git

`main` contiene cambios revisados y compilables.

## Ramas

```text
feat/device-diagnostics
feat/synthetic-dsp
feat/full-duplex-audio
fix/audio-stream-shutdown
docs/update-wall-protocol
```

## Inicio

```powershell
git switch main
git pull
git switch -c feat/device-diagnostics
```

## Antes del commit

```powershell
.\gradlew.bat test
.\gradlew.bat lint
.\gradlew.bat assembleDebug
git status
git diff
```

## Commits

```text
feat: add device audio diagnostics
test: add synthetic echo fixtures
docs: define wall validation gate
fix: close input stream on lifecycle stop
```

Un PR, un objetivo. Preferir squash merge.
