# Use Eclipse Temurin Java 17 JDK base image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Expose the default Spring Boot port
EXPOSE 8080

# Add a volume for temporary files (used by Spring Boot)
VOLUME /tmp

# Copy the JAR file from the root folder (not target) to the container
COPY medPlus.jar app.jar

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
