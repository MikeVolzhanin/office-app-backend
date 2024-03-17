FROM openjdk:17
VOLUME /tmp
EXPOSE 8080
COPY target/office-app-backend-0.0.1-SNAPSHOT.jar backend-app.jar
ENTRYPOINT ["java","-jar","/backend-app.jar"]
