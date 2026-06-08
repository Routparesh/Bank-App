# ==========================================
# Stage 1: Build and package the application
# ==========================================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy only the pom.xml to download dependencies (improves caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the fat/executable JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# Stage 2: Create the minimal runtime image
# ==========================================
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

# Create a non-root user for security purposes
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy the built JAR file from the 'build' stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port (change if your app uses a different port)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]