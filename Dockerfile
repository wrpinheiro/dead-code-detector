FROM openjdk:8u121-jdk-alpine

# Configure  env
COPY build/libs/deadcodedetection-0.0.1-SNAPSHOT.jar /app/deadcodedetection.jar

EXPOSE 80 8080
ENTRYPOINT [ "sh", "-c", "java -jar /app/deadcodedetection.jar" ]
