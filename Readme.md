# Currency Rate Service

## Project Description

The **Currency Rate Service** is a Spring Boot-based application designed to fetch, store, and manage currency exchange rates. The application fetches rates for fiat and cryptocurrency from an external API, stores them in a PostgreSQL database, and exposes these rates through RESTful APIs.

### Features:
- Fetches real-time currency exchange rates for fiat and cryptocurrency.
- Stores fetched data in a PostgreSQL database.
- Exposes API endpoints to retrieve the latest rates.

## Technologies Used

- **Spring Boot**: For managing application beans and context.
- **Spring WebFlux**: For reactive programming and handling HTTP requests asynchronously.
- **Spring Data R2dbc**: For reactive sql database connectivity
- **PostgreSQL**: Relational database to store currency rates.
- **Logback**: For logging configuration.
- **JUnit**: For unit testing the application.
- **Testcontainers**: For persistence layer integration testing.
- **MockWebServer**: For external api cal integration testing.
- **Docker**: For containerization of the app and database.

## Running the Application

Follow these steps to run the application in a Docker container.

#### 1. Clone the Repository

```bash
git clone https://github.com/SpaceUrgent/currency-rate-api.git
```

#### 2. Switch to project directory

```bash
cd currency-rate-service
```

#### 3. Run docker compose

```bash
docker-compose up
```

#### 4. Send GET /currency-rates

```bash
curl http://localhost:8080/currency-rates
```