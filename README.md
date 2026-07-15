# Al Noor Airways

A full-stack flight booking app: search, book, and manage flight reservations, with a premium Emirates-inspired look and feel. Fictional airline, built as a demonstration project.

## Stack

- **Frontend:** React (Vite) + React Router, plain CSS design system (no UI framework)
- **Backend:** Spring Boot 4 (Java 21) + Spring Data JPA + Spring Security (session-cookie auth)
- **Database:** PostgreSQL
- **Deployment:** single Docker image (frontend bundled into the Spring Boot jar's static resources), deployed to Render

## Features

- Search one-way or round-trip flights across a network of routes out of Dubai (DXB)
- Economy / Business / First cabin classes with independent seat inventory and fares per flight
- Multi-passenger checkout with passenger details and a booking receipt (PNR, seats, total)
- Concurrency-safe seat booking (pessimistic row locking prevents overbooking under concurrent requests)
- My Bookings: view and cancel confirmed reservations (seats are restored on cancellation)

## Local development

Requires Java 21, Node 20+, and a Postgres instance.

**Backend** (from `backend/`):

```bash
cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties
# edit application-local.properties with your Postgres connection details
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

**Frontend** (from the repo root):

```bash
npm install
npm run dev
```

The Vite dev server proxies `/api/**` to `http://localhost:8080`.

## Production build

```bash
docker build -t al-noor-airways .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=... \
  -e SPRING_DATASOURCE_USERNAME=... \
  -e SPRING_DATASOURCE_PASSWORD=... \
  al-noor-airways
```

See `render.yaml` for the Render deployment configuration.
