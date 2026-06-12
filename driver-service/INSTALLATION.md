# Guide d'Installation et Configuration - SmartRide Driver Service

## 1. Prérequis Système

### Logiciels Requis
- **Java Development Kit (JDK)**: Version 17 ou supérieure
- **MySQL**: Version 8.0 ou supérieure
- **Maven**: Version 3.6.0 ou supérieure
- **Git**: Version 2.0 ou supérieure

### Vérifier l'installation
```bash
java -version
mysql --version
mvn -version
git --version
```

## 2. Configuration MySQL

### Créer la Base de Données

```sql
-- Créer la base de données
CREATE DATABASE smartride_driver CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Vérifier
SHOW DATABASES;
```

### Vérifier l'accès MySQL

```bash
mysql -u root -p -h localhost
# Entrer le mot de passe (par défaut vide ou "root")
```

## 3. Configuration du Projet

### 3.1 Variables d'Environnement

#### Sur Windows (PowerShell)
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:MAVEN_HOME = "C:\Program Files\Apache\maven-3.x.x"
$env:PATH += ";$env:JAVA_HOME\bin;$env:MAVEN_HOME\bin"
```

#### Sur Windows (CMD)
```cmd
setx JAVA_HOME "C:\Program Files\Java\jdk-17"
setx MAVEN_HOME "C:\Program Files\Apache\maven-3.x.x"
```

#### Sur Linux/Mac
```bash
export JAVA_HOME=/usr/libexec/java_home -v 17
export MAVEN_HOME=/usr/local/maven
export PATH=$PATH:$JAVA_HOME/bin:$MAVEN_HOME/bin
```

### 3.2 Modifier application.properties

```properties
# Fichier: driver-service/src/main/resources/application.properties

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/smartride_driver?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password_here  # Remplacer par votre mot de passe MySQL

# Eureka Server
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
```

## 4. Démarrage des Services

### 4.1 Démarrer Eureka Server (Port 8761)
```bash
cd EurekaServer
./mvnw spring-boot:run
# Ou
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8761
```

Vérifier sur: http://localhost:8761

### 4.2 Démarrer API Gateway (Port 8080)
```bash
cd ApiGateway
./mvnw spring-boot:run
```

Vérifier sur: http://localhost:8080

### 4.3 Démarrer Driver Service (Port 8085)
```bash
cd driver-service
./mvnw spring-boot:run
```

Vérifier sur: http://localhost:8085/api/drivers

### 4.4 Démarrer Autres Services
```bash
# JobMS
cd JobMS
./mvnw spring-boot:run

# Reservation Service (si existant)
cd reservation-service
./mvnw spring-boot:run
```

## 5. Vérifier l'Installation

### 5.1 Checker Eureka
```bash
# Browser: http://localhost:8761
# Vous devriez voir les services enregistrés:
# - driver-service
# - ApiGateway
# - EurekaServer
# - JobMs
```

### 5.2 Tester l'API Driver Service

#### Créer un chauffeur
```bash
curl -X POST http://localhost:8085/api/drivers \
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

#### Récupérer tous les chauffeurs
```bash
curl -X GET http://localhost:8085/api/drivers
```

#### Récupérer un chauffeur par ID
```bash
curl -X GET http://localhost:8085/api/drivers/1
```

#### Changer le statut
```bash
curl -X PATCH "http://localhost:8085/api/drivers/1/status?statut=OCCUPÉ"
```

#### Supprimer un chauffeur
```bash
curl -X DELETE http://localhost:8085/api/drivers/1
```

## 6. Configuration API Gateway

### Ajouter la route Driver Service dans API Gateway

Fichier: `ApiGateway/src/main/resources/application.properties`

```properties
spring.cloud.gateway.routes[2].id=driver-service
spring.cloud.gateway.routes[2].uri=lb://driver-service
spring.cloud.gateway.routes[2].predicates[0]=Path=/api/drivers/**
spring.cloud.gateway.routes[2].filters[0]=StripPrefix=0
```

