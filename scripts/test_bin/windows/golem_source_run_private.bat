
call ".\evera_source__prep.bat"

set ID=99

echo "Running evera on network '%ID%'"
python everaapp.py --protocol_id %ID%

pause
