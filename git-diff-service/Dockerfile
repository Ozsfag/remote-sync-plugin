# ====== build stage ======
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY . /app
RUN ./gradlew :git-diff-service:bootJar --no-daemon

# ====== runtime stage ======
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# нужен для healthcheck в docker-compose (curl внутри контейнера)
RUN apk add --no-cache curl

COPY --from=builder /app/git-diff-service/build/libs/git-diff-service-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
