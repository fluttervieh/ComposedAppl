FROM gradle:6.9.4-jdk11-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon --info -x test

FROM openjdk:11-jre-slim

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/collabnotes-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar","/app/collabnotes-0.0.1-SNAPSHOT.jar"]