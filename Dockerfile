FROM maven:3.8.4-openjdk AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
# The JAR is copied into the current WORKDIR (/app)
COPY --from=build /app/target/sistema-nutricao-0.0.1-SNAPSHOT.jar .

EXPOSE 8080
# The command should reference the jar in the current directory, not /app/subdirectory/
ENTRYPOINT ["java", "-jar", "sistema-nutricao-0.0.1-SNAPSHOT.jar"]