FROM gradle:8.0.2-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test --stacktrace

FROM openjdk AS deploy
WORKDIR /app
COPY --from=build /app/build/libs/team-building.jar .
CMD java -jar team-building.jar
