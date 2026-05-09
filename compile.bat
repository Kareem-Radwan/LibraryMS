@echo off
echo Compiling InkVault Library Management System...

"C:\Program Files\Java\jdk-26.0.1\bin\javac.exe" -d bin src/User.java src/Book.java src/Transaction.java
"C:\Program Files\Java\jdk-26.0.1\bin\javac.exe" -cp bin -d bin src/Admin.java src/Librarian.java src/Member.java
"C:\Program Files\Java\jdk-26.0.1\bin\javac.exe" -cp "bin;lib/jbcrypt-0.4.jar" -d bin src/PasswordUtil.java
"C:\Program Files\Java\jdk-26.0.1\bin\javac.exe" -cp "bin;lib/sqlite-jdbc-3.46.0.0.jar;lib/jbcrypt-0.4.jar" -d bin src/DatabaseHelper.java src/LibraryService.java
"C:\Program Files\Java\jdk-26.0.1\bin\javac.exe" -cp "bin;lib/flatlaf.jar;lib/sqlite-jdbc-3.46.0.0.jar;lib/slf4j-api-2.0.13.jar;lib/jbcrypt-0.4.jar" -d bin src/LoginFrame.java src/InkVaultApp.java src/CreateDatabase.java
"C:\Program Files\Java\jdk-26.0.1\bin\javac.exe" -cp "bin;lib/sqlite-jdbc-3.46.0.0.jar;lib/jbcrypt-0.4.jar" -d bin src/MigratePasswords.java

echo.
echo Compilation complete!
echo.
