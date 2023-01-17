FROM gradle:latest as build

WORKDIR /app/src/

COPY . .

RUN gradle clean shadowJar

FROM adoptopenjdk/openjdk11:latest

WORKDIR /app/

COPY --from=build /app/src/build/libs/mc-maintenance-api-1.0-SNAPSHOT-all.jar .

CMD ["java", "-jar", "mc-maintenance-api-1.0-SNAPSHOT-all.jar"]