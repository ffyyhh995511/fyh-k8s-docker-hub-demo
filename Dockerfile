FROM eclipse-temurin:8-jre

WORKDIR /app

COPY target/fyh-k8s-docker-hub-demo-1.0.0.jar app.jar

EXPOSE 9981

ENTRYPOINT ["java", "-jar", "app.jar"]
