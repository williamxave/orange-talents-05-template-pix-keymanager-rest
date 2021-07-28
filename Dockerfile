FROM openjdk:11
MAINTAINER William Bohn
ARG JAR_FILE=build/libs/*-all.jar
ADD ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT java -jar app.jar