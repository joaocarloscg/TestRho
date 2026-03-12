# Exchange Rate API

Spring Boot API for retrieving exchange rates and performing currency conversions.

## Features

- Get exchange rate from one currency to another
- Get all exchange rates from a base currency
- Convert an amount from one currency to another
- Convert an amount from one currency to multiple currencies
- OpenAPI / Swagger documentation
- In-memory caching with 1-minute TTL configured in the application.yaml
- Unit and integration tests
- Dockerized setup
- GraphQL
- API key authentication and authorization

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
- `AUTH_DEFAULT_API_KEY`

Optional authentication settings:

- `AUTH_ENABLED` (defaults to `true`)
- `AUTH_HEADER_NAME` (defaults to `X-API-Key`)

# Running the application

## Run locally

After setting the required environment variable:

```bash
EXCHANGE_RATE_PROVIDER_ACCESS_KEY=your_provider_api_key_here
AUTH_DEFAULT_API_KEY=your_internal_api_key_here

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
  -e EXCHANGE_RATE_PROVIDER_ACCESS_KEY=your_provider_api_key_here \
  -e AUTH_DEFAULT_API_KEY=your_internal_api_key_here \
  exchange-rate-api
```

Application will be available at:

`http://localhost:8080`

## Run with Docker Compose

Make sure `EXCHANGE_RATE_PROVIDER_ACCESS_KEY` and `AUTH_DEFAULT_API_KEY` are set in your environment or in a local `.env` file before running:

```bash
docker compose up --build
```

# GraphQL

GraphQL is available alongside the existing REST API at:

`http://localhost:8080/graphql`

GraphiQL is enabled for local exploration at:

`http://localhost:8080/graphiql`

Example query:

```graphql
query {
  exchangeRates(base: "USD", currencies: ["EUR", "JPY"]) {
    base
    rates {
      currency
      rate
    }
  }
}
```

Example conversion query:

```graphql
query {
  convert(from: "USD", to: "EUR", amount: 100) {
    from
    to
    rate
    convertedAmount
  }
}
```

# Authentication and Authorization

Protected REST and GraphQL endpoints now require an API key sent in the `X-API-Key` header.

Default authorization model:

- `SCOPE_rates.read` for REST endpoints under `/api/v1/rates/**`
- `SCOPE_graphql.read` for `/graphql`

Public endpoints:

- `/actuator/health`
- `/actuator/info`
- `/swagger-ui/**`
- `/v3/api-docs/**`
- `/graphiql`

Example REST request:

```bash
curl -H "X-API-Key: $AUTH_DEFAULT_API_KEY" \
  http://localhost:8080/api/v1/rates/USD/EUR
```

Example GraphQL request:

```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "X-API-Key: $AUTH_DEFAULT_API_KEY" \
  -d '{"query":"{ exchangeRate(from: \"USD\", to: \"EUR\") { from to rate } }"}'
```

If you need more than one client, define additional `auth.clients[n]` entries with their own API keys and authorities.

# Swagger UI

API documentation is available at:

`http://localhost:8080/swagger-ui/index.html`

# Example Endpoints

## Get all exchange rates from USD

`GET /api/v1/rates/USD`

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

`GET /api/v1/rates/USD/EUR`

Example response:

```json
{
  "from": "USD",
  "to": "EUR",
  "rate": 0.92
}
```

## Convert 100 USD to EUR

`GET /api/v1/conversions/USD/EUR?amount=100`

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

`GET /api/v1/conversions/USD?amount=100&to=EUR&to=GBP&to=JPY`

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