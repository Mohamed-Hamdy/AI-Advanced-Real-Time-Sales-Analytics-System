# AI Advanced Real-Time Sales Analytics System

##  Overview

This project implements an advanced real-time sales analytics system. It supports order management, live revenue tracking, and intelligent product recommendations using AI systems like ChatGPT/Gemini. The system is built with a focus on manual implementation of real-time features and data visualization, ensuring minimal reliance on prebuilt frameworks.

<img src="https://raw.githubusercontent.com/Mohamed-Hamdy/AI-Advanced-Real-Time-Sales-Analytics-System/refs/heads/master/images/Peoject%20preview.png">

##  Features

- **Order Management**
  - Add new orders with product ID, quantity, price, and date.
  
- **Real-Time Analytics**
  - Track total revenue
  - Top-selling products
  - Revenue changes in the last minute
  - Recent order counts
  - All data updates in real-time via WebSocket

- **AI Recommendations**
  - Integrated with OpenAI or Gemini
  - Suggests promotions for products
  - Provides text-based insights on sales trends

- **External API Integration**
  - Integrates with a weather API (e.g., OpenWeather)
  - Suggests dynamic pricing and product promotions based on weather

- **Manual Implementation Highlights**
  - Raw SQL queries (no ORM)
  - Custom WebSocket events for updates
  - Charts drawn manually (no charting libraries)


##  API Endpoints

| Method | Endpoint            | Description                                   |
|--------|---------------------|-----------------------------------------------|
| POST   | `/api/orders`       | Add a new order                               |
| GET    | `/api/analytics`    | Fetch real-time sales analytics               |
| GET    | `/api/recommendations` | Get product recommendations from AI and weather |

### Sample Request — Add Order

```json
POST /api/orders
{
  "productId": 123,
  "quantity": 2,
  "price": 19.99,
  "date": "2025-07-04T19:45:00"
}
```

### Sample Response — Analytics

```json
{
  "totalRevenue": 10000,
  "topProducts": ["product1", "product2"],
  "recentRevenue": 150,
  "recentOrders": 5
}
```

### Sample Response — Recommendations

```json
{
  "aiSuggestion": "Promote product XYZ for higher sales",
  "weatherSuggestion": "Offer cold drinks today due to hot weather"
}
```

##  Architecture

The system architecture is structured as follows:

- **Frontend**
  - Built with plain JavaScript, HTML, and HTML5 Canvas (no charting libraries)
  - Real-time dashboards for displaying:
    - Total revenue
    - Top-selling products
    - Revenue trends
    - New orders in the last minute
  - Forms to add new orders
  - Uses WebSocket connections to receive live updates on analytics and new orders

- **Backend**
  - Developed using Spring Boot (Java)
  - Provides RESTful APIs for:
    - Adding orders
    - Fetching analytics
    - Retrieving AI-powered recommendations
  - Integrates with:
    - OpenAI/Gemini for product recommendation suggestions
    - Weather API (e.g., OpenWeather) to tailor product promotions based on climate
  - Stores data in a lightweight SQLite database with raw SQL queries (no ORM)
  - Broadcasts real-time updates to clients over WebSocket

- **Real-Time Flow**
  - When a new order is added via the API:
    - The backend stores it in SQLite
    - It broadcasts the new order and refreshed analytics through WebSocket
  - The frontend listens for these WebSocket messages to instantly update the UI

- **AI and Weather Integration**
  - Periodically sends recent sales data to an AI system
  - Requests product promotions and strategy recommendations
  - Adjusts suggestions dynamically based on current weather conditions

## System Requirements

### Backend (Java)
- Java 21 JDK
- Maven 3.9+
- DeepSeek API key
- MySql DataBase
  
### Frontend (JavaScript)
- Node.js 18.20.8
- npm 9+ or yarn 1.22+
- Vite 6.3+
