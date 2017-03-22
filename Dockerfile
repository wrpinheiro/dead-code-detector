FROM openjdk:8u121-jdk-alpine

# Configure  env
COPY build/libs/dead-code-detection-service-0.0.1-SNAPSHOT.jar /app/dead-code-detection-service.jar

EXPOSE 80 8080
ENTRYPOINT [ "sh", "-c", "java -jar /app/dead-code-detection-service.jar" ]
