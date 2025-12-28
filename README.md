# Rice Mill Management System - Backend

Production-ready REST API backend for Rice Mill management system built with Java 21 and Spring Boot 3.x.

## Features

- **Authentication & Authorization**: JWT-based auth with role-based access control (ADMIN, MANAGER, STAFF)
- **Inventory Management**: Complete stock management for PADDY and RICE with real-time tracking
- **Warehouse Management**: Multi-warehouse support with capacity tracking
- **Stock Operations**: Inbound, Outbound, Transfer, Adjustment, and Processing operations
- **Processing Records**: Track paddy-to-rice conversion with yield calculations
- **Reports**: Movement reports, processing reports, and stock ledger
- **Dashboard**: Real-time statistics, warehouse utilization, and low stock alerts
- **Audit Trail**: Complete tracking of who did what and when
- **Concurrency Control**: Optimistic locking to prevent race conditions
- **Soft Delete**: Safe deletion with recovery capability

## Tech Stack

- **Java 21**
- **Spring Boot 3.2.1**
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - Spring Validation
  - Spring Actuator
- **MySQL 8.0**
- **Flyway** for database migrations
- **JWT (jjwt 0.12.3)** for authentication
- **Lombok** for boilerplate reduction
- **MapStruct** for DTO mapping
- **SpringDoc OpenAPI** for API documentation
- **Testcontainers** for integration testing

## Prerequisites

- Java 21 or higher
- Maven 3.8+
- MySQL 8.0 (or use Docker Compose)
- IntelliJ IDEA (recommended)

## Quick Start

### 1. Clone and Setup

```bash
cd rice_mill
cp .env.example .env
# Edit .env with your configuration
```

### 2. Start PostgreSQL (Option A: Docker)

```bash
docker-compose up -d
```

### 2. Start MySQL (Option B: Local Installation)

Install MySQL and create database:
```sql
CREATE DATABASE ricemill_dev;
```

Update `.env` with your MySQL credentials.

### 3. Build and Run

```bash
# Build
./mvnw clean install

# Run
./mvnw spring-boot:run
```

Or in IntelliJ IDEA:
1. Open project
2. Wait for Maven dependencies to download
3. Run `RiceMillApplication.java`

### 4. Access the Application

- **API Base URL**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **API Docs**: http://localhost:8080/api/docs
- **Health Check**: http://localhost:8080/actuator/health

### 5. Default Admin Credentials

```
Username: admin
Password: admin123
```

**⚠️ Change these in production!**

## API Endpoints

### Authentication
- `POST /api/auth/login` - Login
- `POST /api/auth/refresh` - Refresh access token
- `POST /api/auth/logout` - Logout
- `GET /api/auth/me` - Get current user
- `POST /api/auth/register` - Register new user (Admin only)

### Users (Admin only)
- `GET /api/users` - List users
- `GET /api/users/{id}` - Get user
- `POST /api/users` - Create user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `POST /api/users/{id}/reset-password` - Reset password

### Warehouses
- `GET /api/warehouses` - List warehouses
- `GET /api/warehouses/{id}` - Get warehouse
- `POST /api/warehouses` - Create warehouse
- `PUT /api/warehouses/{id}` - Update warehouse
- `DELETE /api/warehouses/{id}` - Delete warehouse

### Stock Management
- `GET /api/stocks?type=PADDY|RICE&warehouseId=...` - List stock
- `GET /api/stocks/summary?type=PADDY|RICE` - Stock summary
- `POST /api/stocks/inbound` - Add stock
- `POST /api/stocks/outbound` - Remove stock
- `POST /api/stocks/transfer` - Transfer between warehouses
- `POST /api/stocks/adjust` - Adjust stock quantity
- `POST /api/stocks/process` - Process paddy to rice

### Suppliers & Customers
- `GET /api/suppliers` - List suppliers
- `POST /api/suppliers` - Create supplier
- `PUT /api/suppliers/{id}` - Update supplier
- `DELETE /api/suppliers/{id}` - Delete supplier
- Similar endpoints for `/api/customers`

### Reports
- `GET /api/reports/movements` - Movement report
- `GET /api/reports/processing` - Processing report

### Dashboard
- `GET /api/dashboard` - Dashboard statistics

