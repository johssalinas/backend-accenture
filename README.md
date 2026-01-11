# Franchise API - Backend Accenture

API REST para la gestión de franquicias, sucursales y productos, desarrollada con Spring Boot 4 y Java 25, desplegada en AWS con CI/CD automatizado.

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Java Version](https://img.shields.io/badge/Java-25-orange)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen)]()
[![AWS](https://img.shields.io/badge/AWS-Deployed-orange)]()
[![License](https://img.shields.io/badge/license-Private-red)]()

## 🌐 Demo en Vivo

- **API Base**: http://franchise-api-alb-2042942561.us-east-2.elb.amazonaws.com/api/v1
- **Documentación Swagger**: http://franchise-api-alb-2042942561.us-east-2.elb.amazonaws.com/swagger-ui/index.html

> 💡 **Recomendación**: Revisa la documentación en Swagger para ver ejemplos detallados de cómo probar las APIs y sus posibles respuestas.

## 📋 Tabla de Contenidos

- [Características](#-características)
- [Requerimientos de la Prueba Técnica](#-requerimientos-de-la-prueba-técnica)
- [Inicio Rápido](#-inicio-rápido)
- [Documentación de la API](#-documentación-de-la-api)
- [Tests](#-tests)
- [CI/CD Pipeline](#-cicd-pipeline)

## 🚀 Características

- ✅ **API RESTful** completa con Spring Boot 4.0.1
- ✅ **Arquitectura Hexagonal** (Clean Architecture / Ports & Adapters)
- ✅ **Programación Funcional** con Java 25 (Streams, Optional, Records)
- ✅ **Base de datos PostgreSQL 16** con migraciones Flyway
- ✅ **Cache distribuido con Redis 7** para optimización de rendimiento
- ✅ **Documentación interactiva** con Swagger/OpenAPI
- ✅ **Contenedores Docker** con multi-stage builds
- ✅ **Infraestructura como Código** con Terraform
- ✅ **Despliegue completo en AWS** con ECS Fargate, RDS, ElastiCache
- ✅ **CI/CD automatizado** con GitHub Actions
- ✅ **Tests unitarios y de integración** con JUnit 5 y Mockito
- ✅ **Calidad de código** con Spotless, Checkstyle y OpenRewrite

## ✅ Requerimientos de la Prueba Técnica

Todos los criterios de aceptación y puntos extra fueron implementados:

### Criterios de Aceptación
- ✅ **Desarrollado en Spring Boot** (Spring Boot 4.0.1)
- ✅ **Agregar nueva franquicia** - `POST /api/v1/franchises`
- ✅ **Agregar nueva sucursal** - `POST /api/v1/franchises/{id}/branches`
- ✅ **Agregar nuevo producto** - `POST /api/v1/branches/{id}/products`
- ✅ **Eliminar producto** - `DELETE /api/v1/products/{id}`
- ✅ **Modificar stock de producto** - `PUT /api/v1/products/{id}/stock`
- ✅ **Productos con más stock por sucursal** - `GET /api/v1/franchises/{id}/top-products`
- ✅ **Persistencia en la nube** - PostgreSQL RDS y Redis ElastiCache en AWS

### Puntos Extra
- ✅ **Aplicación empaquetada con Docker** - Dockerfile multi-stage optimizado
- ✅ **Programación funcional** - Uso extensivo de Java 25 features funcionales
- ✅ **Actualizar nombre de franquicia** - `PUT /api/v1/franchises/{id}/name`
- ✅ **Actualizar nombre de sucursal** - `PUT /api/v1/branches/{id}/name`
- ✅ **Actualizar nombre de producto** - `PUT /api/v1/products/{id}/name`
- ✅ **Infraestructura como código** - Terraform para aprovisionar AWS
- ✅ **Solución desplegada en la nube** - AWS con VPC, ECS, RDS, ElastiCache, ALB

## 🏗️ Arquitectura

### Clean Architecture (Arquitectura Hexagonal)

```
┌─────────────────────────────────────────────────────────────┐
│                    APPLICATION LAYER                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │  Use Cases   │  │     DTOs     │  │   Services   │     │
│  │  (Business)  │  │ (Data Trans) │  │ (Orchestrat) │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                             ▲
                             │ Ports
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │   Entities   │  │  Functional  │  │  Repository  │     │
│  │   (Models)   │  │    Utils     │  │    Ports     │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                             ▲
                             │ Adapters
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                  INFRASTRUCTURE LAYER                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ Controllers  │  │     JPA      │  │    Redis     │     │
│  │  (REST API)  │  │   Adapters   │  │    Cache     │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

### Arquitectura AWS Desplegada

```
                    Internet
                       │
                       ▼
                ┌──────────────┐
                │   Route 53   │ (Opcional)
                └──────┬───────┘
                       │
                       ▼
            ┌─────────────────────┐
            │  Application Load   │
            │     Balancer        │
            └─────────┬───────────┘
                      │
         ┌────────────┴───────────┐
         │                        │
         ▼                        ▼
    ┌─────────┐            ┌─────────┐
    │   ECS   │            │   ECS   │
    │ Fargate │            │ Fargate │
    │  Task   │            │  Task   │
    └────┬────┘            └────┬────┘
         │                      │
         └──────────┬───────────┘
                    │
         ┌──────────┴──────────┐
         │                     │
         ▼                     ▼
    ┌─────────┐          ┌─────────┐
    │   RDS   │          │ ElastiC │
    │PostgreSQL│         │  Redis  │
    └─────────┘          └─────────┘
```

## 🛠️ Tecnologías y Librerías

### Framework y Core
- **Spring Boot 4.0.1** - Framework principal
- **Java 25** - Última versión LTS con features funcionales
- **Maven 3.9** - Gestión de dependencias y build

### Persistencia y Base de Datos
- **Spring Data JPA** - ORM y abstracción de datos
- **PostgreSQL 16** - Base de datos relacional
- **Flyway 11.2.0** - Migraciones de base de datos versionadas
- **HikariCP** - Connection pool de alto rendimiento

### Cache y Performance
- **Spring Cache** - Abstracción de cache
- **Redis 7** - Cache distribuido en memoria
- **Jedis** - Cliente Redis para Java

### Documentación
- **SpringDoc OpenAPI 3.0.1** - Generación automática de documentación
- **Swagger UI** - Interface interactiva para probar APIs

### Mapeo y Transformación
- **MapStruct 1.6.3** - Mapeo de objetos type-safe
- **Lombok** - Reducción de código boilerplate

### Calidad de Código
- **Spotless 3.1.0** - Formateo automático de código con Google Java Format
  - Asegura consistencia en el estilo de código
  - Integrado en el proceso de compilación
  
- **Checkstyle 3.6.0** - Análisis estático de código
  - Validación de estándares de codificación (Google Checks)
  - Detección de problemas de estilo y potenciales bugs
  
- **OpenRewrite 6.27.0** - Refactorización automática
  - Análisis estático avanzado
  - Limpieza de código automatizada
  - Eliminación de código muerto y optimizaciones

### Testing
- **JUnit 5** - Framework de testing principal
- **Mockito** - Mocking framework
- **Spring Boot Test** - Testing de integración
- **ArchUnit 1.4.1** - Tests de arquitectura (validación de capas)
- **H2 Database** - Base de datos en memoria para tests

### DevOps y Deployment
- **Docker** - Contenedorización
- **Docker Compose** - Orquestación local
- **Terraform** - Infrastructure as Code
- **GitHub Actions** - CI/CD pipeline

### Monitoreo
- **Spring Boot Actuator** - Endpoints de salud y métricas
- **CloudWatch** - Logs y métricas en AWS

### ¿Por qué Programación Funcional y no Reactiva?

**Decisión Técnica**: Se optó por **programación funcional** en lugar de programación reactiva por las siguientes razones:

1. **Volumen de Concurrencia Esperado**: El proyecto está diseñado para una carga moderada de operaciones CRUD. La programación reactiva (WebFlux) está optimizada para miles de conexiones concurrentes, lo cual sería over-engineering para este caso de uso.

2. **Simplicidad y Mantenibilidad**: La programación funcional con Java 25 ofrece:
   - Código más legible y fácil de mantener
   - Menor curva de aprendizaje para el equipo
   - Menos complejidad en el debugging
   - Stack trace más claros

3. **Performance Adecuado**: Con Spring MVC + Cache (Redis) + Connection Pooling, el sistema maneja eficientemente:
   - Operaciones I/O bloqueantes de forma óptima
   - Cache distribuido para reducir latencia
   - Pool de conexiones optimizado con HikariCP

4. **Features Funcionales de Java 25**:
   - Streams API para procesamiento de colecciones
   - Optional para manejo seguro de nulos
   - Records para inmutabilidad
   - Pattern Matching
   - Lambdas y method references

## 🚀 Inicio Rápido

### Requisitos Previos

#### Para desarrollo local:
- Java 25 o superior
- Maven 3.9+
- Docker y Docker Compose
- Git

#### Para despliegue en AWS:
- AWS CLI 2.x configurado
- Terraform 1.0+
- Cuenta de AWS activa

### Opción 1: Docker Compose (Recomendado para desarrollo)

#### Linux / macOS

```bash
# 1. Clonar el repositorio
git clone https://github.com/johssalinas/backend-accenture.git
cd backend-accenture

# 2. Copiar variables de entorno
cp .env.example .env

# 3. (Opcional) Editar .env con tus configuraciones
nano .env

# 4. Levantar todos los servicios
docker-compose up -d

# 5. Ver logs en tiempo real
docker-compose logs -f app

# 6. Verificar que todo esté corriendo
docker-compose ps

# La API estará disponible en http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui/index.html
```

#### Windows (PowerShell)

```powershell
# 1. Clonar el repositorio
git clone https://github.com/johssalinas/backend-accenture.git
cd backend-accenture

# 2. Copiar variables de entorno
Copy-Item .env.example .env

# 3. (Opcional) Editar .env con tus configuraciones
notepad .env

# 4. Levantar todos los servicios
docker-compose up -d

# 5. Ver logs en tiempo real
docker-compose logs -f app

# 6. Verificar que todo esté corriendo
docker-compose ps

# La API estará disponible en http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui/index.html
```

#### Comandos útiles de Docker Compose

```bash
# Detener servicios
docker-compose stop

# Detener y eliminar contenedores
docker-compose down

# Reiniciar un servicio específico
docker-compose restart app

# Ver logs de un servicio específico
docker-compose logs postgres
docker-compose logs redis

# Reconstruir la aplicación
docker-compose build app
docker-compose up -d app
```

### Opción 2: Ejecución Local (sin Docker para la app)

#### Linux / macOS

```bash
# 1. Levantar solo PostgreSQL y Redis con Docker
docker-compose up -d postgres redis

# 2. Compilar y ejecutar la aplicación
./mvnw clean spring-boot:run

# O compilar primero y luego ejecutar el JAR
./mvnw clean package -DskipTests
java -jar target/franchise-api-0.0.1-SNAPSHOT.jar
```

#### Windows (PowerShell)

```powershell
# 1. Levantar solo PostgreSQL y Redis con Docker
docker-compose up -d postgres redis

# 2. Compilar y ejecutar la aplicación
.\mvnw.cmd clean spring-boot:run

# O compilar primero y luego ejecutar el JAR
.\mvnw.cmd clean package -DskipTests
java -jar target\franchise-api-0.0.1-SNAPSHOT.jar
```

### Verificación de la Instalación

Una vez que la aplicación esté corriendo, verifica que todo funcione:

```bash
# Health check
curl http://localhost:8080/actuator/health

# Accede a Swagger UI en tu navegador
# http://localhost:8080/swagger-ui/index.html
```

## 📚 Documentación de la API

### Local
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Producción (AWS)
- **Swagger UI**: http://franchise-api-alb-2042942561.us-east-2.elb.amazonaws.com/swagger-ui/index.html
- **OpenAPI JSON**: http://franchise-api-alb-2042942561.us-east-2.elb.amazonaws.com/v3/api-docs

> 📖 **Importante**: La documentación en Swagger incluye:
> - Descripción detallada de cada endpoint
> - Ejemplos de request bodies
> - Posibles respuestas (200, 201, 400, 404, 500)
> - Esquemas de datos
> - Funcionalidad "Try it out" para probar directamente

## 🧪 Tests

El proyecto cuenta con una suite completa de tests:

### Tests Unitarios
- Tests de capa de dominio (entidades y lógica de negocio)
- Tests de use cases (aplicación)
- Tests de mappers y transformaciones

### Tests de Integración
- Tests de controladores REST con `@WebMvcTest`
- Tests de repositorios con `@DataJpaTest`
- Tests de configuración de cache

### Tests de Arquitectura
- Validación de dependencias entre capas con ArchUnit
- Verificación de convenciones de nombres
- Validación de estructura hexagonal

### Ejecutar Tests

#### Linux / macOS
```bash
# Ejecutar todos los tests
./mvnw test

# Ejecutar tests específicos
./mvnw test -Dtest=FranchiseControllerTest
./mvnw test -Dtest=ProductUseCaseTest

# Ejecutar solo tests de arquitectura
./mvnw test -Dtest=CleanArchitectureTest
```

#### Windows
```powershell
# Ejecutar todos los tests
.\mvnw.cmd test

# Ejecutar tests específicos
.\mvnw.cmd test -Dtest=FranchiseControllerTest
.\mvnw.cmd test -Dtest=ProductUseCaseTest

# Ejecutar solo tests de arquitectura
.\mvnw.cmd test -Dtest=CleanArchitectureTest
```

### Estadísticas de Testing
- ✅ Tests de capa de presentación (Controllers)
- ✅ Tests de capa de aplicación (Use Cases)
- ✅ Tests de capa de dominio (Entities)
- ✅ Tests de arquitectura (ArchUnit)
- ✅ Tests de integración con base de datos

## 📦 Construir para Producción

### Compilar aplicación

#### Linux / macOS
```bash
# Compilar y empaquetar (con tests)
./mvnw clean package -Dspotless.check.skip=true

# Compilar sin tests (más rápido)
./mvnw clean package -DskipTests -Dspotless.check.skip=true

# El JAR estará en: target/franchise-api-0.0.1-SNAPSHOT.jar
```

#### Windows
```powershell
# Compilar y empaquetar (con tests)
.\mvnw.cmd clean package -Dspotless.check.skip=true

# Compilar sin tests (más rápido)
.\mvnw.cmd clean package -DskipTests -Dspotless.check.skip=true

# El JAR estará en: target\franchise-api-0.0.1-SNAPSHOT.jar
```

### Construir imagen Docker

```bash
# Construcción local
docker build -t franchise-api:latest .
```

### Ejecutar JAR directamente

```bash
java -jar target/franchise-api-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --spring.datasource.url=jdbc:postgresql://your-db:5432/franchise_db \
  --spring.datasource.username=postgres \
  --spring.datasource.password=your-password
```

## ☁️ Despliegue

### Infraestructura AWS

El proyecto está completamente desplegado en AWS con la siguiente infraestructura:

#### Componentes Desplegados

1. **VPC (Virtual Private Cloud)**
   - Subnets públicas y privadas en múltiples AZs
   - Internet Gateway para acceso público
   - NAT Gateway para salida de subnets privadas
   - Route Tables configuradas

2. **Application Load Balancer (ALB)**
   - Distribución de tráfico HTTP
   - Health checks configurados
   - Acceso público en el puerto 80
   - URL: http://franchise-api-alb-2042942561.us-east-2.elb.amazonaws.com

3. **ECS Fargate (Elastic Container Service)**
   - Clúster ECS para orquestación de contenedores
   - Tasks de Fargate serverless (sin EC2)
   - Auto-scaling basado en CPU y memoria
   - Ejecución de contenedores Docker

4. **ECR (Elastic Container Registry)**
   - Registro privado de imágenes Docker
   - Integrado con el pipeline CI/CD
   - Versionamiento de imágenes

5. **RDS PostgreSQL**
   - Base de datos PostgreSQL 16
   - Multi-AZ para alta disponibilidad
   - Backups automáticos
   - Ubicado en subnets privadas

6. **ElastiCache Redis**
   - Cluster Redis 7 para caching
   - Mejora de performance de consultas
   - Ubicado en subnets privadas

7. **CloudWatch**
   - Logs centralizados de la aplicación
   - Métricas de ECS, RDS y ElastiCache
   - Alarmas configuradas

9. **Security Groups**
   - ALB: Permite tráfico HTTP (puerto 80)
   - ECS: Permite tráfico solo desde ALB
   - RDS: Permite tráfico solo desde ECS
   - Redis: Permite tráfico solo desde ECS

#### Variables de Entorno en GitHub

Para el despliegue automatizado, se configuraron las siguientes variables en GitHub:

**Secrets** (sensibles):
- `AWS_ACCESS_KEY_ID` - Credenciales de AWS
- `AWS_SECRET_ACCESS_KEY` - Credenciales de AWS
- `DB_PASSWORD` - Contraseña de la base de datos PostgreSQL
- `DB_USERNAME` - Usuario de la base de datos PostgreSQL

**Variables** (no sensibles):
- `AWS_REGION` - Región AWS
- `ECS_CLUSTER` - Clúster ECS
- `ECS_SERVICE` - Servicio ECS
- `RDS_ENDPOINT` - Endpoint de la base de datos PostgreSQL
- `REDIS_HOST` - Endpoint de ElastiCache Redis
- `REDIS_PORT` - Puerto de ElastiCache Redis
- `SERVER_PORT` - Puerto del servidor
- `SPRING_PROFILES_ACTIVE` - Perfil de Spring

### Despliegue Manual con Terraform

Si deseas replicar la infraestructura:

```bash
# 1. Configurar AWS CLI
aws configure

# 2. Navegar al directorio de Terraform
cd terraform

# 3. Copiar variables de ejemplo
cp terraform.tfvars.example terraform.tfvars

# 4. Editar variables según tu cuenta AWS
nano terraform.tfvars

# 5. Inicializar Terraform
terraform init

# 6. Revisar el plan de ejecución
terraform plan

# 7. Aplicar la infraestructura
terraform apply

# 8. Guardar los outputs (ALB URL, etc)
terraform output
```

## 🔄 CI/CD Pipeline

El proyecto utiliza **GitHub Actions** para automatizar el proceso de integración y despliegue continuo.

### Pipeline de CI/CD

El archivo [.github/workflows/ci-cd.yml](.github/workflows/ci-cd.yml) define el siguiente flujo:

#### 1. **Build and Test** (Se ejecuta en cada push/PR)
```yaml
- Checkout del código
- Setup de Java 25
- Compilación con Maven
- Ejecución de tests
- Empaquetado del JAR
- Upload del artefacto
```

#### 2. **Security Scan** (Se ejecuta después del build)
```yaml
- Análisis de vulnerabilidades con OWASP Dependency Check
- Reporte de CVEs encontrados
```

#### 3. **Docker Build** (Solo en push a main)
```yaml
- Login en AWS ECR
- Build de imagen Docker multi-stage
- Tag de la imagen (latest + SHA)
- Push a ECR
- Cache de layers para optimización
```

#### 4. **Deploy to AWS** (Solo en push a main)
```yaml
- Configuración de credenciales AWS
- Update del servicio ECS
- Force new deployment
- ECS pull la nueva imagen de ECR
- Rolling update sin downtime
```

### Triggers del Pipeline

- **Push a `main`**: Ejecuta todo el pipeline (build → test → docker → deploy)
- **Push a `develop`**: Solo build y test
- **Pull Request**: Solo build y test

### Visualización del Pipeline

```
┌─────────────────────────────────────────────────────┐
│  Push to main                                        │
└────────────────┬────────────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────┐
│  Job 1: Build & Test                     │
│  - Compile code                          │
│  - Run unit tests                        │
│  - Run integration tests                 │
│  - Package JAR                           │
└────────────────┬─────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────┐
│  Job 2: Security Scan                    │
│  - OWASP dependency check                │
└────────────────┬─────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────┐
│  Job 3: Docker Build                     │
│  - Build Docker image                    │
│  - Push to ECR                           │
└────────────────┬─────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────┐
│  Job 4: Deploy to AWS                    │
│  - Update ECS service                    │
│  - Rolling update                        │
│  - Zero downtime deployment              │
└──────────────────────────────────────────┘
```

### Rollback en caso de fallo

Si un despliegue falla, ECS automáticamente hace rollback a la versión anterior estable.

## ⚙️ Configuración

### Archivo .env.example

El proyecto incluye un archivo [.env.example](.env.example) con todas las variables de entorno necesarias:

```bash
# Spring Profile
SPRING_PROFILES_ACTIVE=dev

# Server
SERVER_PORT=8080

# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/franchise_db
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
```

### Perfiles de Spring

- **dev**: Desarrollo local con PostgreSQL y Redis locales
- **prod**: Producción con RDS PostgreSQL y ElastiCache Redis en AWS

### Configuración de Actuator

Endpoints de monitoreo disponibles:
- `/actuator/health` - Estado de la aplicación
- `/actuator/metrics` - Métricas de rendimiento

## 📝 Flujo de Trabajo con Git

El proyecto sigue las mejores prácticas de Git Flow:

- `main` - Código en producción
- `develop` - Integración de features
- `feature/*` - Nuevas funcionalidades
- `fix/*` - Corrección de bugs
- `hotfix/*` - Arreglos urgentes en producción

## 👤 Autor

**Jhon Salinas**
- GitHub: [@johssalinas](https://github.com/johssalinas)
- LinkedIn: [Jhon Salinas](https://www.linkedin.com/in/johssalinas/)

## 📄 Licencia

Este proyecto es privado y confidencial. Desarrollado como prueba técnica para Accenture.

---
