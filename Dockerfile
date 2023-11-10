FROM maven:3.8.5-openjdk-17 as build
COPY ./ /usr/src/app/
WORKDIR /usr/src/app/
RUN mvn clean install
FROM openjdk:17-jdk-slim
COPY --from=build /usr/src/app/report-service/report-container/target/report-container-*.jar /app/report-container-1.0-SNAPSHOT.jar
WORKDIR /app
EXPOSE 8181
CMD ["java", "-jar", "report-container-1.0-SNAPSHOT.jar"]
