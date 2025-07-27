# Use a lightweight OpenJDK image
FROM eclipse-temurin:23-jre-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file (adjust the path if needed)
COPY target/application-1.0.0-jar-with-dependencies.jar /app/app.jar

# Expose port 8080
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar", "-Xms20m", "-Xmx200m"]

#docker build -t joda-app .
#docker run -d -p 8080:8080 --name joda-container joda-app