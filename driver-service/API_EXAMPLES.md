# API Examples - Driver Service

## 1. CRUD Operations

### 1.1 Create Driver (POST)
```bash
curl -X POST http://localhost:8085/api/drivers \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Benali",
    "prenom": "Mohamed",
    "email": "m.benali@smartride.com",
    "telephone": "+216 91 234 567",
    "statut": "DISPONIBLE",
    "marqueVehicule": "Toyota",
    "modeleVehicule": "Corolla 2023",
    "plaqueImmatriculation": "TN 123 ABC",
    "userId": 1
  }'
```

Response (201 Created):
```json
{
  "id": 1,
  "nom": "Benali",
  "prenom": "Mohamed",
  "email": "m.benali@smartride.com",
  "telephone": "+216 91 234 567",
  "statut": "DISPONIBLE",
  "marqueVehicule": "Toyota",
  "modeleVehicule": "Corolla 2023",
  "plaqueImmatriculation": "TN 123 ABC",
  "userId": 1
}
```

### 1.2 Get All Drivers (GET)
```bash
curl -X GET http://localhost:8085/api/drivers \
  -H "Accept: application/json"
```

Response (200 OK):
```json
[
  {
    "id": 1,
    "nom": "Benali",
    "prenom": "Mohamed",
    "email": "m.benali@smartride.com",
    "telephone": "+216 91 234 567",
    "statut": "DISPONIBLE",
    "marqueVehicule": "Toyota",
    "modeleVehicule": "Corolla 2023",
    "plaqueImmatriculation": "TN 123 ABC",
    "userId": 1
  },
  {
    "id": 2,
    "nom": "Amara",
    "prenom": "Fatima",
    "email": "f.amara@smartride.com",
    "telephone": "+216 92 345 678",
    "statut": "OCCUPÉ",
    "marqueVehicule": "BMW",
    "modeleVehicule": "320i 2022",
    "plaqueImmatriculation": "TN 456 XYZ",
    "userId": 2
  }
]
```

### 1.3 Get Driver by ID (GET)
```bash
curl -X GET http://localhost:8085/api/drivers/1 \
  -H "Accept: application/json"
```

Response (200 OK):
```json
{
  "id": 1,
  "nom": "Benali",
  "prenom": "Mohamed",
  "email": "m.benali@smartride.com",
  "telephone": "+216 91 234 567",
  "statut": "DISPONIBLE",
  "marqueVehicule": "Toyota",
  "modeleVehicule": "Corolla 2023",
  "plaqueImmatriculation": "TN 123 ABC",
  "userId": 1
}
```

### 1.4 Update Driver (PUT)
```bash
curl -X PUT http://localhost:8085/api/drivers/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Benali",
    "prenom": "Mohamed Updated",
    "email": "m.benali.updated@smartride.com",
    "telephone": "+216 93 234 567",
    "statut": "DISPONIBLE",
    "marqueVehicule": "Toyota",
    "modeleVehicule": "Camry 2023",
    "plaqueImmatriculation": "TN 123 ABC",
    "userId": 1
  }'
```

Response (200 OK): Updated driver object

### 1.5 Delete Driver (DELETE)
```bash
curl -X DELETE http://localhost:8085/api/drivers/1
```

Response (204 No Content): Empty response


## 2. Search Operations

### 2.1 Get Driver by Email (GET)
```bash
curl -X GET "http://localhost:8085/api/drivers/email/m.benali@smartride.com" \
  -H "Accept: application/json"
```

### 2.2 Get Driver by License Plate (GET)
```bash
curl -X GET "http://localhost:8085/api/drivers/plaque/TN%20123%20ABC" \
  -H "Accept: application/json"
```

### 2.3 Get Driver by User ID (GET)
```bash
curl -X GET http://localhost:8085/api/drivers/user/1 \
  -H "Accept: application/json"
```

### 2.4 Get Drivers by Status (GET)
```bash
curl -X GET http://localhost:8085/api/drivers/status/DISPONIBLE \
  -H "Accept: application/json"
```

Response (200 OK):
```json
[
  {
    "id": 1,
    "nom": "Benali",
    "prenom": "Mohamed",
    "email": "m.benali@smartride.com",
    "telephone": "+216 91 234 567",
    "statut": "DISPONIBLE",
    "marqueVehicule": "Toyota",
    "modeleVehicule": "Corolla 2023",
    "plaqueImmatriculation": "TN 123 ABC",
    "userId": 1
  }
]
```


## 3. Status Management

### 3.1 Update Driver Status (PATCH)
```bash
curl -X PATCH "http://localhost:8085/api/drivers/1/status?statut=OCCUPÉ"
```

Response (200 OK):
```json
{
  "id": 1,
  "nom": "Benali",
  "prenom": "Mohamed",
  "email": "m.benali@smartride.com",
  "telephone": "+216 91 234 567",
  "statut": "OCCUPÉ",
  "marqueVehicule": "Toyota",
  "modeleVehicule": "Corolla 2023",
  "plaqueImmatriculation": "TN 123 ABC",
  "userId": 1
}
```

### 3.2 Change Status to HORS_LIGNE
```bash
curl -X PATCH "http://localhost:8085/api/drivers/1/status?statut=HORS_LIGNE"
```

