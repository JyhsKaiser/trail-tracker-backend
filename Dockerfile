# Etapa de compilación (Multi-stage build)
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# No ejecutamos como root por seguridad (Estándar ADEM/Azure)
RUN addgroup -S trailgroup && adduser -S trailuser -G trailgroup
USER trailuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]