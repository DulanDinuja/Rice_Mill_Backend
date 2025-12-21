# Rice Mill Backend - Project Structure

## Complete File Tree

```
rice_mill/
├── pom.xml
├── docker-compose.yml
├── .env.example
├── .gitignore
├── README.md
├── src/
│   ├── main/
│   │   ├── java/com/ricemill/
│   │   │   ├── RiceMillApplication.java
│   │   │   ├── config/
│   │   │   │   ├── CorsConfig.java
│   │   │   │   ├── DataInitializer.java
│   │   │   │   ├── JpaConfig.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── CustomerController.java
│   │   │   │   ├── DashboardController.java
│   │   │   │   ├── ReportController.java
│   │   │   │   ├── SettingsController.java
│   │   │   │   ├── StockController.java
│   │   │   │   ├── SupplierController.java
│   │   │   │   ├── UserController.java
│   │   │   │   └── WarehouseController.java
│   │   │   ├── dto/
│   │   │   │   ├── ApiResponse.java
│   │   │   │   ├── AuthDto.java
│   │   │   │   ├── CustomerDto.java
│   │   │   │   ├── DashboardDto.java
│   │   │   │   ├── ErrorResponse.java
│   │   │   │   ├── ReportDto.java
│   │   │   │   ├── SettingsDto.java
│   │   │   │   ├── StockDto.java
│   │   │   │   ├── SupplierDto.java
│   │   │   │   ├── UserDto.java
│   │   │   │   └── WarehouseDto.java
│   │   │   ├── entity/
│   │   │   │   ├── BaseEntity.java
│   │   │   │   ├── Batch.java
│   │   │   │   ├── Customer.java
│   │   │   │   ├── InventoryBalance.java
│   │   │   │   ├── MovementType.java
│   │   │   │   ├── ProcessingRecord.java
│   │   │   │   ├── ProductType.java
│   │   │   │   ├── RefreshToken.java
│   │   │   │   ├── Settings.java
│   │   │   │   ├── StockMovement.java
│   │   │   │   ├── Supplier.java
│   │   │   │   ├── User.java
│   │   │   │   ├── UserRole.java
│   │   │   │   └── Warehouse.java
│   │   │   ├── exception/
│   │   │   │   ├── BusinessException.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── repository/
│   │   │   │   ├── BatchRepository.java
│   │   │   │   ├── CustomerRepository.java
│   │   │   │   ├── InventoryBalanceRepository.java
│   │   │   │   ├── ProcessingRecordRepository.java
│   │   │   │   ├── RefreshTokenRepository.java
│   │   │   │   ├── SettingsRepository.java
│   │   │   │   ├── StockMovementRepository.java
│   │   │   │   ├── SupplierRepository.java
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── WarehouseRepository.java
│   │   │   ├── security/
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── JwtUtil.java
│   │   │   └── service/
│   │   │       ├── AuthService.java
│   │   │       ├── CustomerService.java
│   │   │       ├── DashboardService.java
│   │   │       ├── ReportService.java
│   │   │       ├── SettingsService.java
│   │   │       ├── StockService.java
│   │   │       ├── SupplierService.java
│   │   │       ├── UserService.java
│   │   │       └── WarehouseService.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/
│   │           └── V1__init.sql
│   └── test/
│       ├── java/com/ricemill/
│       │   ├── AuthIntegrationTest.java
│       │   └── service/
│       │       └── StockServiceTest.java
│       └── resources/
│           └── application.yml
```

## Module Summary

### Total Files Created: 60+

### Configuration (5 files)
- SecurityConfig: JWT authentication, role-based access
- CorsConfig: CORS configuration for React frontend
- JpaConfig: Auditing configuration
- DataInitializer: Admin user setup
- application.yml: Multi-profile configuration

### Entities (14 files)
- User, UserRole, RefreshToken
- Warehouse, Supplier, Customer
- Batch, InventoryBalance, StockMovement
- ProcessingRecord, Settings
- ProductType, MovementType enums
- BaseEntity with audit fields

