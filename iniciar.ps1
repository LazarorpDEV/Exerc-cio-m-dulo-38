$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
mvn -Pservidor clean verify
if ($LASTEXITCODE -ne 0) { throw 'O Maven falhou. Confira o erro acima.' }
java -jar target/servidor/payara-micro.jar --deploy target/modulo38.war --port 8080 --noCluster --disablephonehome
