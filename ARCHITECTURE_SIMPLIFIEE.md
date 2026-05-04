# 🚀 SmartRide - Microservices Simplifiés

## ✅ Structure Finale (Sans smartride-trajet-service)

```
SmartRide_BackEnd/
│
├── 🔐 EurekaServer (Port 8761)
│   └─ Service Discovery - Enregistre tous les microservices
│
├── 🌐 ApiGateway (Port 8080)
│   └─ Routeur central pour tous les services
│
├── 📦 JobMS (Port 8084)
│   └─ Gestion des jobs/tâches
│
└── 🚗 driver-service (Port 8085) ⭐ NOUVEAU
    └─ Gestion complète des chauffeurs (REMPLACE smartride-trajet-service)
```

## 🎯 Pourquoi driver-service seulement?

✅ **driver-service** contient:
- CRUD complet des chauffeurs
- Gestion statut (DISPONIBLE, OCCUPÉ, HORS_LIGNE)
- Recherche avancée (email, plaque, user_id)
- Intégration avec reservation-service via OpenFeign
- Service discovery Eureka
- API Gateway ready
- MySQL Database

❌ **smartride-trajet-service** - SUPPRIMÉ (doublon)

---

## 🚀 Démarrer les Services

### 1. **Eureka Server** (8761) - À démarrer EN PREMIER
```bash
cd EurekaServer
./mvnw spring-boot:run
```
Vérifier: http://localhost:8761

### 2. **API Gateway** (8080)
```bash
cd ApiGateway
./mvnw spring-boot:run
```

### 3. **Driver Service** (8085) ⭐
```bash
cd driver-service
./mvnw spring-boot:run
```

### 4. **JobMS** (8084) - Si nécessaire
```bash
cd JobMS
./mvnw spring-boot:run
```

---

## 📍 Endpoints Disponibles

### Via API Gateway (Recommandé)
```
GET     http://localhost:8080/api/drivers
POST    http://localhost:8080/api/drivers
GET     http://localhost:8080/api/drivers/{id}
PUT     http://localhost:8080/api/drivers/{id}
DELETE  http://localhost:8080/api/drivers/{id}
PATCH   http://localhost:8080/api/drivers/{id}/status
GET     http://localhost:8080/api/drivers/{id}/reservations
```

### Direct sur Driver Service (Port 8085)
```
http://localhost:8085/api/drivers
```

---

## 🗑️ Suppression de smartride-trajet-service

Pour nettoyer, vous pouvez supprimer:
```bash
rm -rf smartride-trajet-service/
```

---

## ✨ Configuration API Gateway

Ajouter à `ApiGateway/src/main/resources/application.properties`:

```properties
spring.cloud.gateway.routes[2].id=driver-service
spring.cloud.gateway.routes[2].uri=lb://driver-service
spring.cloud.gateway.routes[2].predicates[0]=Path=/api/drivers/**
```

---

## 🧪 Test Rapide

```bash
# Créer un chauffeur
curl -X POST http://localhost:8080/api/drivers \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Test",
    "prenom": "Driver",
    "email": "test@smartride.com",
    "telephone": "5551234567",
    "statut": "DISPONIBLE",
    "marqueVehicule": "Toyota",
    "modeleVehicule": "Corolla",
    "plaqueImmatriculation": "TN999ABC",
    "userId": 1
  }'

# Récupérer tous les chauffeurs
curl http://localhost:8080/api/drivers
```

---

## 📊 Vue d'ensemble Architecture

```
Clients (Web, Mobile)
    ↓
┌─────────────────┐
│  API Gateway    │
│   (8080)        │
└────────┬────────┘
    ┌───┴────────────────────┐
    │                        │
┌───▼────┐ ┌────────────┐ ┌──▼──┐
│ JobMS  │ │   Driver   │ │ ...  │
│ (8084) │ │ Service    │ │      │
│        │ │  (8085)    │ │      │
└────────┘ └─────┬──────┘ └──────┘
               ┌──┴──┐
           ┌───▼──┐ ┌─────────────┐
           │MySQL │ │Eureka Server│
           │      │ │   (8761)    │
           └──────┘ └─────────────┘
```

---

**Status**: ✅ PRÊT À L'EMPLOI
**Fichiers**: driver-service seul
**Ports**: 8761, 8080, 8084, 8085
