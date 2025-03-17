# Polygon API - Stock Price Service

A Spring Boot application that provides a RESTful API for fetching, storing, and retrieving stock price data from the Polygon.io API.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
    - [Environment Variables](#environment-variables)
    - [Building the Application](#building-the-application)
    - [Running with Docker](#running-with-docker)
    - [Running Locally](#running-locally)
- [API Documentation](#api-documentation)
    - [Swagger UI](#swagger-ui)
    - [Endpoints](#endpoints)
    - [Request/Response Examples](#requestresponse-examples)
- [Testing](#testing)
    - [Running Unit Tests](#running-unit-tests)
    - [Running Integration Tests](#running-integration-tests)
    - [Test Coverage](#test-coverage)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

## Overview
The Polygon API application provides a service for fetching stock price data from the Polygon.io API, storing it in a database, and exposing endpoints to retrieve this data. It includes features like caching, error handling, and comprehensive API documentation.

## Features
- Fetch stock price data for a specific company symbol within a date range
- Store stock price data in a MySQL database
- Retrieve stock price data by company symbol and date
- Caching mechanism to improve performance
- Comprehensive error handling
- API documentation with Swagger UI
- Containerization with Docker

## Technology Stack
- Java 17
- Spring Boot 3.x
- Spring Data JPA
- MySQL (Production)
- H2 Database (Testing)
- WireMock (API Testing)
- Swagger/OpenAPI (API Documentation)
- Docker & Docker Compose
- Maven
- JUnit 5 & Mockito

## Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- Docker and Docker Compose (for containerized deployment)
- Polygon.io API key (sign up at polygon.io)

## Getting Started

### Environment Variables
Create a .env file in the root directory with the following variables:
```bash
# Database Configuration
MYSQL_DATABASE=polygon_db
MYSQL_USER=polygon_user
MYSQL_PASSWORD=your_password
MYSQL_ROOT_PASSWORD=your_root_password

# API Configuration
POLYGON_API_KEY=your_polygon_api_key

# Spring Profile
SPRING_PROFILES_ACTIVE=prod
```

### Building the Application
```bash
# Clone the repository
git clone https://github.com/FelipeMorandini/polygon-api.git
cd polygon-api

# Build the application
./mvnw clean package
```

### Running with Docker
The easiest way to run the application is using Docker Compose:
```bash
# Start the application and database
docker-compose up -d

# Check the logs
docker-compose logs -f stock-price-service

# Stop the application
docker-compose down
```

### Running Locally
To run the application locally without Docker:
```bash
# Make sure you have MySQL running locally or update the application.properties
# with the correct database connection details

# Set environment variables
export POLYGON_API_KEY=your_polygon_api_key
export MYSQL_DATABASE=polygon_db
export MYSQL_USER=your_user
export MYSQL_PASSWORD=your_password

# Run the application
./mvn spring-boot:run
```

## API Documentation

### Swagger UI
The application includes Swagger UI for interactive API documentation. Once the application is running, access the Swagger UI at:

http://localhost:8080/swagger-ui.html

### Endpoints
| Method | Endpoint | Description |
| ------ | -------- | ----------- |
| GET | /api/v1/stocks/fetch | Fetch and save stock prices for a given company symbol and date range |
| GET | /api/v1/stocks/{symbol} | Get stock price for a specific company symbol and date |

### Request/Response Examples

#### Fetch and Save Stock Prices
Request:

```http
GET /api/v1/stocks/fetch?companySymbol=AAPL&fromDate=2023-01-01&toDate=2023-01-31&page=0&size=20
```

Response:

```json
{
  "content": [
    {
      "symbol": "AAPL",
      "date": "2023-01-01",
      "openPrice": 173.97,
      "closePrice": 173.57,
      "highPrice": 174.3,
      "lowPrice": 173.12,
      "volume": 77287356
    },
    {
      "symbol": "AAPL",
      "date": "2023-01-02",
      "openPrice": 174.5,
      "closePrice": 176.2,
      "highPrice": 176.8,
      "lowPrice": 174.1,
      "volume": 80123456
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 2,
  "totalPages": 1,
  "last": true,
  "size": 20,
  "number": 0,
  "sort": {
    "empty": true,
    "sorted": false,
    "unsorted": true
  },
  "numberOfElements": 2,
  "first": true,
  "empty": false
}
```

#### Get Stock Price by Symbol and Date
Request:

```http
GET /api/v1/stocks/AAPL?date=2023-01-15
```

Response:

```json
{
  "symbol": "AAPL",
  "date": "2023-01-15",
  "openPrice": 173.97,
  "closePrice": 173.57,
  "highPrice": 174.3,
  "lowPrice": 173.12,
  "volume": 77287356
}
```

### Testing
The application includes comprehensive unit and integration tests.

#### Running Unit Tests
```bash
./mvnw test
```

#### Running Integration Tests
```bash
./mvnw test -Dtest=*IntegrationTest
```

#### Test Coverage
To generate a test coverage report:

```bash
./mvnw verify
```

The coverage report will be available at `target/site/jacoco/index.html`.

### Project Structure


If you prefer a different style or want to ensure the file tree renders correctly, here's an alternative approach using simple indentation:


### Project Structure

**polygon-api/**
- **src/**
  - **main/**
    - **java/com/leadiq/polygonapi/**
      - **config/** - Configuration classes
      - **controller/** - REST controllers
      - **dto/** - Data Transfer Objects
      - **entity/** - JPA entities
      - **exception/** - Custom exceptions and error handling
      - **repository/** - Spring Data repositories
      - **service/** - Business logic services
      - **PolygonApiApplication.java** - Main application class
    - **resources/**
      - **application.properties** - Application configuration
      - ...
  - **test/**
    - **java/com/leadiq/polygonapi/**
      - **controller/** - Controller tests
      - **integration/** - Integration tests
      - **repository/** - Repository tests
      - **service/** - Service tests
      - **PolygonApiApplicationTests.java**
    - **resources/**
      - **application-test.properties** - Test configuration
- **.env** - Environment variables (create this)
- **docker-compose.yml** - Docker Compose configuration
- **Dockerfile** - Docker build configuration
- **mvnw** - Maven wrapper script
- **mvnw.cmd** - Maven wrapper script for Windows
- **pom.xml** - Maven project configuration
- **README.md** - This file

### Configuration

#### Application Properties
The main configuration is in `src/main/resources/application.properties`:

```properties
spring.application.name=polygon-api

spring.datasource.url=jdbc:mysql://localhost:3306/${MYSQL_DATABASE}?createDatabaseIfNotExist=true&serverTimezone=UTC
spring.datasource.username=${MYSQL_USER}
spring.datasource.password=${MYSQL_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Polygon config
polygon.base.url=https://api.polygon.io/v2/aggs/ticker
polygon.api.key=${POLYGON_API_KEY}

# Server port
server.port=8080

# OpenAPI Documentation Configuration
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.doc-expansion=none
springdoc.swagger-ui.disable-swagger-default-url=true
springdoc.swagger-ui.display-request-duration=true
springdoc.default-produces-media-type=application/json
springdoc.default-consumes-media-type=application/json
```

#### Test Properties
Test configuration is in `src/test/resources/application-test.properties`:

```properties
# H2 In-memory database configuration for tests
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver

# Hibernate configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# Disable Polygon API for tests
polygon.base.url=https://api.polygon.io/v2/aggs/ticker
polygon.api.key=${POLYGON_API_KEY}
```

### Troubleshooting

#### Common Issues

**Database Connection Issues**
- Ensure MySQL is running and accessible
- Check database credentials in `.env` file
- Verify network connectivity if using Docker

**Polygon API Issues**
- Verify your Polygon API key is valid
- Check for rate limiting or subscription issues
- Ensure internet connectivity

**Docker Issues**
- Ensure Docker and Docker Compose are installed and running
- Check Docker logs: `docker-compose logs -f`
- Verify port mappings are not conflicting with existing services

#### Logs
Application logs are available:
- When running with Docker: `docker-compose logs -f stock-price-service`
- When running locally: Check the console output or `./logs` directory

### Contributing
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -am 'Add my feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Submit a pull request

### License
This project is licensed under the Apache License 2.0 - see the LICENSE file for details.
