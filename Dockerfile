FROM openjdk:11.0.16 AS builder
COPY . .
RUN ["./gradlew", "build"]

FROM openjdk:11.0.16
COPY --from=builder /app/build/libs/app.jar .
CMD ["java", "-jar", "app.jar"]