### Repositories (10 files)
All JPA repositories with custom queries for filtering and reporting

### Services (9 files)
- AuthService: Login, refresh, logout
- StockService: All inventory operations with concurrency control
- UserService, WarehouseService, SupplierService, CustomerService
- ReportService, DashboardService, SettingsService

### Controllers (9 files)
REST endpoints for all modules with Swagger documentation

### DTOs (11 files)
Request/Response DTOs with validation

### Security (3 files)
- JwtUtil: Token generation and validation
- JwtAuthenticationFilter: Request authentication
- CustomUserDetailsService: User loading

### Exception Handling (3 files)
- GlobalExceptionHandler: Centralized error handling
- BusinessException, ResourceNotFoundException

### Database (1 file)
- V1__init.sql: Complete schema with indexes

### Tests (2 files)
- AuthIntegrationTest: Integration test with Testcontainers
- StockServiceTest: Unit test for business logic

### Documentation (3 files)
- README.md: Complete setup and usage guide
- .env.example: Environment variables template
- docker-compose.yml: PostgreSQL setup

## Key Features Implemented

✅ JWT Authentication with refresh tokens
✅ Role-based authorization (ADMIN, MANAGER, STAFF)
✅ Complete CRUD for all entities
✅ Stock operations: Inbound, Outbound, Transfer, Adjustment, Processing
✅ Concurrency control with pessimistic locking
✅ Negative stock prevention with admin override
✅ Audit trail (createdBy, updatedBy, timestamps)
✅ Soft delete functionality
✅ Pagination and sorting
✅ Comprehensive validation
✅ Global exception handling
✅ Consistent API response format
✅ Dashboard with statistics
✅ Reports (movements, processing)
✅ Settings management
✅ Swagger/OpenAPI documentation
✅ Docker Compose for PostgreSQL
✅ Flyway migrations
✅ Integration and unit tests
✅ Production-ready configuration

## API Endpoints Summary

### Authentication (5 endpoints)
- POST /api/auth/login
- POST /api/auth/refresh
- POST /api/auth/logout
- GET /api/auth/me
- POST /api/auth/register

### Users (6 endpoints - Admin only)
- GET /api/users
- GET /api/users/{id}
- POST /api/users
- PUT /api/users/{id}
- DELETE /api/users/{id}
- POST /api/users/{id}/reset-password

### Warehouses (5 endpoints)
- GET /api/warehouses
- GET /api/warehouses/{id}
- POST /api/warehouses
- PUT /api/warehouses/{id}
- DELETE /api/warehouses/{id}

### Stock (7 endpoints)
- GET /api/stocks
- GET /api/stocks/summary
- POST /api/stocks/inbound
- POST /api/stocks/outbound
- POST /api/stocks/transfer
- POST /api/stocks/adjust
- POST /api/stocks/process

### Suppliers (5 endpoints)
- GET /api/suppliers
- GET /api/suppliers/{id}
- POST /api/suppliers
- PUT /api/suppliers/{id}
- DELETE /api/suppliers/{id}

### Customers (5 endpoints)
- GET /api/customers
- GET /api/customers/{id}
- POST /api/customers
- PUT /api/customers/{id}
- DELETE /api/customers/{id}

### Reports (2 endpoints)
- GET /api/reports/movements
- GET /api/reports/processing

### Dashboard (1 endpoint)
- GET /api/dashboard

### Settings (2 endpoints)
- GET /api/settings
- PUT /api/settings

**Total: 43 REST endpoints**

## Running the Application

1. Start PostgreSQL: `docker-compose up -d`
2. Build: `./mvnw clean install`
3. Run: `./mvnw spring-boot:run`
4. Access: http://localhost:8080/api/swagger-ui.html

## Default Credentials

Username: admin
Password: admin123

Change these immediately in production!
