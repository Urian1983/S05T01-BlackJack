# 🃏 S05T01 — BlackJack | IT Academy Backend Java

> **[English](#english-version) | [Español](#versión-en-español)**

---

<a name="english-version"></a>
# 🇬🇧 English Version

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Key Requirements](#key-requirements)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Installation & Setup](#installation--setup)
- [API Endpoints](#api-endpoints)
- [Security](#security)
- [Docker Deployment](#docker-deployment)
- [Testing](#testing)

---

## 🎯 Project Overview

This project is the official solution for **Sprint 5 Task 1 (S05T01)** of the Backend Java curriculum at IT Academy. It implements a fully reactive Blackjack game engine built on **Spring Boot WebFlux**, following a non-blocking, asynchronous paradigm throughout the entire application stack.

Game state and sessions are persisted in **MongoDB**, while player statistics and rankings are stored in **MySQL** via R2DBC — forming a polyglot persistence architecture.

---

## ✨ Key Requirements

### 🔹 Level 1 — Reactive Development & Persistence

- **Reactive Flow:** Full implementation using `Mono` and `Flux` to ensure non-blocking execution on every layer (Controller → Service → Repository).
- **Polyglot Persistence:**
  - **MySQL (R2DBC):** Stores player profiles and high-score rankings via Spring Data R2DBC.
  - **MongoDB (Reactive):** Stores active game sessions and document-based game state via Spring Data MongoDB Reactive.
- **Centralized Error Handling:** A `GlobalExceptionHandler` provides consistent, reactive-friendly error responses for `GameNotFoundException`, `IllegalMoveException`, `NullPointerException`, and general exceptions.
- **Testing Suite:** Unit and integration tests for the Service and Controller layers using JUnit 5, Mockito, `StepVerifier`, and `WebTestClient`.

### 🔹 Level 2 — DevOps Integration

- **Containerization:** Multi-stage `Dockerfile` for the Spring Boot application.
- **Orchestration:** `docker-compose.yml` that orchestrates the app, MySQL, and MongoDB together.
- **Cloud Registry:** Image published on Docker Hub.

---

## 🛠 Technology Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 / Spring WebFlux |
| Reactive Library | Project Reactor (Mono / Flux) |
| Relational DB | MySQL 8.0 (via R2DBC) |
| Document DB | MongoDB (Reactive) |
| Security | Spring Security (HTTP Basic) |
| API Docs | SpringDoc OpenAPI / Swagger UI |
| Build Tool | Maven 3.9 |
| Containerization | Docker / Docker Compose |
| Testing | JUnit 5, Mockito, Reactor Test, WebTestClient |
| Utilities | Lombok |

---

## 🗂 Project Structure

```
S05T01-BlackJack/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── mvnw / mvnw.cmd
└── src/
    ├── main/
    │   ├── java/it/academy/blackjack/
    │   │   ├── BlackjackApplication.java
    │   │   ├── config/
    │   │   │   ├── MongoConfig.java
    │   │   │   ├── OpenAPIConfig.java
    │   │   │   └── R2dbcConfig.java
    │   │   ├── controller/
    │   │   │   ├── GameController.java
    │   │   │   └── RankingController.java
    │   │   ├── domain/
    │   │   │   ├── Game.java
    │   │   │   ├── enums/
    │   │   │   │   ├── GameState.java
    │   │   │   │   ├── Rank.java
    │   │   │   │   └── Suit.java
    │   │   │   └── model/
    │   │   │       ├── Card.java
    │   │   │       ├── Dealer.java
    │   │   │       ├── Deck.java
    │   │   │       ├── Hand.java
    │   │   │       ├── Player.java
    │   │   │       └── Ranking.java
    │   │   ├── dto/
    │   │   │   ├── game/
    │   │   │   │   └── GameResponseDTO.java
    │   │   │   └── ranking/
    │   │   │       ├── RankingRequestDTO.java
    │   │   │       ├── RankingResponseDTO.java
    │   │   │       └── RenamePlayerDTO.java
    │   │   ├── exceptions/
    │   │   │   ├── GameNotFoundException.java
    │   │   │   ├── GlobalExceptionHandler.java
    │   │   │   └── IllegalMoveException.java
    │   │   ├── mapper/
    │   │   │   ├── GameMapper.java
    │   │   │   ├── GameMapperImpl.java
    │   │   │   ├── RankingMapper.java
    │   │   │   ├── RankingMapperImpl.java
    │   │   │   └── RenamePlayerMapper.java
    │   │   ├── repository/
    │   │   │   ├── mongodb/
    │   │   │   │   └── GameRepository.java
    │   │   │   └── mysql/
    │   │   │       └── RankingRepository.java
    │   │   ├── security/
    │   │   │   └── SecurityConfig.java
    │   │   └── service/
    │   │       ├── mongodb/
    │   │       │   ├── GameService.java
    │   │       │   └── GameServiceImpl.java
    │   │       └── mysql/
    │   │           ├── RankingService.java
    │   │           └── RankingServiceImpl.java
    │   └── resources/
    │       ├── application.properties
    │       └── schema.sql
    └── test/
        └── java/it/academy/blackjack/
            ├── BlackjackApplicationTests.java
            ├── controller/
            │   ├── GameControllerTest.java
            │   └── RankingControllerTest.java
            ├── domain/model/
            │   ├── CardTest.java
            │   ├── DeckTest.java
            │   ├── HandTest.java
            │   └── RankingTest.java
            └── service/
                ├── mongodb/
                │   └── GameServiceImplTest.java
                └── mysql/
                    └── RankingServiceImplTest.java
```

---

## 🏗 Architecture

The application follows a layered reactive architecture:

```
Controller (WebFlux REST)
    ↓ Mono / Flux
Service Layer
    ├── GameServiceImpl  →  MongoDB (ReactiveMongoRepository)
    └── RankingServiceImpl → MySQL (R2dbcRepository)
        ↓
Mapper (Entity ↔ DTO)
        ↓
GlobalExceptionHandler (centralized error responses)
```

**Game domain model:**

```
Game (MongoDB Document)
 ├── Player (name, Hand, stay)
 ├── Dealer extends Player (shouldHit logic: hit below 17)
 ├── Deck (52 unique Cards, shuffled)
 ├── Hand (List<Card>, calculateValue with Ace logic)
 └── GameState (IN_PROGRESS | PLAYER_WIN | DEALER_WIN | PLAYER_BUST | DEALER_BUST | DRAW)
```

**Blackjack rules implemented:**
- Standard initial deal (2 cards each).
- Natural Blackjack detection on deal.
- Player actions: **HIT**, **STAND**, **DOUBLE DOWN**.
- Dealer plays automatically after player stands (hits until ≥ 17).
- Ace valued at 11 or 1 to avoid bust.
- Double Down only allowed on initial 2-card hand.

---

## 🚀 Installation & Setup

### Prerequisites

- Java 21+
- Maven 3.9+
- Docker & Docker Compose (for containerized setup)
- A running MySQL 8.0 instance on port `3306` (or `3307` for local override)
- A running MongoDB instance on port `27017`

### Local Run (Manual)

1. **Clone the repository:**
   ```bash
   git clone <your-repo-url>
   cd S05T01-BlackJack
   ```

2. **Configure database connections** in `src/main/resources/application.properties`:
   ```properties
   # MongoDB
   spring.data.mongodb.uri=mongodb://localhost:27017/blackjack

   # MySQL (R2DBC)
   spring.r2dbc.url=r2dbc:mysql://localhost:3306/blackjack
   spring.r2dbc.username=root
   spring.r2dbc.password=root
   ```
   > The schema (`player_ranking` table) is created automatically on startup via `schema.sql`.

3. **Build and run:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Access Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 📡 API Endpoints

### 🃏 Game Management — `/game` (MongoDB)

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `POST` | `/game/new` | Create a new game session | Yes (any role) |
| `GET` | `/game/{id}` | Get game status by ID | Yes |
| `POST` | `/game/{id}/hit` | Player draws a card | Yes |
| `POST` | `/game/{id}/stand` | Player stands; dealer plays | Yes |
| `POST` | `/game/{id}/doubledown` | Double down (initial 2 cards only) | Yes |
| `DELETE` | `/game/{id}/delete` | Delete a game record | Yes |

### 🏆 Player Rankings — `/player_ranking` (MySQL)

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `GET` | `/player_ranking` | Get global leaderboard (desc by wins) | Yes (any role) |
| `PUT` | `/player_ranking/player` | Rename a player | Yes (ADMIN only) |
| `PUT` | `/player_ranking/result` | Update ranking after a game result | Yes |

**Example: Create a new game**
```http
POST /game/new
Authorization: Basic cGxheWVyOnBsYXllcjEyMzQ=
```

**Example: Player hits**
```http
POST /game/{id}/hit
Authorization: Basic cGxheWVyOnBsYXllcjEyMzQ=
```

---

## 🔒 Security

The API uses **HTTP Basic Authentication** managed by Spring Security.

Two default users are pre-configured in memory:

| Username | Password | Roles |
|---|---|---|
| `player` | `player1234` | `ROLE_PLAYER` |
| `admin` | `admin1234` | `ROLE_ADMIN`, `ROLE_PLAYER` |

**Authorization rules:**

- `/swagger-ui/**`, `/v3/api-docs/**` → Public
- `POST /game/new` → Public (authentication resolved contextually)
- `GET /player_ranking` → Any authenticated user
- `PUT /player_ranking/player` → `ADMIN` only
- All other `/game/**` routes → Any authenticated user

> Passwords are stored using **BCrypt** hashing.

---

## 🐳 Docker Deployment

The application is fully containerized and available on Docker Hub.

**Docker Hub image:** [`urian1983/s05t01blackjackfinal`](https://hub.docker.com/r/urian1983/s05t01blackjackfinal)

### Option 1 — Docker Compose (Recommended)

Starts the application together with MySQL and MongoDB:

```bash
docker compose up --build
```

This will spin up:
- **MongoDB** on port `27017`
- **MySQL 8.0** on port `3307` (mapped from internal `3306`)
- **Blackjack App** on port `8080`

The app waits for MySQL to be healthy before starting (via `healthcheck`).

### Option 2 — Pull & Run the Pre-built Image

```bash
# Pull the latest image
docker pull urian1983/s05t01blackjackfinal:latest

# Run (requires external DB connections via environment variables)
docker run -p 8080:8080 \
  -e SPRING_DATA_MONGODB_URI=mongodb://<host>:27017/blackjack \
  -e SPRING_R2DBC_URL=r2dbc:mysql://<host>:3306/blackjack \
  -e SPRING_R2DBC_USERNAME=root \
  -e SPRING_R2DBC_PASSWORD=root \
  urian1983/s05t01blackjackfinal:latest
```

---

## 🧪 Testing

The test suite covers the Service and Controller layers with unit and slice tests.

```bash
mvn test
```

### Test Coverage

| Test Class | Scope | Framework |
|---|---|---|
| `GameControllerTest` | Controller slice test | `@WebFluxTest`, `WebTestClient`, Mockito |
| `RankingControllerTest` | Controller slice test | `@WebFluxTest`, `WebTestClient`, Mockito |
| `GameServiceImplTest` | Unit test | Mockito, `StepVerifier` |
| `RankingServiceImplTest` | Unit test | Mockito, `StepVerifier` |
| `HandTest` | Unit test | JUnit 5 |
| `DeckTest` | Unit test | JUnit 5 |
| `CardTest` | Unit test | JUnit 5 |
| `RankingTest` | Unit test | JUnit 5 |

> Controller tests use **H2 in-memory** (via R2DBC) and **Flapdoodle embedded MongoDB** to avoid external dependencies.

---
---

<a name="versión-en-español"></a>
# 🇪🇸 Versión en Español

## 📋 Tabla de Contenidos

- [Descripción del Proyecto](#descripción-del-proyecto)
- [Requisitos Clave](#requisitos-clave)
- [Stack Tecnológico](#stack-tecnológico)
- [Arquitectura](#arquitectura)
- [Instalación y Configuración](#instalación-y-configuración)
- [Endpoints de la API](#endpoints-de-la-api)
- [Seguridad](#seguridad)
- [Despliegue con Docker](#despliegue-con-docker)
- [Testing](#testing)

---

## 🎯 Descripción del Proyecto

Este proyecto es la solución oficial para el **Sprint 5 Tarea 1 (S05T01)** del currículo de Backend Java de IT Academy. Implementa un motor de juego de Blackjack completamente reactivo construido sobre **Spring Boot WebFlux**, siguiendo un paradigma no bloqueante y asíncrono en toda la pila de la aplicación.

El estado y las sesiones de juego se persisten en **MongoDB**, mientras que las estadísticas y el ranking de jugadores se almacenan en **MySQL** mediante R2DBC, formando una arquitectura de persistencia políglota.

---

## ✨ Requisitos Clave

### 🔹 Nivel 1 — Desarrollo Reactivo y Persistencia

- **Flujo Reactivo:** Implementación completa usando `Mono` y `Flux` para garantizar la ejecución no bloqueante en cada capa (Controlador → Servicio → Repositorio).
- **Persistencia Políglota:**
  - **MySQL (R2DBC):** Almacena perfiles de jugadores y clasificaciones mediante Spring Data R2DBC.
  - **MongoDB (Reactivo):** Almacena sesiones de juego activas y el estado en formato de documento mediante Spring Data MongoDB Reactive.
- **Manejo Centralizado de Errores:** Un `GlobalExceptionHandler` proporciona respuestas de error consistentes y compatibles con el paradigma reactivo para `GameNotFoundException`, `IllegalMoveException`, `NullPointerException` y excepciones genéricas.
- **Suite de Tests:** Tests unitarios e de integración para las capas de Servicio y Controlador usando JUnit 5, Mockito, `StepVerifier` y `WebTestClient`.

### 🔹 Nivel 2 — Integración DevOps

- **Contenerización:** `Dockerfile` multi-etapa para la aplicación Spring Boot.
- **Orquestación:** `docker-compose.yml` que orquesta la app, MySQL y MongoDB conjuntamente.
- **Registro en la Nube:** Imagen publicada en Docker Hub.

---

## 🛠 Stack Tecnológico

| Categoría | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.5 / Spring WebFlux |
| Librería Reactiva | Project Reactor (Mono / Flux) |
| BD Relacional | MySQL 8.0 (vía R2DBC) |
| BD Documental | MongoDB (Reactivo) |
| Seguridad | Spring Security (HTTP Basic) |
| Docs de API | SpringDoc OpenAPI / Swagger UI |
| Herramienta de Build | Maven 3.9 |
| Contenerización | Docker / Docker Compose |
| Testing | JUnit 5, Mockito, Reactor Test, WebTestClient |
| Utilidades | Lombok |

---

## 🗂 Estructura del Proyecto

```
S05T01-BlackJack/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── mvnw / mvnw.cmd
└── src/
    ├── main/
    │   ├── java/it/academy/blackjack/
    │   │   ├── BlackjackApplication.java
    │   │   ├── config/
    │   │   │   ├── MongoConfig.java
    │   │   │   ├── OpenAPIConfig.java
    │   │   │   └── R2dbcConfig.java
    │   │   ├── controller/
    │   │   │   ├── GameController.java
    │   │   │   └── RankingController.java
    │   │   ├── domain/
    │   │   │   ├── Game.java
    │   │   │   ├── enums/
    │   │   │   │   ├── GameState.java
    │   │   │   │   ├── Rank.java
    │   │   │   │   └── Suit.java
    │   │   │   └── model/
    │   │   │       ├── Card.java
    │   │   │       ├── Dealer.java
    │   │   │       ├── Deck.java
    │   │   │       ├── Hand.java
    │   │   │       ├── Player.java
    │   │   │       └── Ranking.java
    │   │   ├── dto/
    │   │   │   ├── game/
    │   │   │   │   └── GameResponseDTO.java
    │   │   │   └── ranking/
    │   │   │       ├── RankingRequestDTO.java
    │   │   │       ├── RankingResponseDTO.java
    │   │   │       └── RenamePlayerDTO.java
    │   │   ├── exceptions/
    │   │   │   ├── GameNotFoundException.java
    │   │   │   ├── GlobalExceptionHandler.java
    │   │   │   └── IllegalMoveException.java
    │   │   ├── mapper/
    │   │   │   ├── GameMapper.java
    │   │   │   ├── GameMapperImpl.java
    │   │   │   ├── RankingMapper.java
    │   │   │   ├── RankingMapperImpl.java
    │   │   │   └── RenamePlayerMapper.java
    │   │   ├── repository/
    │   │   │   ├── mongodb/
    │   │   │   │   └── GameRepository.java
    │   │   │   └── mysql/
    │   │   │       └── RankingRepository.java
    │   │   ├── security/
    │   │   │   └── SecurityConfig.java
    │   │   └── service/
    │   │       ├── mongodb/
    │   │       │   ├── GameService.java
    │   │       │   └── GameServiceImpl.java
    │   │       └── mysql/
    │   │           ├── RankingService.java
    │   │           └── RankingServiceImpl.java
    │   └── resources/
    │       ├── application.properties
    │       └── schema.sql
    └── test/
        └── java/it/academy/blackjack/
            ├── BlackjackApplicationTests.java
            ├── controller/
            │   ├── GameControllerTest.java
            │   └── RankingControllerTest.java
            ├── domain/model/
            │   ├── CardTest.java
            │   ├── DeckTest.java
            │   ├── HandTest.java
            │   └── RankingTest.java
            └── service/
                ├── mongodb/
                │   └── GameServiceImplTest.java
                └── mysql/
                    └── RankingServiceImplTest.java
```

---

## 🏗 Arquitectura

La aplicación sigue una arquitectura reactiva en capas:

```
Controlador (REST con WebFlux)
    ↓ Mono / Flux
Capa de Servicio
    ├── GameServiceImpl  →  MongoDB (ReactiveMongoRepository)
    └── RankingServiceImpl → MySQL (R2dbcRepository)
        ↓
Mapper (Entity ↔ DTO)
        ↓
GlobalExceptionHandler (respuestas de error centralizadas)
```

**Modelo de dominio del juego:**

```
Game (Documento MongoDB)
 ├── Player (nombre, Hand, stay)
 ├── Dealer extends Player (lógica shouldHit: pide carta por debajo de 17)
 ├── Deck (52 cartas únicas, barajadas)
 ├── Hand (List<Card>, calculateValue con lógica de Ases)
 └── GameState (IN_PROGRESS | PLAYER_WIN | DEALER_WIN | PLAYER_BUST | DEALER_BUST | DRAW)
```

**Reglas de Blackjack implementadas:**
- Reparto inicial estándar (2 cartas para cada uno).
- Detección de Blackjack natural en el reparto.
- Acciones del jugador: **HIT** (pedir carta), **STAND** (plantarse), **DOUBLE DOWN** (doblar).
- El crupier juega automáticamente después de que el jugador se plante (pide cartas hasta ≥ 17).
- El As vale 11 o 1 para evitar pasarse.
- El Double Down solo se permite con las 2 cartas iniciales.

---

## 🚀 Instalación y Configuración

### Requisitos Previos

- Java 21+
- Maven 3.9+
- Docker y Docker Compose (para configuración contenerizada)
- Una instancia de MySQL 8.0 en ejecución en el puerto `3306` (o `3307` para uso local)
- Una instancia de MongoDB en ejecución en el puerto `27017`

### Ejecución Local (Manual)

1. **Clonar el repositorio:**
   ```bash
   git clone <url-del-repositorio>
   cd S05T01-BlackJack
   ```

2. **Configurar las conexiones a bases de datos** en `src/main/resources/application.properties`:
   ```properties
   # MongoDB
   spring.data.mongodb.uri=mongodb://localhost:27017/blackjack

   # MySQL (R2DBC)
   spring.r2dbc.url=r2dbc:mysql://localhost:3306/blackjack
   spring.r2dbc.username=root
   spring.r2dbc.password=root
   ```
   > El esquema (tabla `player_ranking`) se crea automáticamente al arrancar la aplicación mediante `schema.sql`.

3. **Compilar y ejecutar:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Acceder a Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 📡 Endpoints de la API

### 🃏 Gestión del Juego — `/game` (MongoDB)

| Método | Endpoint | Descripción | Auth Requerida |
|---|---|---|---|
| `POST` | `/game/new` | Crear una nueva sesión de juego | Sí (cualquier rol) |
| `GET` | `/game/{id}` | Obtener el estado de una partida por ID | Sí |
| `POST` | `/game/{id}/hit` | El jugador pide una carta | Sí |
| `POST` | `/game/{id}/stand` | El jugador se planta; el crupier juega | Sí |
| `POST` | `/game/{id}/doubledown` | Doblar (solo con las 2 cartas iniciales) | Sí |
| `DELETE` | `/game/{id}/delete` | Eliminar un registro de partida | Sí |

### 🏆 Ranking de Jugadores — `/player_ranking` (MySQL)

| Método | Endpoint | Descripción | Auth Requerida |
|---|---|---|---|
| `GET` | `/player_ranking` | Obtener la clasificación global (desc. por victorias) | Sí (cualquier rol) |
| `PUT` | `/player_ranking/player` | Renombrar un jugador | Sí (solo ADMIN) |
| `PUT` | `/player_ranking/result` | Actualizar el ranking tras el resultado de una partida | Sí |

---

## 🔒 Seguridad

La API utiliza **Autenticación HTTP Basic** gestionada por Spring Security.

Dos usuarios por defecto están preconfigurados en memoria:

| Usuario | Contraseña | Roles |
|---|---|---|
| `player` | `player1234` | `ROLE_PLAYER` |
| `admin` | `admin1234` | `ROLE_ADMIN`, `ROLE_PLAYER` |

**Reglas de autorización:**

- `/swagger-ui/**`, `/v3/api-docs/**` → Público
- `POST /game/new` → Público (la autenticación se resuelve contextualmente)
- `GET /player_ranking` → Cualquier usuario autenticado
- `PUT /player_ranking/player` → Solo `ADMIN`
- Resto de rutas `/game/**` → Cualquier usuario autenticado

> Las contraseñas se almacenan con hash **BCrypt**.

---

## 🐳 Despliegue con Docker

La aplicación está completamente contenerizada y disponible en Docker Hub.

**Imagen en Docker Hub:** [`urian1983/s05t01blackjackfinal`](https://hub.docker.com/r/urian1983/s05t01blackjackfinal)

### Opción 1 — Docker Compose (Recomendado)

Inicia la aplicación junto con MySQL y MongoDB:

```bash
docker compose up --build
```

Esto levantará:
- **MongoDB** en el puerto `27017`
- **MySQL 8.0** en el puerto `3307` (mapeado desde el `3306` interno)
- **Aplicación Blackjack** en el puerto `8080`

La app espera a que MySQL esté sano antes de arrancar (mediante `healthcheck`).

### Opción 2 — Descargar y Ejecutar la Imagen Precompilada

```bash
# Descargar la imagen
docker pull urian1983/s05t01blackjackfinal:latest

# Ejecutar (requiere conexiones a BD externas via variables de entorno)
docker run -p 8080:8080 \
  -e SPRING_DATA_MONGODB_URI=mongodb://<host>:27017/blackjack \
  -e SPRING_R2DBC_URL=r2dbc:mysql://<host>:3306/blackjack \
  -e SPRING_R2DBC_USERNAME=root \
  -e SPRING_R2DBC_PASSWORD=root \
  urian1983/s05t01blackjackfinal:latest
```

---

## 🧪 Testing

La suite de tests cubre las capas de Servicio y Controlador con tests unitarios y de slice.

```bash
mvn test
```

### Cobertura de Tests

| Clase de Test | Alcance | Framework |
|---|---|---|
| `GameControllerTest` | Test de slice de controlador | `@WebFluxTest`, `WebTestClient`, Mockito |
| `RankingControllerTest` | Test de slice de controlador | `@WebFluxTest`, `WebTestClient`, Mockito |
| `GameServiceImplTest` | Test unitario | Mockito, `StepVerifier` |
| `RankingServiceImplTest` | Test unitario | Mockito, `StepVerifier` |
| `HandTest` | Test unitario | JUnit 5 |
| `DeckTest` | Test unitario | JUnit 5 |
| `CardTest` | Test unitario | JUnit 5 |
| `RankingTest` | Test unitario | JUnit 5 |

> Los tests de controlador utilizan **H2 en memoria** (vía R2DBC) y **Flapdoodle MongoDB embebido** para evitar dependencias externas.
