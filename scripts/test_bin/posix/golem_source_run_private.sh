#!/bin/sh

. ./evera_source__prep.sh

ID=99

echo "Running evera on network ${ID}"
python everaapp.py --protocol_id ${ID}

