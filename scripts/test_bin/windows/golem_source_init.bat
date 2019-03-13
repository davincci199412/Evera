
set PROJECT_DIR=%systemdrive%%homepath%\projects

echo "Ensure projects directory exists"
if not exist "%PROJECT_DIR%" md "%PROJECT_DIR%"

echo "Setup venv in ~/projects/evera-env"
python -m venv "%PROJECT_DIR%\evera-env"

echo "Clone into ~/projects/evera"
git clone https://github.com/everafactory/evera "%PROJECT_DIR%\evera"

set CUR_DIR=%CD%

echo "Change directory to ~/projects/evera"
cd "%PROJECT_DIR%\evera"

echo "Build taskcollector"
msbuild apps\rendering\resources\taskcollector\taskcollector.sln /p:Configuration=Release /p:Platform=x64

echo "Run update from previous directory"
cd %CUR_DIR%
call ".\evera_source_update.bat"
