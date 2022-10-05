FROM openjdk:11.0.15 AS builder
COPY . .
RUN ["./gradlew", "build", "-x", "test"]

FROM openjdk:11.0.15
COPY --from=builder /app/build/libs/app.jar .
CMD ["java", "-jar", "app.jar"]
