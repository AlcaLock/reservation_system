FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw

COPY src src
RUN ./mvnw package -DskipTests -q

FROM eclipse-temurin:21-jre

WORKDIR /app

RUN useradd --system --create-home spring
COPY --from=build /workspace/target/*.jar app.jar
RUN chown spring:spring app.jar

USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]