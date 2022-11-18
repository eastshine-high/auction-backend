FROM openjdk:11.0.15
COPY ./app/build/libs/app.jar app.jar
CMD ["java", "-jar", "app.jar"]
