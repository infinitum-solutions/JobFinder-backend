FROM openjdk:10-jdk-alpine
ADD target/JobFinder-backend.jar JobFinder-backend.jar
VOLUME /var/opt/jobfinder-backend
VOLUME /var/log/jobfinder-backend
EXPOSE 8433
EXPOSE 8080
CMD ["java", "-jar", "JobFinder-backend.jar"]
