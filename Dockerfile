# Use Maven image to build the application
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /build

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Package the application
RUN mvn package -DskipTests


# Runtime Stage
FROM amazoncorretto:17

WORKDIR /app
COPY .env .
COPY --from=build /build/target/*.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]