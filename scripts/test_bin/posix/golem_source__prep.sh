#!/bin/sh

echo "Loading config"
_SCRIPT_DIR=$( dirname "${BASH_SOURCE[0]}" )
. "${_SCRIPT_DIR}/_load_config.sh"

echo "Activate evera-env"
. "${VENV_DIR}/bin/activate"

echo "Change to source directory"
cd "${EVERA_SRC_DIR}"
