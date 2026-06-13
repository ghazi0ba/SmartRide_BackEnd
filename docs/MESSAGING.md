# Communication asynchrone — RabbitMQ

Communication événementielle via un **exchange topic** unique `smartride.exchange`.
Quatre scénarios asynchrones, dont un **polyglotte** (Java → NestJS) impliquant le
microservice **rating-service**.

## Topologie

```
                          smartride.exchange (topic)
                                   │
   routing keys :                  │
   ──────────────                  │
   trajet.status.changed ──────────┼────► [reservation.trajet-status.queue]      → reservation-service
   reservation.confirmed ──────────┼────► [payment.reservation-confirmed.queue]  → payment-service
   payment.completed ──────────────┼────► [reservation.payment-completed.queue]  → reservation-service
   trajet.terminated ──────────────┴────► [rating.trajet-terminated.queue]        → rating-service (NestJS)
```

## Les 4 scénarios

### Scénario 1 — Trajet → Reservation (annulation en cascade)
- **Producteur** : `trajet-service` — sur `annulerTrajet` (et aussi `demarrerTrajet` / `terminerTrajet`),
  publie `TrajetStatusChangedEvent { trajetId, statut }` avec la clé `trajet.status.changed`.
- **Consommateur** : `reservation-service` — si `statut == ANNULE`, annule toutes les
  réservations actives (PENDING / CONFIRMED) liées à ce trajet.

### Scénario 2 — Reservation → Payment (création de paiement)
- **Producteur** : `reservation-service` — sur `confirmerReservation`, publie
  `ReservationConfirmedEvent { reservationId, userId, driverId, trajetId, montant }`
  avec la clé `reservation.confirmed`.
- **Consommateur** : `payment-service` — crée un `Payment` (SUCCESS) pour cette réservation.

### Scénario 3 — Payment → Reservation (confirmation de paiement)
- **Producteur** : `payment-service` — après création du paiement, publie
  `PaymentCompletedEvent { reservationId, status, amount }` avec la clé `payment.completed`.
- **Consommateur** : `reservation-service` — marque la réservation comme payée (`paid = true`).

> Les scénarios 2 et 3 s'enchaînent : confirmer une réservation déclenche
> automatiquement la création du paiement, qui déclenche le marquage « payé ».

### Scénario 4 — Trajet → Rating (polyglotte : Java → NestJS)
- **Producteur** : `trajet-service` (Java) — sur `terminerTrajet` (statut → TERMINE), publie
  `TrajetTerminatedEvent { userId, chauffeurId, trajetId }` avec la clé `trajet.terminated`.
- **Consommateur** : `rating-service` (NestJS / Node.js) — file `rating.trajet-terminated.queue`
  liée à l'exchange par la clé `trajet.terminated`. À réception, crée automatiquement un avis
  « en attente de notation » (note 0, statut `pending`) pour ce trajet/chauffeur.

> Démonstration intéressante : communication **inter-langages** (Spring/Java ↔ NestJS/Node)
> via le même exchange RabbitMQ. La sérialisation est du JSON simple : le consommateur Node
> lit le corps du message et ignore les en-têtes Spring, donc l'interopérabilité fonctionne
> sans adaptation particulière.

## Démarrer RabbitMQ

```bash
docker compose -f rabbitmq/docker-compose.yml up -d
```
Console de management : http://localhost:15672 (guest / guest)

## Démo

Prérequis : Eureka, Config Server, Gateway, trajet/reservation/payment démarrés + RabbitMQ.
Pour le scénario 4, ajouter le `rating-service` (NestJS) + un MongoDB local.

### Démo scénario 2 + 3 (enchaînés)
```bash
# 1) Créer une réservation (statut PENDING) — adapter le body à votre DTO
curl -X POST http://localhost:9001/api/reservations \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{ "userId": 1, "driverId": 2, "trajetId": 10, "nombrePassagers": 1, "prixTotal": 25.0 }'

# 2) Confirmer la réservation -> publie reservation.confirmed
curl -X PUT http://localhost:9001/api/reservations/{reservationId}/confirmer \
  -H "Authorization: Bearer $TOKEN"

# 3) Observer :
#    - payment-service log : "ReservationConfirmedEvent recu" puis "Paiement cree"
#    - reservation-service log : "PaymentCompletedEvent recu" puis "marquée comme payée"
#    - GET du paiement par trajet :
curl http://localhost:9001/payments/trajet/10 -H "Authorization: Bearer $TOKEN"
```

### Démo scénario 1 (trajet)
```bash
# Annuler un trajet ayant des réservations -> publie trajet.status.changed (ANNULE)
curl -X PUT http://localhost:9001/smartride_trajet/api/trajets/10/annuler -H "Authorization: Bearer $TOKEN"

# reservation-service log : "Réservation ... annulée suite à l'annulation du trajet 10"
# Vérifier :
curl http://localhost:9001/api/reservations/trajet/10 -H "Authorization: Bearer $TOKEN"
```

### Démo scénario 4 (trajet → rating)
```bash
# 1) Terminer un trajet EN_COURS -> publie trajet.terminated
curl -X PUT http://localhost:9001/api/trajets/{trajetId}/terminer -H "Authorization: Bearer $TOKEN"

# 2) rating-service log : "Trajet termine recu" puis "Avis cree pour trajet: ..."

# 3) Vérifier l'avis créé (statut "pending", note 0) :
curl http://localhost:9001/api/ratings -H "Authorization: Bearer $TOKEN"
```

#### Test sans trajet-service (simulation intégrée)
Le rating-service expose un endpoint qui publie lui-même l'événement, pratique pour tester
le flux sans démarrer trajet-service :
```bash
curl -X POST http://localhost:8087/api/ratings/simulate/trajet-termine \
  -H "Content-Type: application/json" \
  -d '{ "userId": "1", "chauffeurId": "2", "trajetId": "10" }'
```

> Les chemins exacts des actions (`/confirmer`, `/annuler`, `/terminer`, …) dépendent de vos
> `@PutMapping` ; ajustez selon vos controllers.

## Intégration du rating-service (NestJS)

- **Techno** : NestJS (Node.js), port **8087**, MongoDB local `mongodb://localhost:27017/rating-service`.
- **Eureka** : s'enregistre comme `RATING-SERVICE` (via `eureka.config.ts`).
- **Gateway** : route `/api/ratings/**` → `lb://RATING-SERVICE` (rôles CLIENT/CHAUFFEUR/ADMIN).
- **Lancement** : `npm install` puis `npm run start:dev` (nécessite un **MongoDB local** sur 27017,
  RabbitMQ et Eureka démarrés).

> ⚠️ Le `rating-service` n'est **pas** un module Maven (c'est du Node/NestJS) : ne pas l'ajouter
> au `pom.xml` agrégateur, sinon `mvn install` échoue.

## Détails techniques

- **Sérialisation JSON** : `Jackson2JsonMessageConverter` avec `TypePrecedence.INFERRED`.
  Le type est déduit du paramètre du `@RabbitListener`, ce qui permet la communication
  entre services même si les classes d'événement ont des packages différents
  (pas de problème de `__TypeId__` / `ClassNotFoundException`).
- **Déclaration de la topologie** : exchange, queues et bindings sont déclarés via des
  `@Bean` côté Java ; côté NestJS, le consommateur déclare l'exchange, la file et le binding
  avec `amqplib` (`assertExchange` / `assertQueue` / `bindQueue`). Tout est idempotent.
- **Durabilité** : exchange et queues sont `durable=true` (survivent à un redémarrage du broker).