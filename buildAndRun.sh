#!/bin/sh
if [ $(docker ps -a -f name=fant | grep -w fant | wc -l) -eq 1 ]; then
  docker rm -f fant
fi
mvn clean package && docker build -t no.traeen/fant .
docker run --network=fant_backend -p 9080:9080 -p 9443:9443 --name fant -d no.traeen/fant
