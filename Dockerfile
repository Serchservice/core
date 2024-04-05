# Stage 1: Build the application
# docker buildx build --platform linux/amd64 -t evastorm/app-server:1.0 .
FROM maven:3.9.6-amazoncorretto-21 AS Build
WORKDIR /app
LABEL authors="iamevaristus"
COPY pom.xml .
COPY src ./src
RUN mvn clean install

## Stage 2: Run the application
## docker push evastorm/app-server:1.0
FROM amazoncorretto:21.0.2-alpine3.19
WORKDIR /app
LABEL authors="iamevaristus"
EXPOSE 8080
COPY --from=build /app/target/*.jar ./server-0.0.1.jar
#CMD ["java", "-Xms512m", "-Xmx1024m", "-jar", "server-0.0.1.jar"]
CMD ["java", "-jar", "server-0.0.1.jar"]