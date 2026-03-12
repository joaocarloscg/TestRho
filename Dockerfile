# Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY mvnw .
COPY .mvn/ .mvn/
COPY pom.xml .

RUN chmod +x mvnw
# Download and cache dependencies
RUN ./mvnw -B -q dependency:go-offline

COPY src/ src/
# Build
RUN ./mvnw -B clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/exchange-rate-api.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]