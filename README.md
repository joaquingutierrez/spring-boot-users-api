# spring-boot-users-api
API REST desarrollada con Spring Boot para la gestión de usuarios, incluyendo autenticación, validaciones, manejo centralizado de excepciones y una suite completa de pruebas automatizadas.

## Tecnologías

- Java 21
- Spring Boot 4
- Spring MVC
- Spring Data JPA
- Spring Security
- Hibernate
- MySQL
- H2 Database (Testing)
- JUnit 5
- Mockito
- MockMvc
- Maven
- JaCoCo


## Funcionalidades

- Crear usuarios
- Obtener usuario por ID
- Obtener usuario por email
- Listar usuarios
- Actualizar usuarios
- Eliminar usuarios
- Cambiar contraseña
- Validación de datos
- Validación personalizada de contraseñas
- Manejo global de excepciones

## Endpoints

| Método | Endpoint | Descripción |
|---------|----------|-------------|
| GET | /users | Obtener todos los usuarios |
| GET | /users/{id} | Obtener usuario por ID |
| GET | /users/search?email= | Buscar usuario por email |
| POST | /users | Crear usuario |
| PUT | /users/{id} | Actualizar usuario |
| DELETE | /users/{id} | Eliminar usuario |
| PATCH | /users/{id}/change-password | Cambiar contraseña |


## Ejemplos

### Crear usuario

POST /users

#### Request Body
```json
{
  "email": "joaquin@test.com",
  "name": "Joaquin",
  "lastName": "Gutierrez",
  "password": "Hola1234!"
}
```


#### Response Body (201 Created)

```json
{
  "id": 1,
  "email": "joaquin@test.com",
  "name": "Joaquin",
  "lastName": "Gutierrez",
  "createdAt": "2026-07-09T12:00:00",
  "role": "ROLE_USER"
}
```

### Cambiar Contraseña
PATCH /users/{id}/change-password

```json
{
  "oldPassword": "Hola1234!",
  "newPassword": "Hola1234!"
}
```

## Validaciones

La contraseña debe cumplir los siguientes requisitos:

- Entre 8 y 30 caracteres
- Al menos una mayúscula
- Al menos una minúscula
- Al menos un número
- Al menos un carácter especial

## Manejo de errores

La API devuelve respuestas consistentes para todos los errores.

Ejemplo:

```json
{
  "timestamp": "...",
  "status": 404,
  "code": "USER_NOT_FOUND",
  "message": "Usuario no encontrado."
}
```

## Testing

El proyecto incluye:

- Tests unitarios
  - Tests del Repository
  - Tests del Controller
  - Tests del Service
  - Tests del Mapper
  - Tests del GlobalExceptionHandler
  - Tests de Validacion de contraseña
- Tests de integración

Herramientas utilizadas:

- JUnit 5
- Mockito
- MockMvc
- H2 Database
- JaCoCo

## Ejecutar

Clonar el proyecto

```bash
git clone ...
```

Instalar dependencias

```bash
mvn clean install
```

Ejecutar

```bash
mvn spring-boot:run
```


## Ejecutar tests

```bash
mvn test
```

Generar reporte de cobertura

```bash
mvn clean verify
```

El reporte se encuentra en:

```
target/site/jacoco/index.html
```


## Características técnicas

- Arquitectura en capas
- DTOs para requests y responses
- Mapper dedicado
- Manejo centralizado de excepciones
- Validaciones con Bean Validation
- Validador personalizado para contraseñas
- Principios SOLID
- Suite completa de pruebas automatizadas
- Cobertura de código con JaCoCo