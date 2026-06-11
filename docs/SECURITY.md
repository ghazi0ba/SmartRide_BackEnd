# Sécurité — Keycloak centralisée au Gateway

## 1. Architecture

```
Client (Postman / Front)
        │  Authorization: Bearer <JWT Keycloak>
        ▼
   API Gateway (port 9001)  ──►  OAuth2 Resource Server
        │   - valide le JWT (signature via JWKS Keycloak)
        │   - extrait les rôles (realm_access / resource_access)
        │   - autorise/refuse selon la route (RBAC)
        ▼
   Microservices (user, trajet, reservation, driver, payment)
        (réseau interne — font confiance au Gateway)
```

La sécurité est **centralisée au Gateway**. Les microservices en aval ne revalident pas
le token : ils sont accessibles via le Gateway, qui est le seul point d'entrée.

Rôles métier déclarés dans Keycloak : **CLIENT**, **CHAUFFEUR**, **ADMIN**.

## 2. Démarrer Keycloak

```bash
docker compose -f keycloak/docker-compose.yml up -d
```

- Console admin : http://localhost:8080 (admin / admin)
- Realm importé automatiquement : `smartride`
- Client : `smartride-app` (public, Direct Access Grants activé)

Utilisateurs de test (username / password / rôle) :

| Utilisateur | Mot de passe | Rôle(s)                    |
|-------------|--------------|----------------------------|
| client1     | client1      | CLIENT                     |
| driver1     | driver1      | CHAUFFEUR                  |
| admin1      | admin1       | ADMIN (+ CLIENT, CHAUFFEUR)|

## 3. Récupérer un token (Direct Access Grant)

```bash
curl -s -X POST \
  http://localhost:8080/realms/smartride/protocol/openid-connect/token \
  -d "grant_type=password" \
  -d "client_id=smartride-app" \
  -d "username=client1" \
  -d "password=client1" | jq -r .access_token
```

Stocker le token :

```bash
TOKEN=$(curl -s -X POST \
  http://localhost:8080/realms/smartride/protocol/openid-connect/token \
  -d "grant_type=password" -d "client_id=smartride-app" \
  -d "username=driver1" -d "password=driver1" | jq -r .access_token)
```

## 4. Appeler le Gateway

```bash
# Sans token -> 401 Unauthorized
curl -i http://localhost:9001/api/trajets

# Avec token -> 200
curl -i http://localhost:9001/api/trajets -H "Authorization: Bearer $TOKEN"
```

## 5. Matrice d'autorisation (RBAC au Gateway)

| Route                     | Rôles autorisés              |
|---------------------------|------------------------------|
| `/api/users/register`     | public                       |
| `/api/users/login`        | public                       |
| `/api/users/**` (autres)  | ADMIN                        |
| `/api/trajets/**`         | CLIENT, CHAUFFEUR, ADMIN     |
| `/api/reservations/**`    | CLIENT, CHAUFFEUR, ADMIN     |
| `/api/drivers/**`         | CHAUFFEUR, ADMIN             |
| `/payments/**`            | CLIENT, ADMIN                |
| `/actuator/**`, swagger   | public                       |

## 6. Scénarios de démonstration (rôles)

```bash
# 1) Un CLIENT accède aux réservations -> 200
TOKEN=$(get_token client1 client1)
curl -i http://localhost:9001/api/reservations -H "Authorization: Bearer $TOKEN"   # 200

# 2) Le même CLIENT tente d'accéder à l'espace chauffeur -> 403 Forbidden
curl -i http://localhost:9001/api/drivers -H "Authorization: Bearer $TOKEN"        # 403

# 3) Un CHAUFFEUR accède à l'espace chauffeur -> 200
TOKEN=$(get_token driver1 driver1)
curl -i http://localhost:9001/api/drivers -H "Authorization: Bearer $TOKEN"        # 200

# 4) Un ADMIN accède à la gestion des utilisateurs -> 200
TOKEN=$(get_token admin1 admin1)
curl -i http://localhost:9001/api/users/1 -H "Authorization: Bearer $TOKEN"        # 200
```

(`get_token` = la commande curl de la section 3 avec username/password en arguments.)

## 7. Notes

- **Token forwarding** : le Gateway transmet l'en-tête `Authorization` aux microservices.
  Les appels **Feign inter-services** ne propagent pas automatiquement ce token ; ils se font
  sur le réseau interne. Si l'on veut propager le token via Feign, ajouter un
  `RequestInterceptor` (amélioration ultérieure).
- **user-service** garde son JWT maison (jjwt) pour `register`/`login` historiques, qui restent
  publics. L'autorité de référence pour la sécurité est désormais Keycloak au Gateway.
- **Docker (étape 4)** : en conteneur, l'`issuer-uri` doit correspondre au `iss` du token.
  Si les tokens sont émis via `http://localhost:8080` mais que le Gateway tourne en conteneur,
  utiliser un hostname commun (ex. `KC_HOSTNAME` + même URL côté client et côté Gateway) pour
  éviter un mismatch d'issuer.