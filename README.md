# Blackjack API - Spring Boot

A reactive REST API implementation of Blackjack built with Spring Boot and WebFlux, using MongoDB and MySQL databases.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [Docker Deployment](#docker-deployment)
- [API Documentation](#api-documentation)
- [cURL Examples](#curl-examples)

## 🎯 Overview

This project implements a Blackjack game API using Spring Boot with reactive programming. The application manages game sessions and player data using two different databases: MongoDB and MySQL.

## ✨ Features

### Level 1

- **Reactive Implementation**: Application built with Spring WebFlux
- **Global Exception Handling**: GlobalExceptionHandler for centralized error management
- **Dual Database Configuration**: MongoDB and MySQL integration
- **Unit Testing**: Controller and service tests using JUnit and Mockito
- **API Documentation**: Swagger documentation

### Level 2

- **Docker Support**: Containerized application
- **Docker Hub**: Published Docker images

## 🛠 Technology Stack

- **Java**: 17+
- **Spring Boot**: 3.x
- **Spring WebFlux**: Reactive web framework
- **Spring Data MongoDB Reactive**: Reactive MongoDB integration
- **Spring Data JPA**: MySQL integration
- **MongoDB**: Database for game data
- **MySQL**: Database for player data
- **JUnit 5**: Testing framework
- **Mockito**: Mocking framework
- **Swagger**: API documentation
- **Docker**: Containerization

## 📦 Prerequisites

- Java Development Kit (JDK) 17 or higher
- Maven 3.6+
- MongoDB 4.4+
- MySQL 8.0+
- Docker (for containerized deployment)

## 🚀 Installation

### Clone and Build

```bash
git clone https://github.com/Urian1983/S05T01-BlackJack.git
cd S05T01-BlackJack
mvn clean install
```

## ⚙️ Configuration

Configure database connections in `application.properties` or `application.yml`:

```yaml
# MongoDB Configuration
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/blackjack

# MySQL Configuration
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blackjack_players
    username: your_username
    password: your_password

# Server Configuration
server:
  port: 8080
```

## 📡 API Endpoints

### Create New Game
```http
POST /game/new
Content-Type: application/json

{
  "playerName": "Player Name"
}
```
**Response**: `201 Created` - Returns created game information

### Get Game Details
```http
GET /game/{id}
```
**Parameters**: 
- `id` - Unique game identifier

**Response**: `200 OK` - Returns detailed game information

### Play Game
```http
POST /game/{id}/play
Content-Type: application/json

{
  "action": "HIT",
  "bet": 100
}
```
**Parameters**: 
- `id` - Unique game identifier
- Body: Play action and bet amount

**Response**: `200 OK` - Returns play result and current game state

### Delete Game
```http
DELETE /game/{id}/delete
```
**Parameters**: 
- `id` - Unique game identifier

**Response**: `204 No Content` - Game successfully deleted

### Get Player Ranking
```http
GET /ranking
```
**Response**: `200 OK` - Returns player list ordered by ranking and score

### Update Player Name
```http
PUT /player/{playerId}
Content-Type: application/json

{
  "newName": "New Player Name"
}
```
**Parameters**: 
- `playerId` - Unique player identifier
- Body: New player name

**Response**: `200 OK` - Returns updated player information

## 🏃 Running the Application

### Using Maven
```bash
mvn spring-boot:run
```

### Using JAR
```bash
mvn clean package
java -jar target/blackjack-api-0.0.1-SNAPSHOT.jar
```

Application starts on `http://localhost:8080`

## 🧪 Testing

### Run All Tests
```bash
mvn test
```

The project includes unit tests for at least one controller and one service using JUnit and Mockito.

## 🐳 Docker Deployment

### Step 1: Create Dockerfile
Create a `Dockerfile` at project root

### Step 2: Create .dockerignore
Create a `.dockerignore` file at project root

### Step 3: Install Docker and Login
Install Docker on your system

### Step 4: Build Docker Image
```bash
docker build -t blackjack-api:latest .
```

### Step 5: Run Docker Image
```bash
docker run -p 8080:8080 blackjack-api:latest
```

### Step 6: Tag Docker Image
```bash
docker tag blackjack-api:latest urian1983/blackjack-backend:latest
```

### Step 7: Login to Docker Hub
```bash
docker login
```

### Step 8: Push to Docker Hub
```bash
docker push urian1983/blackjack-backend:latest
```

### Step 9: Test the Image
```bash
docker pull urian1983/blackjack-backend:latest
docker run -p 8080:8080 urian1983/blackjack-backend:latest
```

## 📚 API Documentation

Access Swagger documentation at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html

## 🔧 cURL Examples

### Ranking Management (MySQL)

**List all Rankings:**
```bash
curl -X GET http://localhost:8080/api/ranking
```

**Create/Update a Player:**
```bash
curl -X POST http://localhost:8080/api/ranking \
     -H "Content-Type: application/json" \
     -d '{"playerName": "Herr_Dealer", "score": 1000}'
```

**Search for a specific player:**
```bash
curl -X GET "http://localhost:8080/api/ranking/search?name=Herr_Dealer"
```

**Delete a record:**
```bash
curl -X DELETE http://localhost:8080/api/ranking/1
```

### Game/Log Management (MongoDB)

**Save a game result:**
```bash
curl -X POST http://localhost:8080/api/games \
     -H "Content-Type: application/json" \
     -d '{"playerName": "Herr_", "cards": ["A-Spades", "K-Hearts"], "result": "WIN"}'
```

**View complete game history:**
```bash
curl -X GET http://localhost:8080/api/games
```

### Infrastructure Utilities

**Health Check (Actuator):**
```bash
curl -X GET http://localhost:8080/actuator/health
```

**Download OpenAPI JSON:**
```bash
curl -X GET http://localhost:8080/v3/api-docs
```

---

**Happy Coding! 🎲**
