# Build stage — compiles the jar. Render rebuilds this on every deploy, so
# it always builds fresh against Maven Central (unlike this sandbox, Render's
# build environment has normal internet access).
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Downloads dependencies into a layer that's cached separately from source
# changes, so editing a .java file doesn't force a full dependency re-fetch.
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B package -DskipTests

# Run stage — a slim JRE image, not the full JDK/Maven toolchain.
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render sets $PORT at runtime; application.yml already reads it via ${PORT:8080}.
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
