@echo off
echo ========================================
echo InkVault Password Migration Tool
echo ========================================
echo.
echo This script will migrate all plain text passwords
echo in the database to secure BCrypt hashed passwords.
echo.
echo WARNING: Make sure you have a backup of inkvault.db
echo before proceeding!
echo.
pause

echo.
echo Running password migration...
echo.

"C:\Program Files\Java\jdk-26.0.1\bin\java.exe" -cp "bin;lib/sqlite-jdbc-3.46.0.0.jar;lib/jbcrypt-0.4.jar" MigratePasswords

echo.
echo ========================================
echo Migration process completed!
echo ========================================
echo.
pause
