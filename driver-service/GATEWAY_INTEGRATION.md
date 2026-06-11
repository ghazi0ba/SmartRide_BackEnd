# API Gateway Integration - Configuration Guide

## Overview

L'API Gateway route les requêtes vers les microservices correspondants. Pour intégrer driver-service, vous devez configurer les routes.

## Configuration de l'API Gateway

### Fichier: ApiGateway/src/main/resources/application.properties

Ajouter les configurations suivantes pour router vers driver-service:

```properties
# Existing routes...

# Route pour Driver Service
spring.cloud.gateway.routes[2].id=driver-service
spring.cloud.gateway.routes[2].uri=lb://driver-service
spring.cloud.gateway.routes[2].predicates[0]=Path=/api/drivers/**
spring.cloud.gateway.routes[2].filters[0]=StripPrefix=0

# Optional: Add Custom Filters
spring.cloud.gateway.routes[2].filters[1]=AuthorizationHeaderFilter
```

### Fichier: ApiGateway/src/main/resources/application.yml

Alternative YAML configuration:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: driver-service
          uri: lb://driver-service
          predicates:
            - Path=/api/drivers/**
          filters:
            - StripPrefix=0
            # Optional: - AuthorizationHeaderFilter
```

## Routes Disponibles via API Gateway

Après configuration, les routes suivantes sont accessibles:

### Via API Gateway (Port 8080)
```bash
# Instead of: http://localhost:8085/api/drivers
# Use: http://localhost:8080/api/drivers
```

### Exemples d'appels via API Gateway

#### Get All Drivers
```bash
curl -X GET http://localhost:8080/api/drivers
```

#### Create Driver
```bash
curl -X POST http://localhost:8080/api/drivers \
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

#### Get Driver by ID
```bash
curl -X GET http://localhost:8080/api/drivers/1
```

#### Update Driver Status
```bash
curl -X PATCH "http://localhost:8080/api/drivers/1/status?statut=OCCUPÉ"
```

#### Get Driver Reservations
```bash
curl -X GET http://localhost:8080/api/drivers/1/reservations
```

## Advanced Gateway Configuration

### 1. Authentication Filter

Si vous avez besoin d'authentification:

```java
// ApiGateway/src/main/java/esprit/gateway/filter/AuthorizationHeaderFilter.java
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    
    public AuthorizationHeaderFilter() {
        super(Config.class);
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authHeader == null || authHeader.isEmpty()) {
                return Mono.error(new RuntimeException("Missing Authorization Header"));
            }
            return chain.filter(exchange);
        };
    }
    
    public static class Config {
        // configuration properties
    }
}
```

### 2. Load Balancing

API Gateway utilise automatiquement Eureka pour load balancing:

```
Request → API Gateway
            ↓
         Load Balancer (Eureka)
            ↓
         ┌─────────────────────┐
         │  driver-service #1  │ (8085)
         │  driver-service #2  │ (8086)
         │  driver-service #3  │ (8087)
         └─────────────────────┘
```

### 3. Rate Limiting (Optional)

```properties
# Add to application.properties
spring.cloud.gateway.routes[2].filters[0]=RequestRateLimiter=10,20
```

### 4. Retry Policy (Optional)

```properties
# Configuration pour retry automatique
spring.cloud.gateway.routes[2].filters[0]=Retry=3,500
```

## Monitoring Gateway Routes

### Check Registered Routes

```bash
# Endpoint pour voir toutes les routes enregistrées
curl -X GET http://localhost:8080/actuator/gateway/routes
```

### Health Check

```bash
# Vérifier la santé de l'API Gateway
curl -X GET http://localhost:8080/actuator/health
```

## Circuit Breaker Pattern (Optional)

Implémenter avec Resilience4j:

```properties
# Add to pom.xml dependency
spring-cloud-starter-circuitbreaker-resilience4j

# Configuration
resilience4j.circuitbreaker.instances.driver-service.registerHealthIndicator=true
resilience4j.circuitbreaker.instances.driver-service.slidingWindowSize=10
resilience4j.circuitbreaker.instances.driver-service.minimumNumberOfCalls=5
resilience4j.circuitbreaker.instances.driver-service.permittedNumberOfCallsInHalfOpenState=3
resilience4j.circuitbreaker.instances.driver-service.automaticTransitionFromOpenToHalfOpenEnabled=true
resilience4j.circuitbreaker.instances.driver-service.waitDurationInOpenState=10s
resilience4j.circuitbreaker.instances.driver-service.failureRateThreshold=50
```

## Cross-Origin Resource Sharing (CORS)

Configuration CORS au niveau API Gateway:

```java
// ApiGateway/src/main/java/esprit/gateway/config/CorsConfig.java
@Configuration
public class CorsConfig {
    
    @Bean
    public WebFluxConfigurer corsConfigurer() {
        return new WebFluxConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("*")
                    .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
                    .allowedHeaders("*")
                    .maxAge(3600);
            }
        };
    }
}
```

## Logging & Debugging

### Activer les logs de Gateway

```properties
# application.properties
logging.level.org.springframework.cloud.gateway=DEBUG
logging.level.esprit.gateway=DEBUG
```

### Voir les requêtes passant par le gateway

```bash
# Dans les logs
[org.springframework.cloud.gateway.handler.RoutePredicateHandlerMapping] 
Mapping [GET /api/drivers/**] to handler for predicate 
[Path=/api/drivers/**, method Match=[GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS, TRACE]]
```

## Versioning API

Supporter plusieurs versions:

```properties
# Version 1 (Legacy)
spring.cloud.gateway.routes[2].id=driver-service-v1
spring.cloud.gateway.routes[2].uri=lb://driver-service
spring.cloud.gateway.routes[2].predicates[0]=Path=/api/v1/drivers/**

# Version 2 (Current)
spring.cloud.gateway.routes[3].id=driver-service-v2
spring.cloud.gateway.routes[3].uri=lb://driver-service
spring.cloud.gateway.routes[3].predicates[0]=Path=/api/v2/drivers/**
```

## WebSocket Support (Future)

Si vous avez besoin du WebSocket:

```properties
# Add WebSocket support
spring.cloud.gateway.routes[2].predicates[1]=Path=/ws/drivers/**
spring.cloud.gateway.routes[2].filters[0]=RewritePath=/ws(?<segment>.*), $\{segment}
```

## SSL/TLS Configuration

Pour sécuriser le gateway:

```properties
# server.ssl.key-store=classpath:keystore.p12
# server.ssl.key-store-password=password
# server.ssl.key-store-type=PKCS12
```

## Compression

Pour optimiser la bande passante:

```properties
server.compression.enabled=true
server.compression.min-response-size=1024
```

## Response Rewriting

Ajouter/modifier les en-têtes de réponse:

```properties
spring.cloud.gateway.routes[2].filters[0]=AddResponseHeader=X-Response-Default-Foo, Bar
```

## Request Modification

Modifier les requêtes avant d'envoyer au service:

```properties
spring.cloud.gateway.routes[2].filters[0]=AddRequestHeader=X-Request-Header, Value
spring.cloud.gateway.routes[2].filters[1]=RemoveRequestHeader=Foo
```

## Testing Gateway Configuration

```bash
# Test 1: Health Check
curl -X GET http://localhost:8080/actuator/health

# Test 2: Get Routes
curl -X GET http://localhost:8080/actuator/gateway/routes

# Test 3: Get Drivers via Gateway
curl -X GET http://localhost:8080/api/drivers

# Test 4: Create Driver via Gateway
curl -X POST http://localhost:8080/api/drivers \
  -H "Content-Type: application/json" \
  -d '{"nom":"Test","prenom":"User",...}'

# Test 5: Monitor Gateway Metrics
curl -X GET http://localhost:8080/actuator/metrics
```

## Troubleshooting

### Issue: Route not working
```
Solution:
1. Verify driver-service is registered in Eureka
2. Check gateway route configuration
3. Verify predicate Path matches request
4. Check gateway logs
```

### Issue: 503 Service Unavailable
```
Solution:
1. Verify driver-service is running
2. Check Eureka registration
3. Verify database connection
4. Check firewall rules
```

### Issue: CORS error
```
Solution:
1. Add CORS configuration to gateway
2. Enable CORS in driver-service controller
3. Verify Origin headers
```

## Performance Tips

1. **Connection Pooling**: Configure HikariCP in services
2. **Caching**: Implement Redis caching for frequent queries
3. **Compression**: Enable response compression
4. **CDN**: Use CDN for static content
5. **Monitoring**: Use Spring Boot Actuator & Micrometer

## Reference URLs

- API Gateway Health: http://localhost:8080/actuator/health
- Eureka Dashboard: http://localhost:8761
- Driver Service Direct: http://localhost:8085/api/drivers
- Driver Service via Gateway: http://localhost:8080/api/drivers

---

**Last Updated**: May 2024
**Version**: 1.0.0
