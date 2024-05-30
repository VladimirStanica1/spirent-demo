FROM alpine/java:22

WORKDIR /app

COPY target/bird-app-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]