# SmartRide Reservation Service

Service de réservation pour la plateforme SmartRide - Microservice de gestion des réservations de trajets.

## Architecture

Ce microservice est basé sur **Spring Boot** avec les technologies suivantes:
- **Spring Cloud** - Intégration Eureka et OpenFeign
- **MongoDB** - Base de données NoSQL
- **Spring Web** - API REST
- **Spring Validation** - Validation des données
- **Lombok** - Réduction du boilerplate

## Configuration

### Port
- **Port défaut**: 8084
- **Context Path**: /smartride_reservation

### Eureka
- **Service URL**: http://localhost:8761/eureka/

### MongoDB
- **URI**: mongodb://localhost:27017/smartride_reservation

## Structure du projet

```
src/main/java/com/example/smartridereservationservice/
├── controller/          # Controllers REST
├── service/             # Logique métier
├── model/               # Modèles MongoDB
├── dto/                 # Data Transfer Objects
├── repository/          # Accès aux données
├── client/              # Clients Feign
├── exception/           # Gestion des exceptions
└── SmartRideReservationServiceApplication.java  # Main
```

## API Endpoints

### Réservations

#### Créer une réservation
```http
POST /smartride_reservation/api/reservations
Content-Type: application/json

{
  "userId": 1,
  "trajetId": 1,
  "nombrePassagers": 2
}
```

#### Récupérer une réservation
```http
GET /smartride_reservation/api/reservations/{reservationId}
```

#### Récupérer les réservations d'un utilisateur
```http
GET /smartride_reservation/api/reservations/user/{userId}
```

#### Récupérer les réservations d'un trajet
```http
GET /smartride_reservation/api/reservations/trajet/{trajetId}
```

#### Récupérer les réservations par statut
```http
GET /smartride_reservation/api/reservations/status/{status}
```

#### Confirmer une réservation
```http
PATCH /smartride_reservation/api/reservations/{reservationId}/confirmer
```

#### Compléter une réservation
```http
PATCH /smartride_reservation/api/reservations/{reservationId}/completer
```

#### Annuler une réservation
```http
PATCH /smartride_reservation/api/reservations/{reservationId}/annuler
```

#### Récupérer l'historique d'un utilisateur
```http
GET /smartride_reservation/api/reservations/historique/{userId}
```

## Modèles de données

### Reservation
```json
{
  "reservationId": "ObjectId",
  "userId": 1,
  "driverId": 2,
  "trajetId": 1,
  "dateReservation": "2024-01-15T10:30:00",
  "nombrePassagers": 2,
  "prixTotal": 50.00,
  "status": "PENDING",
  "dateCreation": "2024-01-15T10:30:00",
  "dateModification": "2024-01-15T10:30:00",
  "delaiAnnulationLimite": "2024-01-15T11:00:00"
}
```

### ReservationHistory
```json
{
  "historyId": "ObjectId",
  "userId": 1,
  "reservationId": "ObjectId",
  "trajetId": 1,
  "status": "COMPLETED",
  "dateReservation": "2024-01-15T10:30:00",
  "dateCompletion": "2024-01-15T12:00:00",
  "prixTotal": 50.00,
  "dateCreation": "2024-01-15T12:00:00"
}
```

## Statuts de réservation

- **PENDING**: Réservation en attente de confirmation
- **CONFIRMED**: Réservation confirmée
- **COMPLETED**: Réservation complétée
- **CANCELLED**: Réservation annulée

## Validations métier

1. **Vérification de l'utilisateur**: L'utilisateur doit exister (appel à UserMS)
2. **Vérification du trajet**: Le trajet doit exister et être disponible (appel à TrajetMS)
3. **Nombre de passagers**: Le nombre de passagers doit être valide et ≤ places disponibles
4. **Double réservation**: Empêcher les double réservations pour le même utilisateur et trajet
5. **Délai d'annulation**: Délai de 30 minutes avant la limite d'annulation

## Communication inter-services (OpenFeign)

### UserClient
Communique avec le service **smartride-user-service** (port 8083)
- `getUserById(userId)`: Récupère les infos utilisateur

### TrajetClient
Communique avec le service **trajet-s** (port 8082)
- `getTrajetById(trajetId)`: Récupère les infos du trajet

## Gestion des exceptions

Le service inclut un gestionnaire global d'exceptions (`GlobalExceptionHandler`) qui gère:
- ReservationNotFoundException
- UserNotFoundException
- TrajetNotFoundException
- InvalidReservationException
- Exceptions générales

## Démarrage du service

### Prérequis
- Java 17+
- Maven 3.9+
- MongoDB en cours d'exécution
- Eureka Server en cours d'exécution

### Commandes

```bash
# Compiler le projet
./mvnw clean compile

# Exécuter les tests
./mvnw test

# Construire le JAR
./mvnw clean package

# Exécuter le service
./mvnw spring-boot:run
```

## Logs

Les logs sont configurés au niveau DEBUG pour le package du service:
```properties
logging.level.com.example.smartridereservationservice=DEBUG
```

## Contribuer

- Respectez les conventions de nommage existantes
- Ajoutez des tests pour les nouvelles fonctionnalités
- Documentez vos changements dans ce README

---

**Auteur**: SmartRide Team  
**Date**: 2024
