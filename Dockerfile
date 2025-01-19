# Stage 1: Build the application
FROM maven:3.9.6-amazoncorretto-21 AS build
WORKDIR /app
LABEL authors="iamevaristus"

# Copy pom.xml and download dependencies (improve build caching)
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copy source files and build the application
COPY src ./src
RUN mvn -B clean package

# Extract version using Maven property
ARG JAR_VERSION
RUN mvn help:evaluate -Dexpression=project.version -q -DforceStdout | tee version.txt
RUN JAR_VERSION=$(cat version.txt)

# Rename the jar file with the version extracted from pom.xml
RUN mv target/*.jar /app/services-${JAR_VERSION}.jar

# Stage 2: Run the application
FROM amazoncorretto:21.0.2-alpine3.19
WORKDIR /app
LABEL authors="iamevaristus"
EXPOSE 8080

# Copy the built jar from the build stage
COPY --from=build /app/core-${JAR_VERSION}.jar ./core.jar

# Command to run the application
CMD ["java", "-jar", "core.jar"]
