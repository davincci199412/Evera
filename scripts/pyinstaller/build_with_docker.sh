#!/bin/sh

docker build -t evera/pack -f docker/Dockerfile.pack docker
docker run -it -v /tmp:/tmp evera/pack
