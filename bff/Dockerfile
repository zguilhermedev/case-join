FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY mvnw ./
COPY .mvn/ ./.mvn/
RUN chmod +x mvnw
COPY pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src/ ./src/
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]