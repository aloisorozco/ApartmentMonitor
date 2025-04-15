# Stage 1: Build the Java app with Maven
FROM maven:3.9.5-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

# Build the application and skip tests to speed things up
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21

WORKDIR /app

# Copy the JAR file built in stage 1
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]