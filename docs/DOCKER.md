# Docker Compose — orchestration complète

Un seul fichier `docker-compose.yml` (à la racine du projet) lance toute la plateforme :
infrastructure + Spring Cloud core + microservices métier.

## Contenu (12 conteneurs)

| Conteneur            | Image / Build                  | Port(s)         |
|----------------------|--------------------------------|-----------------|
| mysql                | mysql:8.0                      | 3306            |
| mongo                | mongo:7                        | 27017           |
| rabbitmq             | rabbitmq:3.13-management       | 5672, 15672     |
| keycloak             | quay.io/keycloak/keycloak:26   | 8080            |
| eureka               | build ./EurekaServer           | 8761            |
| configserver         | build ./ConfigServer           | 8888            |
| gateway              | build ./ApiGateway             | 9001            |
| user-service         | build ./smartride-user-service | 8081            |
| trajet-service       | build ./smartride-trajet-service | 8082          |
| payment-service      | build ./smartride-payment-service | 8083         |
| reservation-service  | build ./smartride-reservation-service | 8084     |
| driver-service       | build ./driver-service         | 8085            |

## Démarrage

```bash
# Depuis la racine SmartRide_BackEnd/
docker compose build        # build des 8 images Spring (télécharge les dépendances Maven)
docker compose up -d        # démarre tout
docker compose logs -f gateway   # suivre un service
docker compose down         # arrêter (ajouter -v pour supprimer les volumes BD)
```

Points d'accès :
- Eureka      : http://localhost:8761
- Gateway     : http://localhost:9001
- Keycloak    : http://localhost:8080 (admin / admin)
- RabbitMQ UI : http://localhost:15672 (guest / guest)

## Principe : surcharge par variables d'environnement

Le code n'est **pas modifié** pour Docker. Les valeurs `localhost` des `application.properties`
sont surchargées au runtime via des variables d'environnement (relaxed binding Spring) :

| Propriété                                   | Variable d'environnement                          | Valeur en conteneur            |
|---------------------------------------------|---------------------------------------------------|--------------------------------|
| eureka.client.service-url.defaultZone       | EUREKA_CLIENT_SERVICEURL_DEFAULTZONE              | http://eureka:8761/eureka      |
| spring.config.import                        | SPRING_CONFIG_IMPORT                              | …configserver:http://configserver:8888 |
| spring.datasource.url                       | SPRING_DATASOURCE_URL                             | jdbc:mysql://mysql:3306/…      |
| spring.data.mongodb.uri                     | SPRING_DATA_MONGODB_URI                           | mongodb://mongo:27017/smartride |
| spring.rabbitmq.host                        | SPRING_RABBITMQ_HOST                              | rabbitmq                       |
| spring.security.oauth2…jwt.jwk-set-uri      | SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWKSETURI | http://keycloak:8080/…/certs |

Ainsi, le **dev local hors Docker continue de fonctionner** (les properties pointent toujours sur localhost).

## Le point délicat : Keycloak issuer-uri en conteneur

Problème classique : le claim `iss` d'un token est l'URL utilisée pour l'obtenir.
Les clients (Postman/Front) récupèrent le token via `http://localhost:8080` → `iss = http://localhost:8080/...`.
Mais le Gateway, dans le réseau Docker, ne peut pas joindre `localhost:8080`.

Solution appliquée :
- **issuer-uri = `http://localhost:8080/realms/smartride`** (inchangé) → la validation du claim `iss`
  correspond bien aux tokens émis aux clients.
- **jwk-set-uri = `http://keycloak:8080/.../certs`** (surchargé) → les clés publiques sont récupérées
  via le nom de conteneur `keycloak`, joignable dans le réseau interne.

Comme `jwk-set-uri` est explicite, Spring ne fait **aucun appel de découverte OIDC** vers l'issuer :
il récupère les clés depuis `keycloak:8080` et valide simplement la signature + le claim `iss`.
=> La validation fonctionne sans casser quoi que ce soit.

## Ordre de démarrage

`depends_on` + healthchecks garantissent que :
- mysql / mongo / rabbitmq sont **sains** avant de lancer les services qui en dépendent ;
- eureka et configserver sont démarrés avant les microservices.

Le Gateway récupère les clés Keycloak **paresseusement** (au premier appel sécurisé),
il peut donc démarrer avant que Keycloak soit totalement prêt.

## Bases de données

- **user-service**, **driver-service** : H2 en mémoire (aucun conteneur BD requis).
- **trajet-service**, **payment-service** : MySQL (`smartride_trajet`, `paymentservice`),
  créées automatiquement (`createDatabaseIfNotExist=true`).
- **reservation-service** : MongoDB local (`mongodb://mongo:27017/smartride`).

> ⚠️ MongoDB tourne en nœud unique (pas de replica set). Si une opération utilise
> `@Transactional` multi-documents, elle nécessite un replica set. Pour la démo, les
> opérations sont sur document unique. Si besoin de transactions Mongo, lancer mongo en
> mode replica set mono-nœud (`--replSet rs0` + `rs.initiate()`).

## Healthchecks & dépannage

```bash
docker compose ps                 # état + santé des conteneurs
docker compose logs trajet-service
docker compose restart gateway
docker compose down -v && docker compose up -d --build   # repartir de zéro
```

Si un service métier redémarre en boucle : vérifier que sa BD est `healthy`
(`docker compose ps`) et consulter ses logs.
