🃏 S05T01-BlackJack | IT Academy Backend Java
This repository contains the official solution for Spring 5 Task 1 (S05T01) of the Backend Java curriculum at IT Academy. This project demonstrates the implementation of a Reactive REST API using Spring Boot WebFlux, integrated with a polyglot persistence layer.

📋 Table of Contents
Project Overview

Key Requirements

Technology Stack

Installation & Setup

API Endpoints

Docker Deployment

Testing

🎯 Project Overview
The goal of this exercise is to develop a Blackjack game engine that operates on a non-blocking, reactive paradigm. By utilizing Spring WebFlux, the application handles concurrent requests efficiently, managing game state in MongoDB and player statistics in MySQL.

✨ Key Requirements
🔹 Level 1: Reactive Development & Persistence
Reactive Flow: Full implementation using Mono and Flux to ensure a non-blocking execution thread.

Polyglot Persistence:

MySQL: Primary storage for player profiles and high-score rankings (via Spring Data JPA).

MongoDB: Document-based storage for game session logs and active states (via Spring Data MongoDB Reactive).

Centralized Error Handling: Use of GlobalExceptionHandler to provide consistent, reactive-friendly error responses.

Testing Suite: Unit testing for critical Service and Controller layers using JUnit 5 and Mockito.

🔹 Level 2: DevOps Integration
Containerization: Optimized Dockerfile for the Spring Boot application.

Cloud Registry: Image published and maintained on Docker Hub.

🛠 Technology Stack
Java: 17

Framework: Spring Boot 3.x (WebFlux / Spring 5)

Databases:

Relational: MySQL 8.0

Document: MongoDB (Reactive)

Documentation: SpringDoc OpenAPI (Swagger UI)

Containerization: Docker

🚀 Installation & Setup
Clone the repository:

Bash
git clone https://github.com/Urian1983/S05T01-BlackJack.git
cd S05T01-BlackJack
Configuration: Update src/main/resources/application.properties with your database connections:

Properties
# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/blackjack_players
spring.datasource.username=your_user
spring.datasource.password=your_password

# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/blackjack
Build and Run:

Bash
mvn clean install
mvn spring-boot:run
📡 API Endpoints
🃏 Game Management (Reactive MongoDB)
POST /game/new: Create a new game session.

GET /game/{id}: View the status of an ongoing game.

POST /game/{id}/play: Submit a move (HIT/STAND).

🏆 Player Rankings (MySQL)
GET /ranking: Retrieve the global leaderboard.

PUT /player/{id}: Edit player credentials.

DELETE /game/{id}/delete: Remove specific game records.

🐳 Docker Deployment
The application is container-ready. You can find the official image repository here: 👉 Docker Hub: urian1983/blackjack-backend

To run it directly:

Bash
# Pull the latest image
docker pull urian1983/blackjack-backend:latest

# Execute the container
docker run -p 8080:8080 urian1983/blackjack-backend:latest
🧪 Testing
To execute the reactive test suite and ensure all components are functioning correctly:

Bash
mvn test
