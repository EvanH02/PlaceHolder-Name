# Run built jar if it exists, otherwise run compiled classes
$jarPath = "dist\PlaceHolderName.jar"
if (Test-Path $jarPath) {
    java -jar $jarPath
} elseif (Test-Path "build\classes") {
    java -cp "build\classes" org.example.frontend.PlaceHolderName
} else {
    Write-Error "No build found. Run .\build.ps1 to build first."; exit 1
}
