
set PROJECT_DIR=%systemdrive%%homepath%\projects

echo "Activate evera-env"
call "%PROJECT_DIR%\evera-env\Scripts\activate.bat"

echo "Change to source directory"
cd "%PROJECT_DIR%\evera"
