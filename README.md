# Customer Support Hub

## Overview

Customer Support Hub is a secure backend service built with Spring Boot.

The system manages customers, agents, and support tickets while enforcing role-based access control using JWT authentication.

Three user roles are supported:

- ADMIN
- AGENT
- CUSTOMER

Customers can create and track support tickets.

Agents can manage their assigned customers and monitor their tickets.

Admins have full access to all customers and tickets.

---

## Features

### Authentication & Security

- JWT Authentication
- Stateless Security
- Role-Based Authorization
- Spring Security
- Protected REST APIs

### Customer Management

- Create Customer
- View Customers
- View Personal Profile
- Update Personal Profile

### Ticket Management

- Create Ticket
- View My Tickets
- View All Tickets
- Filter Tickets By Status
- View Ticket By ID
- Update Ticket Status
- Assign Ticket To Agent

### Error Handling

Centralized exception handling using:

- BadRequestException
- UnauthorizedException
- ConflictException
- NotFoundException

---

## Technology Stack

| Technology | Version |
|------------|----------|
| Java | 21 |
| Spring Boot | 3.5 |
| Spring Security | 6 |
| Spring Data JPA | Latest |
| Hibernate | 6 |
| MySQL | 8.4 |
| Maven | 3 |
| Docker | Latest |
| Docker Compose | Latest |
| JUnit 5 | Latest |
| Mockito | Latest |

---

## Architecture

The application follows a layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
MySQL
```

### Main Components

```text
AuthController
AuthService

UserController
UserService
UserRepository

TicketController
TicketService
TicketRepository

SecurityConfig
JwtService

GlobalExceptionHandler
```

---

## Security Model

### CUSTOMER

Allowed Operations:

- View own profile
- Update own profile
- Create tickets
- View own tickets

### AGENT

Allowed Operations:

- Create customers
- View assigned customers
- View assigned customer tickets
- Update ticket status

### ADMIN

Allowed Operations:

- View all customers
- Create customers
- View all tickets
- Update ticket status
- Assign tickets to agents

---

## Database Schema

### Users

| Column | Type |
|----------|----------|
| id | BIGINT |
| username | VARCHAR |
| password | VARCHAR |
| name | VARCHAR |
| email | VARCHAR |
| role | ENUM |
| agent_id | BIGINT |

### Tickets

| Column | Type |
|----------|----------|
| id | BIGINT |
| title | VARCHAR |
| description | VARCHAR |
| status | ENUM |
| created_at | DATETIME |
| customer_id | BIGINT |

---

## Authentication

### Login

### Request

POST `/api/auth/login`

```json
{
  "username": "admin",
  "password": "admin123"
}
```

### Response

```json
{
  "token": "jwt-token"
}
```

---

## API Endpoints

# Authentication

### Login

```http
POST /api/auth/login
```

---

# Customers

### Create Customer

```http
POST /api/customers
```

Roles:

```text
AGENT
ADMIN
```

Example:

```json
{
  "username": "customer1",
  "password": "customer123",
  "name": "Customer One",
  "email": "customer1@test.com",
  "agentId": 2
}
```

---

### Get Customers

```http
GET /api/customers
```

Roles:

```text
AGENT
ADMIN
```

---

### Get My Profile

```http
GET /api/customers/me
```

Roles:

```text
CUSTOMER
AGENT
ADMIN
```

---

### Update My Profile

```http
PUT /api/customers/me
```

Roles:

```text
CUSTOMER
AGENT
ADMIN
```

---

# Tickets

### Create Ticket

```http
POST /api/tickets
```

Role:

```text
CUSTOMER
```

Example:

```json
{
  "title": "Login issue",
  "description": "Unable to login to the system"
}
```

---

### Get My Tickets

```http
GET /api/tickets/my
```

Role:

```text
CUSTOMER
```

---

### Get Tickets

```http
GET /api/tickets
```

Roles:

```text
AGENT
ADMIN
```

Optional filter:

```http
GET /api/tickets?status=OPEN
```

Possible statuses:

```text
OPEN
IN_PROGRESS
CLOSED
```

---

### Get Ticket By Id

```http
GET /api/tickets/{id}
```

Roles:

```text
AGENT
ADMIN
```

---

### Update Ticket Status

```http
PUT /api/tickets/{id}/status
```

Roles:

```text
AGENT
ADMIN
```

Request:

```json
{
  "status": "IN_PROGRESS"
}
```

Possible values:

```text
OPEN
IN_PROGRESS
CLOSED
```

---

### Assign Ticket To Agent

```http
PUT /api/tickets/{id}/assign/{agentId}
```

Role:

```text
ADMIN
```

Example:

```http
PUT /api/tickets/1/assign/2
```

---

## Exception Handling

The application provides consistent error responses.

Example:

```json
{
  "timestamp": "2026-06-19T22:23:16",
  "status": 404,
  "message": "Ticket not found"
}
```

Supported errors:

| Status | Description |
|----------|----------|
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 409 | Conflict |

---

## Running Locally

### Build

```bash
mvn clean package
```

### Run

```bash
java -jar target/customer-support-hub-0.0.1-SNAPSHOT.jar
```

Application:

```text
http://localhost:8080
```

---

## Running With Docker

### Build

```bash
docker build -t customer-support-hub .
```

### Start

```bash
docker compose up -d --build
```

### Verify

```bash
docker compose ps
```

Expected:

```text
customer-support-app     Up
customer-support-mysql   Up (healthy)
```

### Stop

```bash
docker compose down
```

---

## Testing

Run all tests:

```bash
mvn test
```

Current test coverage includes:

- UserService tests
- TicketService tests
- Security authorization tests
- Spring context loading tests

---

## Design Decisions & Tradeoffs

### Chosen

- Monolithic architecture
- JWT-based authentication
- MySQL relational database
- Layered architecture
- Hibernate automatic schema management

### Not Implemented

To keep the assignment focused and concise:

- Refresh Tokens
- Email Notifications
- Audit Logging
- Pagination
- Integration Tests with Testcontainers
- Swagger/OpenAPI
- CI/CD Pipeline

---

## Future Improvements

- Swagger Documentation
- Refresh Tokens
- Pagination
- Ticket Comments
- Ticket Attachments
- Audit Trail
- Event Driven Architecture
- Testcontainers Integration Tests
- GitHub Actions CI/CD Pipeline

---

## Author

Avihay Ben Ishay

Backend Developer

Java | Spring Boot | Microservices | Kafka | Docker | Kubernetes