FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /projeto
COPY pom.xml .
COPY src ./src
RUN mvn -B -Pservidor clean verify

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /projeto/target/servidor/payara-micro.jar ./payara-micro.jar
COPY --from=build /projeto/target/modulo38.war ./modulo38.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "payara-micro.jar", "--deploy", "modulo38.war", "--port", "8080", "--noCluster", "--disablephonehome"]
