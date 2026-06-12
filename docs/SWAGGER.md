# Swagger centralisé via l'API Gateway

Un seul point d'accès agrège la documentation OpenAPI de tous les microservices.

## Accès

**http://localhost:9001/swagger-ui.html**

Un menu déroulant (en haut à droite) permet de basculer entre les services :
user-service, trajet-service, reservation-service, driver-service, payment-service.

## Fonctionnement

```
                    http://localhost:9001/swagger-ui.html
                                  │  (UI springdoc sur le Gateway)
                                  ▼
   menu déroulant ── /user-service/v3/api-docs ──► (RewritePath) ──► lb://smartride-user-service /v3/api-docs
                  ── /trajet-service/v3/api-docs ─► …                ──► lb://trajet-s /v3/api-docs
                  ── /reservation-service/v3/api-docs ──► …          ──► lb://smartride-reservation-service
                  ── /driver-service/v3/api-docs ─► …                ──► lb://driver-service
                  ── /payment-service/v3/api-docs ─► …               ──► lb://PAYMENT-SERVICE
```

- Chaque microservice expose sa propre spec OpenAPI sur `/v3/api-docs` (dépendance springdoc).
- Le Gateway ajoute une **route par service** qui réécrit `/<service>/v3/api-docs` → `/v3/api-docs`
  et l'achemine vers le service via `lb://` (Eureka).
- La propriété `springdoc.swagger-ui.urls[*]` liste ces specs dans une **UI unique**.

## Sécurité

Les chemins Swagger sont déjà en `permitAll()` dans la `SecurityConfig` du Gateway
(`/swagger-ui/**`, `/swagger-ui.html`, `/v3/api-docs/**`, `/*/v3/api-docs/**`, `/webjars/**`).
La doc est donc accessible sans token. Pour tester un endpoint **protégé** depuis Swagger UI,
il faudra coller un token Keycloak (bouton *Authorize*) ou tester via Postman.

## ⚠️ Matrice de versions springdoc (important)

springdoc **n'est pas géré** par le BOM Spring Boot et dépend de la version de Boot.
Comme les modules utilisent des versions de Boot différentes, la version springdoc diffère :

| Service              | Spring Boot | springdoc            | UI       |
|----------------------|-------------|----------------------|----------|
| ApiGateway           | 3.4.3       | 2.8.5 (webflux-ui)   | agrégée  |
| smartride-user-service     | 3.5.14 | 2.8.5 (webmvc-ui)    | exposée  |
| driver-service       | 3.2.4       | 2.6.0 (webmvc-ui)    | exposée  |
| smartride-trajet-service   | 4.0.x  | 3.0.3 (webmvc-ui)    | exposée  |
| smartride-payment-service  | 4.0.x  | 3.0.3 (webmvc-ui)    | exposée  |
| smartride-reservation-service | 4.0.x | 3.0.3 (webmvc-ui) | exposée  |

> Spring Boot 4 utilise **Jackson 3**, incompatible avec springdoc 2.x → les modules Boot 4
> requièrent springdoc **3.x**. La version est centralisée dans une propriété `<springdoc.version>`
> de chaque pom : si un build échoue, ajustez-la (ex. `mvn dependency:tree` pour vérifier).

### Recommandation
Le plus propre serait d'**harmoniser toutes les versions de Spring Boot** sur une seule
(ex. tout en 3.5.x ou tout en 4.0.x). Cela permettrait une seule version springdoc partout
et éviterait des surprises de compatibilité. À voir selon le temps disponible.

## Vérification rapide

```bash
# Spec brute d'un service à travers le Gateway
curl http://localhost:9001/trajet-service/v3/api-docs

# UI agrégée dans le navigateur
open http://localhost:9001/swagger-ui.html
```
