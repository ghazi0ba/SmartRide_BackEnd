# Driver Service - SmartRide Microservice

Microservice complet pour la gestion des chauffeurs dans la plateforme SmartRide.

## Structure du Projet

```
driver-service/
├── src/
│   ├── main/
│   │   ├── java/esprit/driver/
│   │   │   ├── DriverApplication.java          # Main Spring Boot Application
│   │   │   ├── controller/
│   │   │   │   └── DriverRestAPI.java          # REST Controllers
│   │   │   ├── service/
│   │   │   │   └── DriverService.java          # Business Logic
│   │   │   ├── repository/
│   │   │   │   └── DriverRepository.java       # Database Access Layer
│   │   │   ├── entity/
│   │   │   │   └── Driver.java                 # JPA Entity
│   │   │   ├── dto/
│   │   │   │   └── DriverDTO.java              # Data Transfer Object
│   │   │   └── client/
│   │   │       └── ReservationClient.java      # OpenFeign Client
│   │   └── resources/
│   │       └── application.properties          # Configuration
│   └── test/
│       └── java/esprit/driver/
│           └── DriverApplicationTests.java
├── pom.xml                                     # Maven Configuration
├── mvnw                                        # Maven Wrapper (Unix)
└── mvnw.cmd                                    # Maven Wrapper (Windows)
```

## Technologie Stack

- **Spring Boot 3.2.4** - Framework principal
- **Spring Cloud 2023.0.0** - Microservices
- **Spring Data JPA** - ORM
- **MySQL 8.0** - Base de données
- **Eureka Client** - Service Discovery
- **OpenFeign** - Communication inter-services
- **Lombok** - Reduce boilerplate code
- **Java 17** - Language version

## Configuration

### application.properties

```properties
spring.application.name=driver-service
server.port=8085

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/smartride_driver?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=

# Eureka
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
```

## Entité Driver

```java
- id (Long) - Primary Key, Auto-generated
- nom (String) - Driver's last name
- prenom (String) - Driver's first name
- email (String) - Unique email
- telephone (String) - Phone number
- statut (Enum) - DISPONIBLE, OCCUPÉ, HORS_LIGNE
- marqueVehicule (String) - Vehicle brand
- modeleVehicule (String) - Vehicle model
- plaqueImmatriculation (String) - License plate (Unique)
- userId (Long) - Link to User Service
```

## API Endpoints

### CRUD Opérations

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/drivers` | Créer un chauffeur |
| GET | `/api/drivers` | Récupérer tous les chauffeurs |
| GET | `/api/drivers/{id}` | Récupérer un chauffeur par ID |
| PUT | `/api/drivers/{id}` | Mettre à jour un chauffeur |
| DELETE | `/api/drivers/{id}` | Supprimer un chauffeur |

### Recherche Spécifiques

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/drivers/email/{email}` | Trouver par email |
| GET | `/api/drivers/plaque/{plaque}` | Trouver par plaque d'immatriculation |
| GET | `/api/drivers/user/{userId}` | Trouver par user ID |
| GET | `/api/drivers/status/{statut}` | Trouver par statut |

### Gestion du Statut

| Method | Endpoint | Description |
|--------|----------|-------------|
| PATCH | `/api/drivers/{id}/status?statut=DISPONIBLE` | Changer le statut |

### Intégration avec Reservation Service (OpenFeign)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/drivers/{driverId}/reservations` | Récupérer les réservations d'un chauffeur |

## Services et Composants

### DriverService
- CRUD complet du chauffeur
- Méthodes de recherche (email, plaque, user_id, statut)
- Gestion du statut
- Gestion d'erreurs

### DriverRestAPI (Controller)
- Tous les endpoints REST
- Gestion des requêtes HTTP
- Réponses avec status codes appropriés
- Intégration avec ReservationClient

### ReservationClient (OpenFeign)
- Appel au service de réservations
- Récupération des réservations d'un chauffeur
- Gestion des erreurs de communication

### DriverRepository
- Accès à la base de données
- Requêtes personnalisées
- Méthodes de recherche

## Installation et Démarrage

### Prérequis
- Java 17+
- MySQL 8.0+
- Maven 3.6+

### Étapes

1. **Naviguer au répertoire du projet**
   ```bash
   cd driver-service
   ```

2. **Compiler le projet**
   ```bash
   ./mvnw clean compile
   ```

3. **Construire le JAR**
   ```bash
   ./mvnw clean package -DskipTests
   ```

4. **Exécuter l'application**
   ```bash
   ./mvnw spring-boot:run
   ```

   Ou directement avec Java :
   ```bash
   java -jar target/driver-service-0.0.1-SNAPSHOT.jar
   ```

## Intégration avec API Gateway

Ajouter la configuration pour router vers driver-service dans API Gateway :

```properties
spring.cloud.gateway.routes[x].id=driver-service
spring.cloud.gateway.routes[x].uri=lb://driver-service
spring.cloud.gateway.routes[x].predicates[0]=Path=/api/drivers/**
```

## Architecture d'intégration

```
┌─────────────────┐
│   API Gateway   │
└────────┬────────┘
         │
         ├─→ Driver Service (8085)
         │   └─→ MySQL Database
         │   └─→ Eureka Client
         │
         ├─→ Reservation Service (8086)
         │   └─→ (Communication via OpenFeign)
         │
         └─→ Other Services
             └─→ User Service
             └─→ Job Service
             └─→ Trajet Service

┌──────────────────┐
│ Eureka Server    │
│ (8761)           │
└──────────────────┘
```

## Exemple d'utilisation

### Créer un chauffeur
```bash
POST /api/drivers
Content-Type: application/json

{
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

### Changer le statut
```bash
PATCH /api/drivers/1/status?statut=OCCUPÉ
```

### Récupérer les réservations d'un chauffeur
```bash
GET /api/drivers/1/reservations
```

## Notes Importantes

- Le service se découvre automatiquement via Eureka
- Les erreurs de communication avec Reservation Service sont gérées gracieusement
- Les status valides : DISPONIBLE, OCCUPÉ, HORS_LIGNE
- Email et plaque d'immatriculation doivent être uniques
- userId permet de lier avec le service User

## Prochaines étapes

1. Intégrer avec le service User pour validation des user_id
2. Ajouter des validations de données avec Bean Validation
3. Implémenter la gestion des erreurs avec Exception Handler
4. Ajouter des logs structurés
5. Implémenter les tests unitaires et d'intégration
6. Ajouter Swagger/OpenAPI pour la documentation API
7. Configurer les certificats SSL/TLS
