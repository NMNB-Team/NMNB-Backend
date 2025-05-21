FROM openjdk:17-jdk-slim

WORKDIR /app

COPY nmnb-webflux-bootstrap/build/libs/*.jar /app/app.jar

ENV SERVER_PORT=8084

EXPOSE 8084 8085

# Run the application
CMD ["java", "-jar", "-Dserver.port=${SERVER_PORT}", "/app/app.jar"]
