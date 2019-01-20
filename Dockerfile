FROM openjdk:8-jdk-alpine
ADD target/JobFinder-backend.jar JobFinder-backend.jar
VOLUME /var/opt/JobFinder-backend
EXPOSE 8433
EXPOSE 8080
CMD ["java", "-jar", "JobFinder-backend.jar"]
