
call ".\evera_source__prep.bat"

echo "Running evera with DEBUG logs"
python everaapp.py --loglevel DEBUG

pause
