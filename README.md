# BankApp

A Spring Boot-based banking web application.

## Overview

BankApp is a web application for managing bank accounts, built with Spring Boot, Thymeleaf, Spring Data JPA, and Spring Security. It uses MySQL as the database.

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- MySQL 8.0

## Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/Routparesh/Bank-App.git
   cd Bank-App
   ```

2. Configure the MySQL database:
   - Create a database named `bankapp` (or update the name in `src/main/resources/application.properties`).
   - Ensure MySQL is running on `localhost:3306`.
   - Update the username and password in `src/main/resources/application.properties` if needed.

   Example `application.properties`:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/bankapp?useSSL=false&serverTimezone=UTC
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

3. Build the project:

   ```bash
   ./mvnw clean package
   ```

4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```
   The application will be available at `http://localhost:8080`.

## Running Tests

To run the unit and integration tests:

```bash
./mvnw test
```

## Building a Runnable JAR

To build an executable JAR:

```bash
./mvnw clean package
```

The JAR will be placed in `target/bankapp-0.0.1-SNAPSHOT.jar`.

You can run it with:

```bash
java -jar target/bankapp-0.0.1-SNAPSHOT.jar
```

## Docker Support (Optional)

A Dockerfile is not yet included. To containerize the application, you can use the following base:

```dockerfile
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/bankapp-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

Build and run:

```bash
docker build -t bankapp .
docker run -p 8080:8080 bankapp
```

## Environment Variables

You can override the default configuration using environment variables:

- `SPRING_DATASOURCE_URL`: JDBC URL for the database.
- `SPRING_DATASOURCE_USERNAME`: Database username.
- `SPRING_DATASOURCE_PASSWORD`: Database password.
- `SERVER_PORT`: Port to run the application on (default: 8080).

Example:

```bash
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/bankapp SPRING_DATASOURCE_USERNAME=root SPRING_DATASOURCE_PASSWORD=secret java -jar target/bankapp-0.0.1-SNAPSHOT.jar
```

## CI/CD Notes

- The project uses Maven for building and testing.
- Ensure your CI pipeline runs `./mvnw verify` (or `test`) to validate the build.
- For deployment, package the JAR and deploy to your preferred environment (e.g., Kubernetes, VM, or PaaS).

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact

For questions or support, please open an issue on the GitHub repository
