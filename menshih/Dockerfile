FROM maven:3.8-openjdk-17 as build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar player.jar
ENTRYPOINT ["java", "-jar", "player.jar"]