@echo off
setlocal
set DIR=%~dp0
java --module-path "%DIR%libs" --add-modules javafx.controls,javafx.fxml -jar "%DIR%retroroom-1.0-win.jar"
endlocal