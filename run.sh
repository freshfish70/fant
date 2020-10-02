#!/bin/sh
docker container rm -f fant 
docker run --network=fant_backend -p 9080:9080 -p 9443:9443 --user 1000 --name fant -v $(pwd)/target:/config/dropins -v $(pwd)/server:/opt/ol/wlp/output/defaultServer/ no.traeen/fant 