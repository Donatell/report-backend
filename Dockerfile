FROM amazoncorretto:11.0.11
COPY ./target/report-backend-0.2.jar /usr/app/
WORKDIR /usr/app/
ENTRYPOINT ["java", "-jar", "report-backend-0.2.jar"]
EXPOSE 8443