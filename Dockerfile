FROM openliberty/open-liberty:kernel-java11-openj9-ubi

ENV POSTGRES_PASSWORD example
ENV POSTGRES_USER postgres

COPY --chown=1001:0  src/main/liberty/config/postgresql-42.2.16.jar /config/
COPY --chown=1001:0  target/fant.war /config/dropins/
COPY --chown=1001:0  server.xml /config

RUN configure.sh