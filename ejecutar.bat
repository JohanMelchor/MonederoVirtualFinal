@echo off
cd /d "%~dp0"
set FX=%USERPROFILE%\.m2\repository\org\openjfx
set MP=%FX%\javafx-base\13\javafx-base-13-win.jar;%FX%\javafx-controls\13\javafx-controls-13-win.jar;%FX%\javafx-graphics\13\javafx-graphics-13-win.jar;%FX%\javafx-fxml\13\javafx-fxml-13-win.jar
if not exist MonederoVirtual.jar call compilar.bat
java --module-path "%MP%" --add-modules javafx.controls,javafx.fxml -jar MonederoVirtual.jar
