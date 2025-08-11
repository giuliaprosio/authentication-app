# Build stage
FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["sh", "-c", "java --version && java --enable-preview -jar /app.jar"]
#ENTRYPOINT ["java", "--enable-preview", "-jar", "/app.jar"]
