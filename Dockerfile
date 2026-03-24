# 🏗️ Build stage
FROM maven:3.8.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY backend/gamebackend/pom.xml .
COPY backend/gamebackend/src ./src
RUN mvn clean package -DskipTests

# 🚀 Run stage
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
