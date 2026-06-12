# SmartRide — Backend Microservices

Plateforme de covoiturage / VTC en architecture microservices (Spring Boot / Spring Cloud).
Gestion des utilisateurs, trajets, réservations, chauffeurs et paiements, avec sécurité
centralisée (Keycloak), communication synchrone (Feign) et asynchrone (RabbitMQ).

## Sommaire
- [Architecture](#architecture)
- [Microservices & ports](#microservices--ports)
- [Stack technique](#stack-technique)
- [Démarrage rapide (Docker)](#démarrage-rapide-docker)
- [Démarrage manuel (dev)](#démarrage-manuel-dev)
- [Sécurité (Keycloak)](#sécurité-keycloak)
- [Communication entre services](#communication-entre-services)
- [Documentation API (Swagger)](#documentation-api-swagger)
- [Documentation détaillée](#documentation-détaillée)

## Architecture

```
                         ┌─────────────┐
                         │  Keycloak   │  (OAuth2 / OIDC, rôles)
                         └──────┬──────┘
                                │ valide les JWT
   Client / Front ──► API Gateway (9001) ──► routes lb:// via Eureka
                                │
        ┌───────────────┬───────┼────────────────┬──────────────┐
        ▼               ▼       ▼                ▼              ▼
   user-service   trajet-service  reservation-service  driver-service  payment-service
     (H2)            (MySQL)         (MongoDB)             (H2)           (MySQL)
        └────────── Feign (sync) ──────────┘     └──── RabbitMQ (async) ────┘

   Eureka (8761) : découverte    |    Config Server (8888) : configuration centralisée
```

## Microservices & ports

| Service              | Nom Eureka                      | Port | Base de données | Préfixe API        |
|----------------------|----------------------------------|------|-----------------|--------------------|
| user-service         | `smartride-user-service`         | 8081 | H2 (mémoire)    | `/api/users`       |
| trajet-service       | `trajet-s`                       | 8082 | MySQL           | `/api/trajets`     |
| payment-service      | `PAYMENT-SERVICE`                | 8083 | MySQL           | `/payments`        |
| reservation-service  | `smartride-reservation-service`  | 8084 | MongoDB         | `/api/reservations`|
| driver-service       | `driver-service`                 | 8085 | H2 (mémoire)    | `/api/drivers`     |

Infrastructure : **Eureka** 8761, **Config Server** 8888, **API Gateway** 9001,
**Keycloak** 8080, **RabbitMQ** 5672 (UI 15672), **MySQL** 3306, **MongoDB** 27017.

## Stack technique

- Java 17, Spring Boot (3.2 → 4.0 selon module), Spring Cloud
- Spring Cloud Gateway, Netflix Eureka, Spring Cloud Config
- Spring Security + OAuth2 Resource Server, **Keycloak**
- OpenFeign (synchrone), **RabbitMQ** / Spring AMQP (asynchrone)
- JPA/Hibernate (MySQL, H2), Spring Data MongoDB
- springdoc-openapi (Swagger centralisé)
- Docker / Docker Compose

## Démarrage rapide (Docker)

```bash
cd SmartRide_BackEnd
docker compose build
docker compose up -d
docker compose ps          # vérifier l'état/santé
```

Points d'accès :
- API Gateway : http://localhost:9001
- Swagger UI  : http://localhost:9001/swagger-ui.html
- Eureka      : http://localhost:8761
- Keycloak    : http://localhost:8080 (admin / admin)
- RabbitMQ UI : http://localhost:15672 (guest / guest)

Détails : [docs/DOCKER.md](docs/DOCKER.md).

## Démarrage manuel (dev)

Lancer les brokers, puis les services dans l'ordre :

```bash
docker compose -f keycloak/docker-compose.yml up -d
docker compose -f rabbitmq/docker-compose.yml up -d
# puis, chacun dans son module :
#   1. EurekaServer   2. ConfigServer   3. les 5 microservices   4. ApiGateway
mvn spring-boot:run
```

> En dev local, les `application.properties` pointent sur `localhost` (BD, Eureka, RabbitMQ…).
> En Docker, ces valeurs sont surchargées par variables d'environnement (voir docker-compose.yml).

## Sécurité (Keycloak)

Sécurité **centralisée au Gateway** : il valide les JWT Keycloak et applique le contrôle
d'accès **par rôle** sur les routes. Rôles : `CLIENT`, `CHAUFFEUR`, `ADMIN`.

```bash
# Obtenir un token (utilisateur de test)
TOKEN=$(curl -s -X POST http://localhost:8080/realms/smartride/protocol/openid-connect/token \
  -d grant_type=password -d client_id=smartride-app \
  -d username=driver1 -d password=driver1 | jq -r .access_token)

curl http://localhost:9001/api/drivers -H "Authorization: Bearer $TOKEN"   # 200
curl http://localhost:9001/api/drivers                                     # 401
```

Utilisateurs de test : `client1` (CLIENT), `driver1` (CHAUFFEUR), `admin1` (ADMIN).
Détails et matrice des rôles : [docs/SECURITY.md](docs/SECURITY.md).

## Communication entre services

**Synchrone (Feign)** : ex. `payment → trajet`, `reservation → user`/`trajet`, `driver → trajet`/`reservation`.

**Asynchrone (RabbitMQ)** — 3 scénarios événementiels via l'exchange `smartride.exchange` :
1. `trajet → reservation` : trajet annulé → réservations liées annulées
2. `reservation → payment` : réservation confirmée → paiement créé
3. `payment → reservation` : paiement abouti → réservation marquée payée

Détails et démos : [docs/MESSAGING.md](docs/MESSAGING.md).

## Documentation API (Swagger)

Swagger **centralisé** via le Gateway : http://localhost:9001/swagger-ui.html
(menu déroulant pour choisir le service). Détails : [docs/SWAGGER.md](docs/SWAGGER.md).

## Documentation détaillée

| Document | Contenu |
|----------|---------|
| [docs/SECURITY.md](docs/SECURITY.md)   | Keycloak, rôles, scénarios d'autorisation |
| [docs/MESSAGING.md](docs/MESSAGING.md) | RabbitMQ, topologie, scénarios de démo |
| [docs/DOCKER.md](docs/DOCKER.md)       | Orchestration Docker Compose, ports, dépannage |
| [docs/SWAGGER.md](docs/SWAGGER.md)     | Swagger centralisé, matrice de versions |

## État d'avancement

- [x] Microservices CRUD (user, trajet, reservation, driver, payment)
- [x] Eureka, Config Server, API Gateway
- [x] Sécurité Keycloak centralisée au Gateway + rôles
- [x] Feign (synchrone) + RabbitMQ (asynchrone)
- [x] Docker Compose global
- [x] Swagger centralisé
- [ ] Front-End
- [ ] Bonus (Kafka, Prometheus/Grafana, CI/CD, Kubernetes)
