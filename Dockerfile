FROM maven:3.6.3-openjdk-11 AS maven_build

COPY pom.xml /tmp/

COPY src /tmp/src/

VOLUME /tmp/

RUN mvn clean install

FROM openjdk

MAINTAINER mohamed_swilam@hotmail.com

EXPOSE 8080

COPY target/*.jar app.jar

COPY db db

CMD java -jar /app.jar