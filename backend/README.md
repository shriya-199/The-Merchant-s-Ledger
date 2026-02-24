# Merchant's Ledger Backend

## Run

1. Ensure PostgreSQL is running.
2. Create a database named `merchant_ledger`.
3. Set environment variables if needed:

```
DB_URL=jdbc:postgresql://localhost:5432/merchant_ledger
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=change-me-please-change
RABBITMQ_ENABLED=false
CACHE_TYPE=simple
```

4. Start the API:

```
mvn spring-boot:run
```

## Seeded credentials

- Email: admin@ledger.com
- Password: Admin123!
- Password policy: 8+ chars with upper, lower, number, symbol
- Roles: ADMIN, MANAGER, STAFF (ADMIN cannot be self-assigned)

## API

- POST `/api/auth/register`
- POST `/api/auth/login`
- GET `/api/auth/me`
- PUT `/api/users/me`
- GET/POST `/api/customers`
- GET/POST `/api/ledger`
- GET `/api/summary`
- GET/POST `/api/warehouses`
- GET/POST `/api/products`
- GET `/api/products/barcode/{barcode}`
- GET `/api/inventory/summary`
- GET `/api/inventory/stock`
- GET/POST `/api/inventory/movements`
- GET `/api/inventory/low-stock`
- GET `/api/analytics`
- GET `/api/exports/stock.csv`
- GET `/api/exports/movements.csv`
- GET `/api/notifications`
- POST `/api/notifications/{id}/read`
- GET `/api/audit`
- GET `/api/admin/users`
- PUT `/api/admin/users/{id}`

## Real-time

- WebSocket endpoint: `/ws`
- Stock updates topic: `/topic/stock`
- WebSocket requires `Authorization: Bearer <token>` header
- Alerts topic: `/topic/alerts`

## Eventing + Cache

- RabbitMQ event bus (optional):
  - Enable with `RABBITMQ_ENABLED=true`
  - Configure broker via `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`
  - Exchange/queue/routing envs:
    - `INVENTORY_EXCHANGE`
    - `INVENTORY_QUEUE`
    - `INVENTORY_ROUTING_KEY`
  - Inventory movements are published to RabbitMQ and consumed for async stock topic fan-out.

- Redis cache (optional):
  - Enable with `CACHE_TYPE=redis`
  - Configure via `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`
  - Cached endpoints include inventory list/summary/low-stock/movements and analytics.
