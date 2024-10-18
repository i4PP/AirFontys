# Use an official OpenJDK image as a parent image
FROM openjdk:21-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the local files to the container
COPY . .

# Grant execution permissions to the mvnw script
RUN chmod +x ./mvnw

# Package the application using the specified pom.xml in the "Flight API" directory
RUN ./mvnw clean package

# Expose the port the application runs on
EXPOSE 8080

# Run the application jar located in the "Flight API/target" directory
CMD ["java", "-jar", "Flight API/target/flight-api-0.0.1-SNAPSHOT.jar"]
