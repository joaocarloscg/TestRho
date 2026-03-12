# Exchange Rate API

Spring Boot API for retrieving exchange rates and performing currency conversions.

## Features

- Get exchange rate from one currency to another
- Get all exchange rates from a base currency
- Convert an amount from one currency to another
- Convert an amount from one currency to multiple currencies
- OpenAPI / Swagger documentation
- In-memory caching with 1-minute TTL
- Unit and integration tests
- Dockerized setup

## Tech Stack

- Java 21
- Spring Boot
- Maven
- Spring Web
- Spring Cache
- Caffeine
- Swagger
- JUnit 5 / Mockito / MockMvc
- Docker

## Configuration

This application requires an API key for the external exchange rate provider.

Set the following environment variable before starting the application:

- `EXCHANGE_RATE_PROVIDER_ACCESS_KEY`

# Running the application

## Run locally

After setting the required environment variable:

```bash
./mvnw spring-boot:run
```

Application will start at:

`http://localhost:8080`

## Build Docker image

```bash
docker build -t exchange-rate-api .
```

## Run container

```bash
docker run -p 8080:8080 \
  -e EXCHANGE_RATE_PROVIDER_ACCESS_KEY=your_api_key_here \
  exchange-rate-api
```

Application will be available at:

`http://localhost:8080`

## Run with Docker Compose

Make sure `EXCHANGE_RATE_PROVIDER_ACCESS_KEY` is set in your environment or in a local `.env` file before running:

```bash
docker compose up --build
```

# Swagger UI

API documentation is available at:

`http://localhost:8080/swagger-ui/index.html`

# Example Endpoints

## Get all exchange rates from USD

`GET /api/v1/exchange-rates/USD`

Example response:

```json
{
  "base": "USD",
  "rates": {
    "EUR": 0.92,
    "GBP": 0.78,
    "JPY": 150.10
  }
}
```

## Get exchange rate from USD to EUR

`GET /api/v1/exchange-rates/USD/EUR`

Example response:

```json
{
  "from": "USD",
  "to": "EUR",
  "rate": 0.92
}
```

## Convert 100 USD to EUR

`GET /api/v1/exchange-rates/convert/USD/EUR?amount=100`

Example response:

```json
{
  "from": "USD",
  "to": "EUR",
  "originalAmount": 100,
  "rate": 0.92,
  "convertedAmount": 92.000000
}
```

## Convert 100 USD to multiple currencies

`GET /api/v1/exchange-rates/convert/USD?amount=100&to=EUR&to=GBP&to=JPY`

Example response:

```json
{
  "from": "USD",
  "originalAmount": 100,
  "conversions": [
    {
      "currency": "EUR",
      "rate": 0.92,
      "convertedAmount": 92.000000
    },
    {
      "currency": "GBP",
      "rate": 0.78,
      "convertedAmount": 78.000000
    },
    {
      "to": "JPY",
      "rate": 149.50,
      "convertedAmount": 14950.000000
    }
  ]
}
```

# Caching Strategy

To minimize calls to the external provider, exchange rates are cached in-memory for **1 minute** using **Caffeine**.

This matches the challenge requirement that clients can tolerate data with up to **1 minute delay**.

Caching occurs at the **service layer**, ensuring:

- minimal external API calls
- consistent responses across endpoints
- improved response times

# Design Notes

Key design decisions:

- **Layered architecture** separating controller, service, and client
- **Custom exception hierarchy** for consistent API errors
- **Centralized exception handling** using `@RestControllerAdvice`
- **Caffeine caching** to reduce external provider calls
- **Environment-based configuration** for container deployment
- **Swagger/OpenAPI documentation** for discoverability
- **Test coverage across layers**