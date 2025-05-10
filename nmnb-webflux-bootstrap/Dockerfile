FROM openjdk:17-jdk-slim

WORKDIR /app

COPY nmnb-webflux-bootstrap/build/libs/*.jar /app/app.jar

ENV SERVER_PORT=8083

EXPOSE 8083 8084

# Run the application
CMD ["java", "-jar", "-Dserver.port=${SERVER_PORT}", "/app/app.jar"]