### 3.3 Change Status back to DISPONIBLE
```bash
curl -X PATCH "http://localhost:8085/api/drivers/1/status?statut=DISPONIBLE"
```


## 4. OpenFeign Integration - Get Reservations

### 4.1 Get Driver Reservations
```bash
curl -X GET http://localhost:8085/api/drivers/1/reservations \
  -H "Accept: application/json"
```

Response (200 OK) - From Reservation Service:
```json
[
  {
    "id": 101,
    "driverId": 1,
    "passengerId": 10,
    "reservationDate": "2024-05-04T10:30:00",
    "startPoint": "Tunis Center",
    "endPoint": "Airport",
    "status": "COMPLETED"
  },
  {
    "id": 102,
    "driverId": 1,
    "passengerId": 11,
    "reservationDate": "2024-05-04T14:00:00",
    "startPoint": "La Marsa",
    "endPoint": "Tunis",
    "status": "COMPLETED"
  }
]
```

Response (503 Service Unavailable) - If Reservation Service is down:
```json
{
  "timestamp": "2024-05-04T15:30:00.123456",
  "status": 503,
  "error": "Service Unavailable"
}
```


## 5. Error Handling Examples

### 5.1 Driver Not Found (GET)
```bash
curl -X GET http://localhost:8085/api/drivers/999
```

Response (404 Not Found):
```json
{
  "timestamp": "2024-05-04T15:30:00.123456",
  "status": 404,
  "error": "Driver Not Found",
  "message": "Driver not found with id: 999"
}
```

### 5.2 Invalid Email (POST)
```bash
curl -X POST http://localhost:8085/api/drivers \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Test",
    "prenom": "Driver",
    "email": "m.benali@smartride.com",
    "telephone": "+216 91 234 567",
    "statut": "DISPONIBLE",
    "marqueVehicule": "Toyota",
    "modeleVehicule": "Corolla 2023",
    "plaqueImmatriculation": "TN 999 DEF",
    "userId": 1
  }'
```

Response (500 Internal Server Error):
```json
{
  "timestamp": "2024-05-04T15:30:00.123456",
  "status": 500,
  "error": "Internal Server Error",
  "message": "could not execute statement [Duplicate entry 'm.benali@smartride.com' for key 'drivers.email']"
}
```


## 6. HTTP Status Codes

| Code | Meaning | Example |
|------|---------|---------|
| 200 | OK | GET, PUT success |
| 201 | Created | POST success |
| 204 | No Content | DELETE success |
| 400 | Bad Request | Invalid input data |
| 404 | Not Found | Driver doesn't exist |
| 500 | Server Error | Database error |
| 503 | Service Unavailable | External service down |


## 7. Testing with Postman

### Setup Postman Environment Variables
```json
{
  "DRIVER_SERVICE_URL": "http://localhost:8085",
  "DRIVER_ID": "1",
  "DRIVER_EMAIL": "m.benali@smartride.com"
}
```

### Postman Collection Sample

#### Create Driver
- URL: `{{DRIVER_SERVICE_URL}}/api/drivers`
- Method: POST
- Body: JSON (raw)

#### Get All Drivers
- URL: `{{DRIVER_SERVICE_URL}}/api/drivers`
- Method: GET

#### Get Driver by ID
- URL: `{{DRIVER_SERVICE_URL}}/api/drivers/{{DRIVER_ID}}`
- Method: GET

#### Update Status
- URL: `{{DRIVER_SERVICE_URL}}/api/drivers/{{DRIVER_ID}}/status?statut=OCCUPÉ`
- Method: PATCH

#### Get Reservations
- URL: `{{DRIVER_SERVICE_URL}}/api/drivers/{{DRIVER_ID}}/reservations`
- Method: GET


## 8. Batch Operations (Example Script)

```bash
#!/bin/bash

BASE_URL="http://localhost:8085/api/drivers"

# Create multiple drivers
echo "Creating drivers..."
curl -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{"nom":"Driver1","prenom":"Test1","email":"d1@test.com","telephone":"5551111","statut":"DISPONIBLE","marqueVehicule":"Toyota","modeleVehicule":"Corolla","plaqueImmatriculation":"TN001","userId":1}'

curl -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{"nom":"Driver2","prenom":"Test2","email":"d2@test.com","telephone":"5552222","statut":"DISPONIBLE","marqueVehicule":"BMW","modeleVehicule":"320i","plaqueImmatriculation":"TN002","userId":2}'

# Get all drivers
echo -e "\n\nGetting all drivers..."
curl -X GET $BASE_URL

# Update status for driver 1
echo -e "\n\nUpdating driver 1 status..."
curl -X PATCH "$BASE_URL/1/status?statut=OCCUPÉ"
```


## 9. Rate Limiting & Performance Considerations

- Max request body size: 2MB (default)
- Connection timeout: 20000ms
- Database pool size: 10 connections
- Recommended QPS: < 1000 requests/second


## 10. Additional Resources

- Swagger API Documentation: http://localhost:8085/swagger-ui.html (if Springdoc-OpenAPI is added)
- Health Check: http://localhost:8085/actuator/health
- Metrics: http://localhost:8085/actuator/metrics
