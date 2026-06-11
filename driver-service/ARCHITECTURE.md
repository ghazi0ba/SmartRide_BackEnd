# SmartRide Driver Service - Architecture & Structure

## 📦 Project Structure

```
driver-service/
│
├── pom.xml                                    # Maven Configuration (Dependencies)
├── mvnw                                       # Maven Wrapper (Unix/Linux)
├── mvnw.cmd                                   # Maven Wrapper (Windows)
│
├── .gitignore                                 # Git Ignore Configuration
│
├── README.md                                  # Project Overview & Documentation
├── INSTALLATION.md                            # Installation & Setup Guide
├── API_EXAMPLES.md                            # API Usage Examples
├── ARCHITECTURE.md                            # This file
│
└── src/
    ├── main/
    │   ├── java/esprit/driver/
    │   │   ├── DriverApplication.java         # 🚀 Main Spring Boot Application
    │   │   │                                  # - Enables Eureka Discovery
    │   │   │                                  # - Enables OpenFeign
    │   │   │
    │   │   ├── entity/
    │   │   │   └── Driver.java               # 📊 JPA Entity
    │   │   │       - id (PK)
    │   │   │       - Personal Info (nom, prenom, email, telephone)
    │   │   │       - Status (DISPONIBLE, OCCUPÉ, HORS_LIGNE)
    │   │   │       - Vehicle Info (marque, modèle, plaque)
    │   │   │       - userId (FK reference)
    │   │   │
    │   │   ├── dto/
    │   │   │   └── DriverDTO.java            # 📦 Data Transfer Object
    │   │   │       - Mapping: Entity ↔ DTO
    │   │   │       - API Response/Request serialization
    │   │   │
    │   │   ├── repository/
    │   │   │   └── DriverRepository.java     # 💾 Data Access Layer
    │   │   │       - CRUD Operations (JpaRepository)
    │   │   │       - Custom Queries:
    │   │   │         * findByEmail()
    │   │   │         * findByPlaqueImmatriculation()
    │   │   │         * findByStatut()
    │   │   │         * findByUserId()
    │   │   │
    │   │   ├── service/
    │   │   │   └── DriverService.java        # 🧠 Business Logic Layer
    │   │   │       - CRUD Operations
    │   │   │       - Search Operations
    │   │   │       - Status Management
    │   │   │       - Error Handling & Validation
    │   │   │
    │   │   ├── controller/
    │   │   │   └── DriverRestAPI.java        # 🌐 REST API Endpoints
    │   │   │       - POST   /api/drivers              (Create)
    │   │   │       - GET    /api/drivers              (Get All)
    │   │   │       - GET    /api/drivers/{id}         (Get One)
    │   │   │       - GET    /api/drivers/email/{e}    (Search)
    │   │   │       - GET    /api/drivers/plaque/{p}   (Search)
    │   │   │       - GET    /api/drivers/user/{uid}   (Search)
    │   │   │       - GET    /api/drivers/status/{s}   (Filter)
    │   │   │       - PUT    /api/drivers/{id}         (Update)
    │   │   │       - PATCH  /api/drivers/{id}/status  (Status Only)
    │   │   │       - DELETE /api/drivers/{id}         (Delete)
    │   │   │       - GET    /api/drivers/{id}/reservations (OpenFeign)
    │   │   │
    │   │   ├── client/
    │   │   │   └── ReservationClient.java    # 🔗 OpenFeign HTTP Client
    │   │   │       - Service-to-Service Communication
    │   │   │       - Calls: reservation-service
    │   │   │       - Method: getReservationsByDriverId()
    │   │   │
    │   │   ├── config/
    │   │   │   └── EurekaConfig.java         # ⚙️ Eureka Configuration
    │   │   │       - Service Discovery Setup
    │   │   │       - Auto-registration
    │   │   │
    │   │   ├── exception/
    │   │   │   ├── DriverNotFoundException.java
    │   │   │   └── GlobalExceptionHandler.java # 🚨 Error Management
    │   │   │       - Centralized Exception Handling
    │   │   │       - Custom Error Responses
    │   │   │
    │   │   └── [Other packages can be added here]
    │   │
    │   └── resources/
    │       ├── application.properties        # Properties Configuration
    │       └── application.yml               # YAML Configuration (Alternative)
    │           - Database Connection
    │           - Eureka Client Setup
    │           - Server Port & Context Path
    │           - Logging Configuration
    │
    └── test/
        └── java/esprit/driver/
            └── DriverApplicationTests.java   # Unit Test Class


## 🏗️ Layered Architecture

```
┌────────────────────────────────────────────┐
│           PRESENTATION LAYER               │
│    DriverRestAPI (REST Controllers)        │
│  HTTP Requests → JSON Responses            │
└────────────────────────────────────────────┘
                    ↓
┌────────────────────────────────────────────┐
│        BUSINESS LOGIC LAYER                │
│        DriverService (Services)            │
│  Business Rules, Validation, Logic         │
└────────────────────────────────────────────┘
                    ↓
┌────────────────────────────────────────────┐
│      PERSISTENCE LAYER                     │
│  DriverRepository (Data Access)            │
│  JPA Queries, Database Operations          │
└────────────────────────────────────────────┘
                    ↓
┌────────────────────────────────────────────┐
│           DATABASE LAYER                   │
│      MySQL Database (smartride_driver)     │
│      Table: drivers                        │
└────────────────────────────────────────────┘
```


## 🔄 Request/Response Flow

```
1. CLIENT REQUEST
        ↓
