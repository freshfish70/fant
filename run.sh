#!/bin/sh
docker container rm -f fant 
docker network create fant_backend
docker run --network=fant_backend -p 9080:9080 -p 9443:9443 --name fant -v $(pwd)/target:/config/dropins no.traeen/fant