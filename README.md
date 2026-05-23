# RestLab

_Programare Web - Indrumare de laborator prof. dr. ing. Dominic Mircea KRISTÁLY_

RestLab is a full-stack product catalog application built as the final result of the web programming labs.

## What the project covers

- REST API development with Spring Boot
- JSON responses, `@RequestBody`, `ResponseEntity`, `@PutMapping`, `@DeleteMapping`
- DTOs, validation, and centralized error handling
- persistence with Spring Data JPA and MySQL
- logging, metrics, and monitoring support
- Spring Security with JWT authentication and role-based access
- automated backend testing
- React frontend with `useEffect`, Axios, controlled forms, CRUD actions, toast notifications, and delete confirmation

## Scope

The application manages products in a catalog:

- lists products from the backend
- filters products by name on the client side
- shows product details
- creates and edits products through forms
- deletes products with confirmation
- validates data on both frontend and backend

## Project structure

```text
backend/   Spring Boot REST API, security, persistence, validation, tests
frontend/  React + Vite interface for the product catalog
```

## Lab progression behind the project

| Lab | Topic | What was added to this project |
| --- | --- | --- |
| 4 | REST API with Spring Boot | controller basics, JSON, request/response handling |
| 5 | Spring Data JPA and MySQL | persistent products and categories |
| 6 | DTOs, validation, error handling | request/response DTOs and centralized exceptions |
| 7 | Logging and metrics | application logs and metrics integration |
| 8 | Spring Security and JWT | authentication, authorization, CORS |
| 9 | Automated testing | backend tests for main layers |
| 10 | React basics | components, props, state, JSX |
| 11 | REST API integration in React | `useEffect`, Axios, loading product lists |
| 12 | CRUD in React | controlled forms, create/edit/delete, toast feedback |

## Main technologies

- React
- Vite
- Axios
- Spring Boot
- Spring Security
- Spring Data JPA
- JWT
- MySQL

## Run locally

1. Start the MySQL container from `backend/docker-compose.yml`.
2. Run the Spring Boot backend from `backend/`.
3. Run the React frontend from `frontend/`.
4. Open the frontend in the browser.

During development, the frontend uses `http://localhost:8080/api`.

## Database

The project uses MySQL in Docker. The compose file is in `backend/docker-compose.yml` and creates:

- database name: `rest_lab_db`
- user: `labuser`
- password: `labpass`
- port: `3306`
