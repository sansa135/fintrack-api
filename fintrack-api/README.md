# FinTrack API

A secure REST API for personal expense tracking, built with Java 17 and Spring Boot 3.

## Features
- JWT authentication (register / login), BCrypt password hashing, stateless sessions
- Expense CRUD with per-user data isolation (users can only access their own records)
- Pagination, sorting and category filtering
- Monthly spend-by-category summary (JPQL aggregate query)
- Bean Validation with structured 400 error responses
- Integration tests (MockMvc), Docker + MySQL via docker-compose, GitHub Actions CI

## Tech stack
Java 17 · Spring Boot · Spring Security · Spring Data JPA (Hibernate) · MySQL / H2 · JUnit 5 · Maven · Docker

## Run locally (H2, zero setup)
    mvn spring-boot:run

## Run with MySQL
    docker compose up --build

## API
| Method | Endpoint | Description |
|---|---|---|
| POST | /api/auth/register | Create account, returns JWT |
| POST | /api/auth/login | Returns JWT |
| POST | /api/expenses | Create expense |
| GET | /api/expenses?category=&page=&size=&sort= | List (paginated) |
| GET | /api/expenses/{id} | Get one |
| PUT | /api/expenses/{id} | Update |
| DELETE | /api/expenses/{id} | Delete |
| GET | /api/expenses/summary?month=2026-10 | Total per category |

Send the token as `Authorization: Bearer <token>`.

## Example
    curl -X POST localhost:8080/api/auth/register -H "Content-Type: application/json" \
      -d '{"username":"sanskruti","password":"secret123"}'

    curl -X POST localhost:8080/api/expenses -H "Authorization: Bearer <token>" \
      -H "Content-Type: application/json" \
      -d '{"amount":250.50,"category":"Food","description":"Lunch","date":"2026-10-05"}'

## Run tests
    mvn verify
