# Communication asynchrone — RabbitMQ

Communication événementielle via un **exchange topic** unique `smartride.exchange`.
Trois scénarios asynchrones, dont un impliquant le service **trajet**.

## Topologie

```
                          smartride.exchange (topic)
                                   │
   routing keys :                  │
   ──────────────                  │
   trajet.status.changed ──────────┼────► [reservation.trajet-status.queue]      → reservation-service
   reservation.confirmed ──────────┼────► [payment.reservation-confirmed.queue]  → payment-service
   payment.completed ──────────────┴────► [reservation.payment-completed.queue]  → reservation-service
```

## Les 3 scénarios

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

## Démarrer RabbitMQ

```bash
docker compose -f rabbitmq/docker-compose.yml up -d
```
Console de management : http://localhost:15672 (guest / guest)

## Démo

Prérequis : Eureka, Config Server, Gateway, trajet/reservation/payment démarrés + RabbitMQ.

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
curl -X PUT http://localhost:9001/api/trajets/10/annuler -H "Authorization: Bearer $TOKEN"

# reservation-service log : "Réservation ... annulée suite à l'annulation du trajet 10"
# Vérifier :
curl http://localhost:9001/api/reservations/trajet/10 -H "Authorization: Bearer $TOKEN"
```

> Les chemins exacts des actions (`/confirmer`, `/annuler`, …) dépendent de vos
> `@PutMapping` ; ajustez selon vos controllers.

## Détails techniques

- **Sérialisation JSON** : `Jackson2JsonMessageConverter` avec `TypePrecedence.INFERRED`.
  Le type est déduit du paramètre du `@RabbitListener`, ce qui permet la communication
  entre services même si les classes d'événement ont des packages différents
  (pas de problème de `__TypeId__` / `ClassNotFoundException`).
- **Déclaration de la topologie** : exchange, queues et bindings sont déclarés via des
  `@Bean` ; Spring AMQP les crée automatiquement au démarrage (idempotent).
- **Durabilité** : exchange et queues sont `durable=true` (survivent à un redémarrage du broker).