Puis tester via API Gateway:
```bash
curl -X GET http://localhost:8080/api/drivers
```

## 7. Intégration avec Reservation Service (OpenFeign)

### Configuration du Client Feign

Le fichier `ReservationClient.java` configure automatiquement:

```java
@FeignClient(name = "reservation-service", url = "http://localhost:8086")
```

### Endpoints pour les Réservations
```bash
# Récupérer les réservations d'un chauffeur
curl -X GET http://localhost:8085/api/drivers/1/reservations
```

## 8. Structure des Bases de Données

### Table drivers
```sql
CREATE TABLE drivers (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nom VARCHAR(100) NOT NULL,
  prenom VARCHAR(100) NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  telephone VARCHAR(20) NOT NULL,
  statut ENUM('DISPONIBLE', 'OCCUPÉ', 'HORS_LIGNE') DEFAULT 'HORS_LIGNE',
  marque_vehicule VARCHAR(50) NOT NULL,
  modele_vehicule VARCHAR(50) NOT NULL,
  plaque_immatriculation VARCHAR(20) UNIQUE NOT NULL,
  user_id BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

## 9. Logs et Debugging

### Activer les Logs Debug

Ajouter à `application.properties`:
```properties
logging.level.root=INFO
logging.level.esprit.driver=DEBUG
logging.level.org.springframework.cloud.openfeign=DEBUG
logging.level.com.netflix.eureka=DEBUG
```

### Fichier Log
```bash
# Les logs sont générés dans: driver-service/logs/application.log
tail -f driver-service/logs/application.log
```

## 10. Commandes Maven Utiles

```bash
# Nettoyer le projet
./mvnw clean

# Compiler
./mvnw compile

# Exécuter les tests
./mvnw test

# Construire le JAR
./mvnw package -DskipTests

# Exécuter l'application
./mvnw spring-boot:run

# Exécuter avec un profil spécifique
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

# Checker les dépendances
./mvnw dependency:tree
```

## 11. Dépannage Courants

### Erreur: Port déjà utilisé
```bash
# Trouver le processus utilisant le port
lsof -i :8085  # Mac/Linux
netstat -ano | findstr :8085  # Windows

# Arrêter le processus
kill -9 <PID>  # Mac/Linux
taskkill /PID <PID> /F  # Windows
```

### Erreur: Connexion MySQL impossible
```bash
# Vérifier que MySQL est en cours d'exécution
mysql -u root -p

# Vérifier les credentials dans application.properties
# S'assurer que la base de données existe
mysql -u root -p -e "SHOW DATABASES;"
```

### Erreur: Eureka ne trouve pas le service
- Vérifier que Eureka Server s'exécute sur le port 8761
- Attendre 30-60 secondes que le service s'enregistre
- Vérifier les logs pour les erreurs de connexion

### Service ne se découvre pas via Eureka
```properties
# Ajouter à application.properties
eureka.instance.hostname=localhost
eureka.instance.prefer-ip-address=true
```

## 12. Performance et Optimisation

### Augmenter la mémoire JVM

```bash
# Linux/Mac
export MAVEN_OPTS="-Xmx512m -Xms256m"
./mvnw spring-boot:run

# Windows
set MAVEN_OPTS=-Xmx512m -Xms256m
mvnw.cmd spring-boot:run
```

### Connection Pool
```properties
# Ajouter à application.properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
```

## 13. Déploiement en Production

### Générer le WAR/JAR
```bash
./mvnw clean package -DskipTests -Pprod
```

### Docker (Optionnel)
```dockerfile
FROM openjdk:17-slim
COPY target/driver-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 14. Support et Ressources

- Documentation Spring Boot: https://spring.io/projects/spring-boot
- Documentation Spring Cloud: https://spring.io/projects/spring-cloud
- MySQL Documentation: https://dev.mysql.com/doc/
- Eureka Documentation: https://github.com/Netflix/eureka/wiki

---
**Date**: Mai 2024
**Version**: 1.0.0
**Auteur**: SmartRide Team
