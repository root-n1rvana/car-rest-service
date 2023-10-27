# Build stage
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn package -DskipTests

# Final stage
FROM openjdk
WORKDIR /app
COPY --from=builder /app/target/*.jar car_rest_kocherga.jar
ENTRYPOINT ["java", "-jar", "car_rest_kocherga.jar"]