2. API GATEWAY (Optional routing)
        ↓
3. DriverRestAPI (Controller)
   - Route matching
   - Parameter extraction
        ↓
4. DriverService (Business Logic)
   - Validation
   - Data transformation
   - Business rules
        ↓
5. DriverRepository (Database)
   - SQL Query generation
   - Transaction management
        ↓
6. MySQL Database
   - CRUD operations
   - Data persistence
        ↓
7. Response Building
   - DTO conversion
   - JSON serialization
        ↓
8. CLIENT RESPONSE (JSON)
```


## 🔌 Integration Points

### Eureka Client (Service Discovery)
```
driver-service
     ↓
Eureka Server (8761)
     ↑
Other Services (locate driver-service)
```

### OpenFeign (Inter-Service Communication)
```
driver-service
     ↓
ReservationClient (Interface)
     ↓
Reservation Service (8086)
     ↓
Reservations Data
```

### API Gateway (External Access)
```
External Clients
     ↓
API Gateway (8080)
     ↓
driver-service (8085)
```


## 📊 Entity Relationship

```
┌─────────────────────────────────────┐
│            DRIVER                   │
├─────────────────────────────────────┤
│ PK  id                              │
│ ─── nom                             │
│ ─── prenom                          │
│ ─── email (UNIQUE)                  │
│ ─── telephone                       │
│ ─── statut (ENUM)                   │
│ ─── marqueVehicule                  │
│ ─── modeleVehicule                  │
│ ─── plaqueImmatriculation (UNIQUE)  │
│ FK  user_id (User Service ref)      │
└─────────────────────────────────────┘
        ↑
        │ (Relation)
        │
USER SERVICE           RESERVATION SERVICE
┌──────────────┐      ┌──────────────────┐
│ User         │      │ Reservation      │
│ id           │      │ id               │
│ name         │      │ driverId (FK)    │
└──────────────┘      └──────────────────┘
```


## 📈 Scalability & Performance

### Database Optimization
- Connection Pooling (HikariCP)
- Connection Pool Size: 10
- Index on: email, plaqueImmatriculation, statut, userId

### Caching Strategy (Future)
- Cache driver lookups by ID
- Cache drivers by status
- TTL: 5-10 minutes

### Load Balancing (via Eureka)
```
API Gateway
    ├─→ driver-service instance 1
    ├─→ driver-service instance 2
    └─→ driver-service instance 3
```


## 🔐 Security Considerations

### Current Implementation
- CORS enabled (Access-Control-Allow-Origin: *)
- No authentication/authorization yet

### Future Enhancements
- OAuth 2.0 / JWT Authentication
- Role-based Access Control (RBAC)
- Request validation (Bean Validation)
- API Rate Limiting
- HTTPS/TLS encryption


## 🧪 Testing Strategy

### Unit Tests (JUnit 5)
```java
- Test DriverService methods
- Test DriverRestAPI endpoints
- Mock DriverRepository
```

### Integration Tests
```java
- Test with real MySQL database
- Test Eureka registration
- Test OpenFeign calls (mocked)
```

### End-to-End Tests
```bash
- Test complete API flows
- Test service discovery
- Test inter-service communication
```


## 📋 API Versioning (Future)

```
Current: /api/drivers
Versioned: /api/v1/drivers
            /api/v2/drivers
```


## 🚀 Deployment Targets

### Development
```
localhost:8085
MySQL: localhost:3306
Eureka: localhost:8761
```

### Staging/Production
```
Docker Container
Kubernetes Pod
AWS ECS/EKS
Azure Container Instances
```


## 📦 Dependencies Overview

```
Spring Boot (Web, Data JPA, Actuator)
    ↓
Spring Cloud (Eureka, OpenFeign)
    ↓
MySQL Connector (Database Driver)
    ↓
Lombok (Boilerplate Reduction)
    ↓
JUnit 5 (Testing)
```


## 🔧 Configuration Files

### application.properties
- Database credentials
- Eureka settings
- Server configuration
- Logging setup

### application.yml (Alternative)
- Same configuration in YAML format
- Better for complex configurations

### pom.xml
- Maven build configuration
- Dependency management
- Plugin configuration
- Build profiles


## 📚 Key Design Patterns

### 1. MVC Pattern
- Model: Driver entity & DTO
- View: JSON responses
- Controller: DriverRestAPI

### 2. DAO Pattern
- DriverRepository abstracts database access
- JpaRepository provides CRUD
- Custom query methods

### 3. Service Layer Pattern
- DriverService encapsulates business logic
- Separates concerns
- Facilitates testing

### 4. Feign Client Pattern
- ReservationClient for service-to-service communication
- Declarative HTTP client
- Automatic serialization

### 5. Exception Handling Pattern
- GlobalExceptionHandler centralizes error handling
- Consistent error responses
- Custom exceptions


## 🎯 Performance Benchmarks (Expected)

- Create Driver: ~50ms
- Get All Drivers (100 records): ~200ms
- Get Driver by ID: ~20ms
- Search by Email/Plaque: ~30ms
- Update Status: ~40ms
- Delete Driver: ~30ms
- Get Reservations via OpenFeign: ~500ms (depends on Reservation Service)


## 📞 Support & Troubleshooting

See INSTALLATION.md for common issues and solutions.


---

**Last Updated**: May 2024
**Version**: 1.0.0
**Architecture**: Microservices (Eureka + OpenFeign)
**Technology Stack**: Spring Boot 3, Spring Cloud, MySQL, JPA
