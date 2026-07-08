# Stage 1: Build
FROM docker.io/maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM docker.io/eclipse-temurin:17-jre-alpine
WORKDIR /app

# Создаем директорию для логов
RUN mkdir -p /app/logs

COPY --from=build /app/target/notes-service-1.0.0.jar app.jar

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 8181

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
