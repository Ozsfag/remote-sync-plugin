FROM eclipse-temurin:21-jdk as builder
WORKDIR /app

COPY . /app
RUN ./gradlew :git-diff-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/git-diff-service/build/libs/git-diff-service-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]