@echo off
if exist dist\PlaceHolderName.jar (
  java -jar dist\PlaceHolderName.jar
) else if exist build\classes (
  java -cp build\classes org.example.frontend.PlaceHolderName
) else (
  echo No build found. Run build.ps1 to build first.
)
