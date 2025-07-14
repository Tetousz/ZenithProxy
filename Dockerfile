# Use official OpenJDK image
FROM eclipse-temurin:17

# Create app directory
WORKDIR /app

# Copy ZenithProxy files into the image
COPY . /app

# If your main JAR file is named ZenithProxy.jar:
CMD ["java", "-jar", "ZenithProxy.jar"]
