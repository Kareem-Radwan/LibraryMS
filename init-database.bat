@echo off
echo Initializing InkVault Database...
"C:\Program Files\Java\jdk-26.0.1\bin\java.exe" -cp "bin;lib/sqlite-jdbc-3.46.0.0.jar" CreateDatabase
pause
