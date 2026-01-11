# Dockerfile multi-stage para Spring Boot 4 con Java 25
FROM maven:3.9-eclipse-temurin-25-alpine AS build
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descargar dependencias (capa cacheada)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar aplicación
RUN mvn clean package -DskipTests -Dspotless.check.skip=true -B

# Etapa final: imagen ligera con JRE
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Crear usuario no privilegiado
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar JAR de la etapa de compilación
COPY --from=build /app/target/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Ejecutar aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
