# Use an official OpenJDK image as a parent image
FROM openjdk:21-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the local files to the container
COPY . .

# Grant execution permissions to the mvnw script
RUN chmod +x ./mvnw

# Package the application
RUN ./mvnw clean package

# Expose the port the application runs on
EXPOSE 8081

# Run the application
CMD ["java", "-jar", "target/authentication-0.0.1-SNAPSHOT.jar"]
