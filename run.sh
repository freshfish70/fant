#!/bin/sh
docker run --network=fant_backend -d -p 9080:9080 -p 9443:9443 --name fant -v $(pwd)/target:/config/dropins no.traeen/fant