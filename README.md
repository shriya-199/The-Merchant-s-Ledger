# The Merchant's Ledger

Full-stack inventory and merchant operations platform built with:
- `Spring Boot` + `PostgreSQL` backend
- `React + Vite` frontend
- `RabbitMQ` eventing for async inventory movement propagation
- `Redis` caching for read-heavy API paths
- JWT auth, role-based access, inventory workflows, reconciliation, alerts, analytics, and exports

## Quick Start (Local)

1. Create PostgreSQL database: `merchant_ledger`
2. Start backend:
   - `cd backend`
   - `mvn spring-boot:run`
3. Start frontend:
   - `cd front-end`
   - `npm install`
   - `npm run dev`
4. Open `http://localhost:5173`

Default admin login:
- Email: `admin@ledger.com`
- Password: `Admin123!`

## Environment Variables

Frontend (`front-end/.env`):
- `VITE_API_BASE_URL` (default local backend: `http://localhost:8080`)
- `VITE_GOOGLE_CLIENT_ID` (optional for Google auth)

Backend (`backend/src/main/resources/application.yml` supports env overrides):
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `CORS_ALLOWED_ORIGINS` (comma-separated)
- `RABBITMQ_ENABLED` (`true` to enable async event bus)
- `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`
- `CACHE_TYPE` (`simple` or `redis`)
- `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`

## Deployment Notes

- `render.yaml` is included for Render deployment.
- Frontend Docker image uses Nginx SPA fallback (`try_files ... /index.html`) so direct route refreshes work.

## Production Checks

- Frontend:
  - `npm run lint` (passes with warnings only)
  - `npm run build` (passes)
- Backend:
  - `mvn -DskipTests package` (passes)
