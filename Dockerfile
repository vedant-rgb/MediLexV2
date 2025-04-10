# Use Eclipse Temurin Java 17 JDK base image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Expose the default Spring Boot port (optional but good practice)
EXPOSE 8080

# Add a volume for temp files
VOLUME /tmp

# Copy the JAR file from the target folder to the container
COPY target/medPlus-0.0.1-SNAPSHOT.jar app.jar

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
