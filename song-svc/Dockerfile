FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ ./src/
RUN mvn clean package -DskipTests

FROM openjdk:17-alpine AS package
WORKDIR /app
RUN apk update && apk add --no-cache curl
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8072
CMD ["java", "-jar", "app.jar"]
