#!/bin/sh
if [ $(docker ps -a -f name=fant | grep -w fant | wc -l) -eq 1 ]; then
  docker rm -f fant
fi
mvn clean package && docker build -t no.traeen/fant .