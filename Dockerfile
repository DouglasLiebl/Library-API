FROM maven:3.8.5-openjdk-18 AS build
WORKDIR /app
COPY . .
RUN mvn clean package


FROM openjdk:18
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT java -jar app.jar