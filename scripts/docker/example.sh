#!/bin/bash

set -e

if [ "$#" -lt 2 ]; then
    echo "$0 [image_name] [source_path]"
    exit 1
fi

NODE_COUNT=3
DOCKER_BRIDGE="docker0"
DOCKER_ADDR=$(ifconfig ${DOCKER_BRIDGE} | grep "inet addr" | cut -d':' -f2 | cut -d' ' -f1 | sed "s/\n|\\n//g")

CMD="python docker_helper.py $1"
EVERA_DIR=$2

echo "   [i] Killing + starting containers"
${CMD} kill %{container}
${CMD} rm %{container}

for i in $(seq 1 $NODE_COUNT); do
    docker run -d $1 --entrypoint=/bin/bash
done

echo "   [i] Replacing evera"
${CMD} exec %{container} sh -c "rm -rf /opt/evera"
${CMD} cp ${EVERA_DIR} %{container}:/opt/evera

echo "   [i] Clearing resources"
${CMD} exec %{container} sh -c "rm -rf /opt/evera/gnr/benchmarks"

# echo "   [i] Setting up evera"
# ${CMD} exec %{container} sh -c "cd /opt/evera && python setup.py clean > /tmp/evera-setup.log"
# ${CMD} exec %{container} sh -c "cd /opt/evera && python setup.py develop >> /tmp/evera-setup.log"

echo "   [i] Executing GNR node"
${CMD} exec %{container} sh -c "rm -rf /root/.local/evera/keys"
${CMD} exec %{container} sh -c "echo 'cd /opt/evera && nohup python gui/node.py -a %{ip} -p ${DOCKER_ADDR}:40102 >/tmp/gnr-node.log 2>&1 &' > /root/gnr-node.sh"
${CMD} exec -d %{container} sh /root/gnr-node.sh
