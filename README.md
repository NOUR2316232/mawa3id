# mawa3id

Appointment no-show reduction platform with:
- Business dashboard (manage services, availability, appointments, analytics)
- Public front-office booking page for customers
- Spring Boot backend + React frontend + MySQL

## Tech Stack
- Backend: Java 17, Spring Boot 3.2, Spring Security, Spring Data JPA, Maven
- Frontend: React 18, React Router, Zustand, Axios, Tailwind CSS
- Database: MySQL/MariaDB

## Repository Structure
- `backend/` Spring Boot API
- `frontend/` React app

## Current Defaults (from this repo)
- Backend port: `8088`
- Backend base URL: `http://localhost:8088`
- Frontend dev URL: `http://localhost:3000`
- Frontend API base (client): `http://localhost:8088/api`
- Database: `mawa3id` on `localhost:3306`
- DB user: `root`
- DB password: empty

## Prerequisites
- Java 17+
- Maven 3.8+
- Node.js 16+
- npm
- MySQL or MariaDB running locally

## 1) Configure Database
Backend config is in `backend/src/main/resources/application.properties`.

Current DB config:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mawa3id?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=
```

`createDatabaseIfNotExist=true` will create the database automatically if your DB user has permission.

## 2) Run Backend

```bash
cd backend
mvn spring-boot:run
```

Backend health check (manual):
- Open any existing API endpoint, e.g. login/register path prefix is `/api`

## 3) Run Frontend

```bash
cd frontend
npm install
npm start
```

Open:
- `http://localhost:3000`

## Test Accounts
Business login account available in this environment:
- Email: `test@mawa3id.local`
- Password: `test1234`

This same account is used for dashboard login and protected backend API access.

## App Access

### Dashboard (business side)
- URL: `http://localhost:3000`
- Login required

### Front-office (customer booking page)
- URL format: `http://localhost:3000/book/{businessId}`
- No login required

Working example (test business):
- `http://localhost:3000/book/dea3d18e-28f4-4c56-897b-2a1cff72627d`

## Main API Overview
Base API: `http://localhost:8088/api`

### Public
- `GET /public/booking/{businessId}`
  - Returns business profile + services + availability for front-office page
- `POST /public/booking/{businessId}/appointments`
  - Creates appointment request without authentication
- `POST /appointments/public/confirm/{token}`
- `POST /appointments/public/cancel/{token}`

### Auth
- `POST /register`
- `POST /login`

### Protected (JWT required)
- `GET/PUT /profile`
- `GET/POST/PUT/DELETE /services`
- `GET/POST/PUT/DELETE /availability`
- `GET/POST/PUT/DELETE /appointments`
- `GET /analytics`

## Authentication Notes
- Security config permits:
  - `POST /api/register`
  - `POST /api/login`
  - all `/api/public/**`
- Everything else requires `Authorization: Bearer <token>`

## Build / Test

### Backend
```bash
cd backend
mvn test
```

### Frontend
```bash
cd frontend
npm run build
```

## Common Issues

### 1) `mvn spring-boot:run` fails at startup
- Ensure MySQL/MariaDB is running on port `3306`
- Verify DB credentials in `backend/src/main/resources/application.properties`
- Check that your DB user can create DB/schema when using `createDatabaseIfNotExist=true`

### 2) Frontend cannot reach backend
- Ensure backend is running on `8088`
- Confirm `frontend/src/api/client.js` base URL is `http://localhost:8088/api`

### 3) 403 on register/login
- Use URLs with `/api` prefix when calling backend directly:
  - `http://localhost:8088/api/register`
  - `http://localhost:8088/api/login`

## Notes
- There are existing module READMEs in `backend/README.md` and `frontend/README.md`, but this root README reflects the current integrated setup.
