FROM eclipse-temurin:21-jdk as builder
WORKDIR /app

# Копируем весь проект (или api-gateway + build-скрипты)
COPY . /app

# Собираем только модуль api-gateway
RUN ./gradlew :api-gateway:bootJar --no-daemon

# Финальный образ на jre
FROM eclipse-temurin:21-jre
WORKDIR /app

# Копируем собранный jar-файл
COPY --from=builder /app/api-gateway/build/libs/api-gateway-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
