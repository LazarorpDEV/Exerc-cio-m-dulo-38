#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
mvn -Pservidor clean verify
java -jar target/servidor/payara-micro.jar --deploy target/modulo38.war --port 8080 --noCluster --disablephonehome
