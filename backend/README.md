# mawa3id Backend

This is the Spring Boot backend for the mawa3id appointment no-show reduction system.

## Requirements
- Java 17+
- Maven 3.8+
- PostgreSQL 13+

## Configuration

1. Create PostgreSQL database:
```sql
CREATE DATABASE mawa3id;
```

2. Update `src/main/resources/application.properties` with your database credentials

3. Update JWT secret key in `application.properties`

## Building

```bash
mvn clean install
```

## Running

```bash
mvn spring-boot:run
```

The server will start on `http://localhost:8080`

## API Documentation

All API endpoints require JWT authentication (except login and register).

Include the token in the Authorization header:
```
Authorization: Bearer <your_jwt_token>
```

See the main README.md for API endpoint details.
