# Dota Analyse

**Dota Analyse** est une application web conçue pour les **analystes**, **coachs** et **spectateurs** de Dota 2.  
Elle permet :
- d’afficher les **parties professionnelles en cours**,
- de consulter les **matchs récents des joueurs pro**,
- de **créer une draft personnalisée** et de visualiser les **pourcentages de win rate** des équipes en fonction de leur composition.

---

## Technologies utilisées

- **Frontend** : Vue.js  
- **Backend** : Python, Spring Boot  
- **Streaming / Messaging** : Apache Kafka  
- **Base de données** : PostgreSQL  
- **Conteneurisation** : Docker

---

## Lancer le projet

### Prérequis

- Docker
- Python version 3.12
- Open Jdk version 23

### Étapes

1. Cloner le dépôt :
   ```bash
   git clone https://github.com/mmoyaerts/ProjetDOTA.git
   cd ProjetDOTA

2. Lancer le projet :
    ```
    docker-compose up --build -d

3. Lancer le back :
    cd dota
    ./mvnw spring-boot:run

4. Accéder à l'application :
    http://localhost:5173


Par Thomas Letoublon et Mathieu Moyaerts