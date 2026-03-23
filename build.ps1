# Build using Ant
if (!(Get-Command ant -ErrorAction SilentlyContinue)) {
    Write-Error "Ant is not installed or not on PATH. Install Ant or run via your IDE."; exit 1
}
ant -f build.xml dist
if ($LASTEXITCODE -ne 0) { Write-Error "Ant build failed"; exit $LASTEXITCODE }
Write-Host "Build complete: dist\PlaceHolderName.jar"
