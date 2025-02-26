# Etapa 1: Construcción
FROM maven:3.9.4-eclipse-temurin-17 AS build

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo pom.xml y descarga las dependencias
COPY pom.xml ./
COPY .mvn ./.mvn
COPY mvnw ./
RUN ./mvnw dependency:go-offline

# Copia el código fuente y construye el JAR
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Etapa 2: Ejecución
FROM openjdk:17-jdk-slim

# Establece el directorio de trabajo en el contenedor
WORKDIR /app

# Copia el archivo JAR desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto que usa la aplicación
EXPOSE 8004

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar","app.jar"]