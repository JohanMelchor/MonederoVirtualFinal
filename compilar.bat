@echo off
REM Compila el proyecto con JavaFX 13 (tomado del repositorio local de Maven)
cd /d "%~dp0"
set FX=%USERPROFILE%\.m2\repository\org\openjfx
set MP=%FX%\javafx-base\13\javafx-base-13-win.jar;%FX%\javafx-controls\13\javafx-controls-13-win.jar;%FX%\javafx-graphics\13\javafx-graphics-13-win.jar;%FX%\javafx-fxml\13\javafx-fxml-13-win.jar
if not exist "%FX%\javafx-controls\13\javafx-controls-13-win.jar" (
    echo No se encontro JavaFX 13 en %FX%
    echo Ejecute primero: mvn javafx:run   ^(Maven lo descarga^)
    pause
    exit /b 1
)
if exist out rmdir /s /q out
mkdir out
dir /s /b src\main\java\*.java > fuentes.txt
javac --module-path "%MP%" --add-modules javafx.controls,javafx.fxml -encoding UTF-8 -d out @fuentes.txt
if errorlevel 1 (
    del fuentes.txt
    echo Error al compilar
    pause
    exit /b 1
)
del fuentes.txt
xcopy /s /y /q src\main\resources out\ > nul
echo Main-Class: co.edu.uniquindio.poo.Main> manifest.txt
jar cfm MonederoVirtual.jar manifest.txt -C out .
del manifest.txt
rmdir /s /q out
echo Listo: se genero MonederoVirtual.jar
