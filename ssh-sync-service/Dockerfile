FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY . /app
RUN chmod +x ./gradlew
RUN ./gradlew :ssh-sync-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl
COPY --from=builder /app/ssh-sync-service/build/libs/ssh-sync-service-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
