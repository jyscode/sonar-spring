# Gradle + JDK 환경에서 먼저 빌드
FROM gradle:8.5-jdk17 AS builder
COPY . /app
WORKDIR /app
RUN gradle bootJar

# 슬림한 Java 런타임으로 실행
FROM openjdk:17-jdk-slim
COPY --from=builder /app/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
