# Customer Support Hub

## Overview

Customer Support Hub is a secure backend service built with Spring Boot.

The system manages customers, agents, and support tickets while enforcing role-based access control using JWT authentication.

The application supports three roles:

* ADMIN
* AGENT
* CUSTOMER

Customers can create and track support tickets.

Agents can manage their assigned customers and view their tickets.

Admins have full access to all customers and tickets.

---

## Technology Stack

| Category | Technology |
|-----------|------------|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security, JWT |
| Persistence | Spring Data JPA, Hibernate |
| Database | MySQL 8.4 |
| Build Tool | Maven |
| Testing | JUnit 5, Mockito |
| Containerization | Docker, Docker Compose |


---

## Features

### Authentication & Security

* JWT Authentication
* Stateless Security
* Role-Based Authorization
* Spring Security
* BCrypt Password Hashing
* Protected REST APIs

### Customer Management

* Create Customer
* View Customers
* View Personal Profile
* Update Personal Profile

### Ticket Management

* Create Ticket
* View My Tickets
* View All Tickets
* Filter Tickets By Status
* View Ticket By ID
* Update Ticket Status
* Assign Ticket To Agent

### Error Handling

Centralized exception handling using:

* BadRequestException
* UnauthorizedException
* ConflictException
* NotFoundException
* AccessDeniedException

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
JwtService

UserController
UserService
UserRepository

TicketController
TicketService
TicketRepository

SecurityConfig
GlobalExceptionHandler
DataInitializer
```

---

## Security Model

### CUSTOMER

Allowed Operations:

* Login
* View own profile
* Update own profile
* Create tickets
* View own tickets
* View own ticket by ID

### AGENT

Allowed Operations:

* Login
* Create customers
* View assigned customers
* View assigned customer tickets
* View assigned customer ticket by ID
* Update ticket status for assigned customers
* Update own profile

### ADMIN

Allowed Operations:

* Login
* View all customers
* Create customers
* View all tickets
* View any ticket
* Update ticket status
* Assign tickets to agents
* Update own profile

---

## Database Schema

### Users

| Column   | Description                 |
| -------- | --------------------------- |
| id       | User ID                     |
| username | Unique username             |
| password | BCrypt hashed password      |
| name     | Full name                   |
| email    | Unique email                |
| role     | ADMIN / AGENT / CUSTOMER    |
| agent_id | Assigned agent for customer |

### Tickets

| Column      | Description                 |
| ----------- | --------------------------- |
| id          | Ticket ID                   |
| title       | Ticket title                |
| description | Ticket description          |
| status      | OPEN / IN_PROGRESS / CLOSED |
| created_at  | Creation timestamp          |
| customer_id | Ticket owner                |

---

## Default Users

The application creates the following users automatically on startup.

### Admin

```text
username: admin
password: admin123
```

### Agent

```text
username: agent
password: agent123
```

---

## Authentication

### Login

```http
POST /api/auth/login
```

Request:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

Response:

```json
{
  "token": "jwt-token"
}
```

---

## API Endpoints

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

Behavior:

* ADMIN receives all customers
* AGENT receives only assigned customers

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

Supported statuses:

```text
OPEN
IN_PROGRESS
CLOSED
```

---

### Get Ticket By ID

```http
GET /api/tickets/{id}
```

Roles:

```text
CUSTOMER
AGENT
ADMIN
```

Access Rules:

* CUSTOMER can access only own tickets
* AGENT can access only tickets of assigned customers
* ADMIN can access any ticket

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

Supported values:

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

Example response:

```json
{
  "timestamp": "2026-06-20T08:45:05",
  "status": 404,
  "message": "Ticket not found"
}
```

Supported status codes:

| Status | Description  |
| ------ | ------------ |
| 200    | OK           |
| 201    | Created      |
| 400    | Bad Request  |
| 401    | Unauthorized |
| 403    | Forbidden    |
| 404    | Not Found    |
| 409    | Conflict     |

---

## Running Locally

Build:

```bash
mvn clean package
```

Run:

```bash
java -jar target/customer-support-hub-0.0.1-SNAPSHOT.jar
```

Application URL:

```text
http://localhost:8080
```

---

## Running With Docker

Build and Start:

```bash
mvn clean package
docker compose up -d --build
```

Verify:

```bash
docker compose ps
```

Expected:

```text
customer-support-app     Up
customer-support-mysql   Up (healthy)
```

Stop:

```bash
docker compose down
```

---

## Testing

Run all tests:

```bash
mvn clean test
```

Current tests cover:

* UserService validation
* TicketService business logic
* Security authorization rules
* Spring context loading

---

## Design Decisions

* Monolithic Spring Boot application
* JWT-based authentication
* Role-based authorization using Spring Security
* MySQL relational database
* Layered architecture
* Centralized exception handling
* Dockerized deployment

---

## Future Improvements

* Pagination
* Ticket comments
* Additional authorization tests
* API documentation

---

## Author

Avihay Ben Ishay

Backend Developer
