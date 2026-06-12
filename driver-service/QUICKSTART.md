# Getting Started with Driver Service

## Quick Start Guide (5 minutes)

### Step 1: Prerequisites
```bash
✓ Java 17 installed
✓ MySQL 8.0 installed and running
✓ Maven 3.6 installed
✓ Port 8085 available
```

### Step 2: Database Setup
```bash
# Option A: Using SQL Script
mysql -u root -p < driver-service/DATABASE_SETUP.sql

# Option B: Manual Setup
mysql -u root -p
> CREATE DATABASE smartride_driver CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
> exit
```

### Step 3: Configure Database Credentials
Edit `driver-service/src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=your_password_here
```

### Step 4: Build the Project
```bash
cd driver-service
./mvnw clean package -DskipTests
```

### Step 5: Run the Application
```bash
./mvnw spring-boot:run
```

Expected output:
```
Tomcat started on port(s): 8085 (http)
Application 'driver-service' is running
```

### Step 6: Verify Installation
```bash
# Test endpoint
curl -X GET http://localhost:8085/api/drivers

# Expected response: []
# Or: [list of sample drivers]
```

## Common Issues & Solutions

### Issue 1: "Connection refused" on port 3306
**Solution**: Start MySQL
```bash
# Windows
net start MySQL80

# Mac
brew services start mysql

# Linux
sudo systemctl start mysql
```

### Issue 2: "Access denied for user 'root'"
**Solution**: Update credentials in application.properties or reset MySQL password

### Issue 3: Port 8085 already in use
**Solution**: Change port in application.properties
```properties
server.port=8086
```

### Issue 4: Service not visible in Eureka
**Solution**: Ensure Eureka Server is running on port 8761
```bash
cd ../EurekaServer
./mvnw spring-boot:run
```

## Project Structure Summary

```
driver-service/
├── pom.xml                  # Build configuration
├── src/main/java/           # Source code
│   └── esprit/driver/
│       ├── DriverApplication.java
│       ├── entity/          # Data models
│       ├── dto/             # Data transfer objects
│       ├── repository/      # Database access
│       ├── service/         # Business logic
│       ├── controller/      # REST endpoints
│       ├── client/          # Feign clients
│       ├── config/          # Configuration
│       └── exception/       # Error handling
├── src/main/resources/      # Configuration files
└── README.md                # Full documentation
```

## Key Features

✅ **Complete CRUD Operations** - Create, read, update, delete drivers
✅ **Service Discovery** - Automatic registration with Eureka
✅ **Inter-Service Communication** - Call other services via OpenFeign
✅ **Database Integration** - MySQL with Hibernate/JPA
✅ **REST API** - 10+ endpoints for driver management
✅ **Status Management** - Track driver availability
✅ **Error Handling** - Centralized exception handling
✅ **Logging** - Structured logging configuration

## API Endpoints Overview

| Method | Path | Purpose |
|--------|------|---------|
| POST | /api/drivers | Create driver |
| GET | /api/drivers | Get all drivers |
| GET | /api/drivers/{id} | Get driver by ID |
| PUT | /api/drivers/{id} | Update driver |
| DELETE | /api/drivers/{id} | Delete driver |
| PATCH | /api/drivers/{id}/status | Update status |
| GET | /api/drivers/{id}/reservations | Get reservations |

Full API documentation: See [API_EXAMPLES.md](API_EXAMPLES.md)

## Testing the API

### Create a Driver
```bash
curl -X POST http://localhost:8085/api/drivers \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Test",
    "prenom": "Driver",
    "email": "test@smartride.com",
    "telephone": "5551234567",
    "statut": "DISPONIBLE",
    "marqueVehicule": "Toyota",
    "modeleVehicule": "Camry",
    "plaqueImmatriculation": "TN 999 ABC",
    "userId": 1
  }'
```

### Get All Drivers
```bash
curl -X GET http://localhost:8085/api/drivers
```

### Update Status
```bash
curl -X PATCH "http://localhost:8085/api/drivers/1/status?statut=OCCUPÉ"
```

See [API_EXAMPLES.md](API_EXAMPLES.md) for more examples.

## Docker Deployment (Optional)

Create `Dockerfile`:
```dockerfile
FROM openjdk:17-slim
WORKDIR /app
COPY target/driver-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8085
```

Build and run:
```bash
docker build -t driver-service:1.0 .
docker run -p 8085:8085 --name driver-service driver-service:1.0
```

## Environment Variables

Set before running:
```bash
# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-17
set MAVEN_HOME=C:\Program Files\Maven

# Mac/Linux
export JAVA_HOME=/usr/libexec/java_home -v 17
export MAVEN_HOME=/usr/local/maven
```

## Monitoring & Logs

View logs in real-time:
```bash
tail -f target/driver-service.log
```

Check application health:
```bash
curl -X GET http://localhost:8085/actuator/health
```

## IDE Integration

### VS Code
- Install "Spring Boot Extension Pack"
- Open project folder
- Run from debugger or terminal

### IntelliJ IDEA
- File → Open → Select driver-service folder
- Right-click pom.xml → Add as Maven Project
- Run → Run 'DriverApplication'

### Eclipse
- File → Import → Existing Maven Projects
- Select driver-service folder
- Run As → Spring Boot App

## Next Steps

1. **Add Authentication**: Implement JWT/OAuth2
2. **Add Validation**: Use Bean Validation annotations
3. **Add Logging**: Use SLF4J + Logback
4. **Add Tests**: Unit and integration tests
5. **Add Documentation**: Swagger/OpenAPI
6. **Configure CI/CD**: GitHub Actions/Jenkins
7. **Set up Monitoring**: Prometheus + Grafana

## Useful Commands

```bash
# Build project
./mvnw clean package -DskipTests

# Run tests
./mvnw test

# Generate project report
./mvnw site

# Check dependencies
./mvnw dependency:tree

# Update dependencies
./mvnw versions:display-dependency-updates

# Format code
./mvnw spotless:apply
```

## Documentation Links

- [README.md](README.md) - Full project documentation
- [INSTALLATION.md](INSTALLATION.md) - Detailed installation guide
- [API_EXAMPLES.md](API_EXAMPLES.md) - API usage examples
- [ARCHITECTURE.md](ARCHITECTURE.md) - System architecture
- [GATEWAY_INTEGRATION.md](GATEWAY_INTEGRATION.md) - API Gateway setup

## Support

For issues or questions:
1. Check [INSTALLATION.md](INSTALLATION.md) troubleshooting section
2. Review log files in target directory
3. Check application health endpoint
4. Verify all services are running in Eureka dashboard

---

**Version**: 1.0.0
**Last Updated**: May 2024
**Status**: Ready for Development
