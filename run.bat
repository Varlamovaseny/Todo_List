@echo off
echo Компиляция Java файлов...
javac *.java
if %errorlevel% == 0 (
    echo Запуск приложения...
    java TodoApp
) else (
    echo Ошибка компиляции!
)
pause