# JDK
FROM eclipse-temurin:21-jdk-alpine

# Set directory
WORKDIR /app

# COPY file
COPY target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run
ENTRYPOINT ["java", "-jar", "app.jar"]