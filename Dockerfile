FROM eclipse-temurin:11-alpine
RUN mkdir /opt/disa-notification-service
COPY target/notification.service-0.0.1-SNAPSHOT.jar /opt/disa-notification-service/
CMD ["java", "-jar", "/opt/disa-notification-service/notification.service-0.0.1-SNAPSHOT.jar"]
