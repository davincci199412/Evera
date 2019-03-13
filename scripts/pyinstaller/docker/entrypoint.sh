#!/bin/bash

set -e

cd evera
git stash && git pull --rebase

pip install -r requirements.txt
python setup.py pyinstaller
cp -r dist/* /tmp

echo "-------------------------------------"
echo "Package location: /tmp/evera[app,cli]"
echo "-------------------------------------"
