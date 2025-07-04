# Personal Finance Manager

A comprehensive personal finance management system built with Spring Boot 3.2, providing robust user authentication, transaction management, category management, savings goals, and detailed financial reporting.

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Setup and Installation](#setup-and-installation)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Database Schema](#database-schema)
- [Security](#security)
- [Error Handling](#error-handling)

## Features

### 1. User Management and Authentication
- **Registration**: Users can register with email, password, full name, and phone number
- **Login**: Session-based authentication using username/email and password
- **Session Management**: Secure session-based authentication with HTTP-only cookies
- **Logout**: Proper session invalidation and cleanup
- **Data Isolation**: Complete data segregation between user accounts

### 2. Transaction Management
- **Create Transaction**: Add financial transactions with amount, date, category, and description
- **Read Transactions**: View all transactions sorted by newest first with filtering capabilities
- **Update Transaction**: Modify any transaction field except the date field
- **Delete Transaction**: Remove transactions (excluded from savings goals calculations)
- **Filtering**: Filter transactions by date range, category, and transaction type

### 3. Category Management
- **Default Categories**: 
  - Income: Salary
  - Expense: Food, Rent, Transportation, Entertainment, Healthcare, Utilities
- **Custom Categories**: Users can create custom income and expense categories
- **Category Validation**: All transactions must reference valid, non-deleted categories
- **Category Protection**: Categories referenced by transactions cannot be deleted

### 4. Savings Goals
- **Create Goal**: Set savings goals with name, target amount, and target date
- **Progress Tracking**: Automatic calculation of progress based on (Total Income - Total Expenses) since goal start date
- **Goal Management**: View, update target amount/date, and delete goals as required
- **Progress Display**: Shows percentage completion and remaining amount

### 5. Reports and Analytics
- **Monthly Reports**: Income by category, expenses by category, and net savings for specified month/year
- **Yearly Reports**: Aggregate monthly data for comprehensive financial overview
- **Category Breakdown**: Detailed breakdown of income and expenses by category

## Technology Stack

| Component | Technology |
|-----------|------------|
| Programming Language | Java 17+ |
| Framework | Spring Boot 3.2 |
| Security | Spring Security (Session-based) |
| Database | H2 (In-memory) |
| Testing Framework | JUnit 5, Mockito |
| Build Tool | Maven |
| Validation | Jakarta Bean Validation |

## Architecture

The application follows a layered architecture pattern:

- **Controller Layer**: REST endpoints for API exposure
- **Service Layer**: Business logic implementation
- **Repository Layer**: Data access abstraction
- **Entity Layer**: JPA entities with proper relationships
- **DTO Layer**: Data transfer objects for API communication
- **Exception Handling**: Global exception handler with appropriate HTTP status codes
- **Configuration**: External configuration via application.properties

## Setup and Installation

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd personal-finance-manager
   ```

2. **Build the application**
   ```bash
   mvn clean compile
   ```

3. **Run tests**
   ```bash
   mvn test
   ```

4. **Start the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - Base URL: `http://localhost:8080/api`
   - H2 Console: `http://localhost:8080/api/h2-console`

### H2 Database Configuration
- **URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: (empty)

## API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "phoneNumber": "+1234567890"
}
```

**Response** (201 Created):
```json
{
  "message": "User registered successfully",
  "userId": 1
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123"
}
```

**Response** (200 OK):
```json
{
  "message": "Login successful"
}
```

#### Logout
```http
POST /api/auth/logout
```

**Response** (200 OK):
```json
{
  "message": "Logout successful"
}
```

### Transaction Endpoints

#### Create Transaction
```http
POST /api/transactions
Content-Type: application/json

{
  "amount": 50000.00,
  "date": "2024-01-15",
  "category": "Salary",
  "description": "January Salary"
}
```

#### Get Transactions
```http
GET /api/transactions
GET /api/transactions?startDate=2024-01-01&endDate=2024-01-31&category=Salary
```

#### Update Transaction
```http
PUT /api/transactions/{id}
Content-Type: application/json

{
  "amount": 60000.00,
  "description": "Updated January Salary"
}
```

#### Delete Transaction
```http
DELETE /api/transactions/{id}
```

### Category Endpoints

#### Get All Categories
```http
GET /api/categories
```

#### Create Custom Category
```http
POST /api/categories
Content-Type: application/json

{
  "name": "SideBusinessIncome",
  "type": "INCOME"
}
```

#### Delete Custom Category
```http
DELETE /api/categories/{name}
```

### Savings Goals Endpoints

#### Create Goal
```http
POST /api/goals
Content-Type: application/json

{
  "goalName": "Emergency Fund",
  "targetAmount": 5000.00,
  "targetDate": "2026-01-01"
}
```

#### Get All Goals
```http
GET /api/goals
```

#### Get Specific Goal
```http
GET /api/goals/{id}
```

#### Update Goal
```http
PUT /api/goals/{id}
Content-Type: application/json

{
  "targetAmount": 6000.00,
  "targetDate": "2026-02-01"
}
```

#### Delete Goal
```http
DELETE /api/goals/{id}
```

### Reports Endpoints

#### Monthly Report
```http
GET /api/reports/monthly/{year}/{month}
```

**Example**: `GET /api/reports/monthly/2024/1`

#### Yearly Report
```http
GET /api/reports/yearly/{year}
```

**Example**: `GET /api/reports/yearly/2024`

## Testing

The application includes comprehensive testing with 80%+ code coverage:

### Test Categories
- **Unit Tests**: Service layer business logic testing
- **Integration Tests**: Full application flow testing
- **Repository Tests**: Database interaction testing
- **Controller Tests**: REST endpoint testing

### Running Tests
```bash
# Run all tests
mvn test

# Run with coverage report
mvn test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### Test Coverage Requirements
- Minimum 80% code coverage
- All critical business logic covered
- Integration tests for complete user workflows
- Repository tests for data access operations

## Database Schema

### Users Table
- `id` (Primary Key)
- `username` (Unique, Email)
- `password` (Encrypted)
- `full_name`
- `phone_number` (Unique)
- `created_at`

### Categories Table
- `id` (Primary Key)
- `name`
- `type` (INCOME/EXPENSE)
- `is_custom` (Boolean)
- `user_id` (Foreign Key, nullable for default categories)

### Transactions Table
- `id` (Primary Key)
- `amount` (Decimal)
- `date`
- `description`
- `category_id` (Foreign Key)
- `user_id` (Foreign Key)
- `created_at`

### Savings Goals Table
- `id` (Primary Key)
- `goal_name`
- `target_amount` (Decimal)
- `target_date`
- `start_date`
- `current_progress` (Decimal)
- `progress_percentage` (Decimal)
- `remaining_amount` (Decimal)
- `user_id` (Foreign Key)
- `created_at`

## Security

### Authentication
- Session-based authentication using Spring Security
- Secure session cookies with HTTP-only flag
- Password encryption using BCrypt
- Session timeout: 30 minutes

### Authorization
- All API endpoints except registration and login require authentication
- Data isolation ensures users can only access their own data
- Category access control (default + user's custom categories)

### Input Validation
- Comprehensive input validation using Jakarta Bean Validation
- Email format validation for usernames
- Phone number format validation
- Date validation (no future dates for transactions)
- Amount validation (positive values only)

## Error Handling

### HTTP Status Codes

| Status Code | Description | Example Scenarios |
|-------------|-------------|-------------------|
| 200 OK | Success | Login successful, data retrieved |
| 201 Created | Resource created | User registered, transaction created |
| 400 Bad Request | Validation errors | Malformed input, validation failures |
| 401 Unauthorized | Invalid credentials | Wrong password, expired session |
| 403 Forbidden | Access denied | Accessing other user's data |
| 404 Not Found | Resource not found | Non-existent transaction/goal |
| 409 Conflict | Duplicate resource | Email/phone already exists |

### Error Response Format
```json
{
  "error": "Error message describing the issue"
}
```

### Validation Error Response
```json
{
  "fieldName": "Error message for specific field",
  "anotherField": "Another validation error"
}
```

## Deployment

### Local Development
The application is configured for local development with H2 in-memory database and detailed logging.

### Production Considerations
For production deployment:
1. Replace H2 with a persistent database (PostgreSQL, MySQL)
2. Configure external database properties
3. Set up proper logging configuration
4. Configure HTTPS and secure session cookies
5. Set up monitoring and health checks

## License

This project is developed as part of an academic assignment and is intended for educational purposes.

## Contact

For questions or support, please contact the development team.
#   s p r i n g - b o o t - f i n a n c e - m - s  
 