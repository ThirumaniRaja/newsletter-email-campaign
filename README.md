# Newsletter Email Campaign Manager API

A secure backend API for managing mailing lists, creating email campaigns, and scheduling email sends with comprehensive JWT authentication and role-based access control.

## Features

### 1. Authentication
- ✅ User registration and login using JWT (JSON Web Tokens)
- ✅ Password encryption using BCrypt
- ✅ Token-based security for all endpoints
- ✅ Automatic token validation and user context management

### 2. Mailing List Management
- ✅ Create and manage mailing lists
- ✅ Add/remove subscribers with email validation
- ✅ View detailed mailing list information
- ✅ Prevent duplicate emails within the same list
- ✅ Subscriber count tracking

### 3. Campaign Management
- ✅ Create campaigns in DRAFT status
- ✅ Edit campaigns (only in DRAFT status)
- ✅ Link campaigns to mailing lists
- ✅ Campaign statuses: DRAFT, SCHEDULED, SENT, FAILED
- ✅ Campaign details: Name, Subject, Content, Target list

### 4. Campaign Scheduling & Sending
- ✅ Schedule campaigns for future date/time
- ✅ Automatic validation (cannot schedule in past)
- ✅ Scheduled campaigns auto-send at specified time
- ✅ Email sending logging for all subscribers
- ✅ Campaign status updates to SENT after completion
- ✅ Error tracking and failed email logging

### 5. Query & Reporting
- ✅ View all campaigns with pagination
- ✅ Filter campaigns by status (DRAFT, SCHEDULED, SENT, FAILED)
- ✅ View email sending logs per campaign
- ✅ Track subscriber details

## Technology Stack

- **Framework**: Spring Boot 3.1.5
- **Authentication**: JWT (JJWT 0.12.3) + Spring Security
- **Database**: H2 (in-memory for development)
- **ORM**: Spring Data JPA + Hibernate
- **Password Encoding**: BCrypt
- **API Documentation**: Springdoc OpenAPI (Swagger UI)
- **Build Tool**: Maven
- **Java Version**: 17

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- IDE: IntelliJ IDEA, Eclipse, or VS Code

### Installation

1. Clone the repository
```bash
cd newsletter-email-campaign
```

2. Build the project
```bash
mvn clean install
```

3. Run the application
```bash
mvn spring-boot:run
```

The API will be available at: `http://localhost:8080/api`

### Swagger UI Documentation

Once the application is running, access the interactive API documentation:
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/v3/api-docs

### H2 Database Console

Access the in-memory database console:
- **URL**: http://localhost:8080/api/h2-console
- **JDBC URL**: `jdbc:h2:mem:newsletterdb`
- **Username**: `sa`
- **Password**: (leave blank)