### Settings
- `GET /api/settings` - Get settings
- `PUT /api/settings` - Update settings (Admin only)

## API Response Format

All responses follow this format:

**Success:**
```json
{
  "success": true,
  "data": { ... },
  "error": null
}
```

**Error:**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "ERROR_CODE",
    "message": "Error message",
    "details": ["field1: error detail", "field2: error detail"]
  }
}
```

## Authentication

Use JWT Bearer token in Authorization header:

```
Authorization: Bearer <access_token>
```

### Login Example

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "admin123"
  }'
```

Response:
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGc...",
    "refreshToken": "550e8400-...",
    "user": {
      "id": "...",
      "username": "admin",
      "email": "admin@ricemill.com",
      "fullName": "System Administrator",
      "roles": ["ADMIN"]
    }
  },
  "error": null
}
```

## Role-Based Access Control

### ADMIN
- Full system access
- User management
- Settings management
- All inventory operations

### MANAGER
- Inventory operations
- Reports
- Warehouse CRUD
- Supplier/Customer management
- Cannot manage users or settings

### STAFF
- View inventory
- Create stock movements (inbound, outbound, transfer, process)
- Limited access to reports
- Cannot manage users, settings, or warehouses

## Database Schema

Key tables:
- `users` - User accounts
- `user_roles` - User role assignments
- `refresh_tokens` - JWT refresh tokens
- `warehouses` - Warehouse locations
- `suppliers` - Supplier information
- `customers` - Customer information
- `batches` - Stock batches/lots
- `inventory_balance` - Current stock levels
- `stock_movements` - All stock transactions
- `processing_records` - Paddy to rice conversions
- `settings` - Application settings

## Configuration

### Profiles

- `dev` - Development (default)
- `test` - Testing
- `prod` - Production

Set profile:
```bash
export SPRING_PROFILES_ACTIVE=prod
./mvnw spring-boot:run
```

### Environment Variables

See `.env.example` for all available variables.

## Testing

### Run All Tests

```bash
./mvnw test
```

### Run Integration Tests

```bash
./mvnw verify
```

Tests use Testcontainers to spin up PostgreSQL automatically.

## Building for Production

```bash
./mvnw clean package -DskipTests
```

JAR file will be in `target/rice-mill-backend-1.0.0.jar`

### Run Production JAR

```bash
java -jar target/rice-mill-backend-1.0.0.jar \
  --spring.profiles.active=prod \
  --DB_HOST=your-db-host \
  --DB_PASSWORD=your-db-password \
  --JWT_SECRET=your-secret-key
```

## Security Best Practices

1. **Change default admin credentials** immediately
2. **Use strong JWT secret** (min 256 bits) in production
3. **Enable HTTPS** in production
4. **Restrict CORS origins** to your frontend domain
5. **Use environment variables** for sensitive data
6. **Regular security updates** for dependencies
7. **Enable database SSL** in production

## Troubleshooting

### Database Connection Issues

```bash
# Check MySQL is running
docker-compose ps

# View logs
docker-compose logs mysql

# Restart
docker-compose restart mysql
```

### Port Already in Use

Change port in `application.yml`:
```yaml
server:
  port: 8081
```

### Flyway Migration Errors

```bash
# Clean and rebuild
./mvnw clean flyway:clean flyway:migrate
```

## Project Structure

```
rice_mill/
├── src/
│   ├── main/
│   │   ├── java/com/ricemill/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── exception/       # Exception handling
│   │   │   ├── mapper/          # MapStruct mappers
│   │   │   ├── repository/      # JPA repositories
│   │   │   ├── security/        # Security & JWT
│   │   │   ├── service/         # Business logic
│   │   │   └── RiceMillApplication.java
│   │   └── resources/
│   │       ├── db/migration/    # Flyway migrations
│   │       └── application.yml
│   └── test/                    # Tests
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Support

For issues and questions, please check:
1. Swagger UI documentation
2. Application logs
3. Database logs

## License

Proprietary - All rights reserved
#   R i c e _ M i l l _ B a c k e n d  
 #   R i c e _ M i l l _ B a c k e n d  
 #   R i c e _ M i l l _ B a c k e n d  
 