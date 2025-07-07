# Sales Analytics Backend

A Spring Boot backend application for the Advanced Real-Time Sales Analytics System that provides real-time sales data, analytics, and AI-powered recommendations.

## Features

- **RESTful APIs** for orders, analytics, and recommendations
- **Real-time WebSocket** communication for live updates
- **MySQL Database** with JPA/Hibernate
- **AI-powered recommendations** based on sales data
- **CORS support** for frontend integration
- **Comprehensive analytics** including revenue trends and top products
- **Lombok integration** for cleaner code
- **Java 21** support

## Technology Stack

- **Java 21**
- **Spring Boot 3.5.3**
- **MySQL 8.x**
- **Lombok 1.18.32**
- **JPA/Hibernate**
- **WebSocket**
- **Maven**

## API Endpoints

### Orders
- `POST /api/orders` - Create a new order
- Request body:
```json
{
  "productName": "Laptop",
  "quantity": 3,
  "price": 500.0,
  "date": "2025-01-05T19:00:00"
}
```

### Analytics
- `GET /api/analytics` - Get real-time sales analytics
- Response includes:
  - Total revenue
  - Total orders
  - Top products by sales
  - Recent orders
  - Revenue change percentage
  - Orders in last minute

### Recommendations
- `GET /api/recommendations` - Get AI-powered recommendations
- Returns strategic suggestions based on current sales data

### WebSocket
- `ws://localhost:8080/ws` - Real-time updates for:
  - New orders
  - Analytics updates
  - Connection status

## Database Setup

1. **Create MySQL Database**
   ```sql
   CREATE DATABASE sales_db;
   ```

2. **Update Database Configuration**
   The application is configured to connect to MySQL with these settings:
   - URL: `jdbc:mysql://localhost:3306/sales_db`
   - Username: `root`
   - Password: `root`
   
   Update `application.properties` if your MySQL configuration is different.

## Quick Start

1. **Prerequisites**
   - Java 21 or higher
   - Maven 3.6 or higher
   - MySQL 8.x running on localhost:3306

2. **Setup Database**
   ```bash
   mysql -u root -p
   CREATE DATABASE sales_db;
   ```

3. **Run the application**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

4. **Access the application**
   - API: http://localhost:8080/api
   - Actuator: http://localhost:8080/actuator
   - WebSocket: ws://localhost:8080/ws

## Database Schema

The application will automatically create the following table structure:

```sql
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    price DOUBLE NOT NULL,
    date DATETIME NOT NULL,
    created_at DATETIME
);
```

Sample data will be inserted automatically on first run.

## Architecture

- **Controllers**: Handle HTTP requests and responses
- **Services**: Business logic and data processing
- **Repositories**: Data access layer using Spring Data JPA
- **DTOs**: Data transfer objects for API communication (with Lombok)
- **Entities**: JPA entities (with Lombok)
- **WebSocket**: Real-time communication handler
- **Configuration**: CORS and WebSocket configuration

## AI Integration

The recommendation service generates intelligent suggestions based on:
- Sales performance data
- Product popularity trends
- Revenue patterns
- Simulated weather conditions for seasonal recommendations

## Development Notes

- **Lombok**: Used for reducing boilerplate code (getters, setters, constructors)
- **MySQL**: Production-ready database with proper indexing
- **JPA Queries**: Manual JPQL queries for better control
- **Real-time functionality**: Implemented with WebSockets
- **CORS configured** for frontend development servers
- **Comprehensive error handling** and logging
- **Sample data included** for immediate testing

## Testing

The application includes sample data and can be tested immediately after startup. Use tools like Postman or curl to test the API endpoints, or connect the Vue.js frontend for full functionality.

## Logging

The application uses SLF4J with Logback for logging:
- DEBUG level for com.example package
- INFO level for general application logs
- ERROR level for exceptions and errors

## Actuator Endpoints

Health and monitoring endpoints available at:
- `/actuator/health` - Application health status
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics