# ChatQ Assist - FAQ & Support Ticket System

A GDPR-compliant FAQ management and support ticket system for SMBs, featuring an embeddable chat widget with intelligent handoff capabilities.

## 🚀 Features

### ✅ Implemented (v0.1)

#### FAQ Management
- **📝 Full CRUD System**: Create, Read, Update, Delete FAQ entries
- **🏷️ Tag System**: Organize FAQs with tags for better categorization
- **📊 Usage Tracking**: Track how often each FAQ is accessed
- **🔄 Display Order**: Custom ordering for FAQ presentation
- **✅ Active/Inactive**: Soft enable/disable FAQs without deletion

#### Support Ticket System
- **🎫 Complete Ticket Management**: Full CRUD API for support tickets
- **📋 Status Tracking**: OPEN, IN_PROGRESS, RESOLVED, CLOSED
- **⚡ Priority Levels**: LOW, MEDIUM, HIGH, URGENT
- **👤 Customer Information**: Name, email, phone, original question
- **📊 Statistics Dashboard**: Real-time ticket metrics and counts
- **🔍 Filtering & Search**: Filter by status, search by customer details
- **📧 Email Notifications**: Automatic HTML email alerts on handoff
- **💼 Assignment System**: Assign tickets to team members
- **📝 Internal Notes**: Add notes and comments to tickets

#### Chat Widget (Frontend)
- **🎨 Modern Angular 17 Widget**: Standalone component architecture
- **📱 Responsive Design**: Mobile-first UI with clean styling
- **💬 Handoff Modal**: Customer information collection form
- **🎯 Smart Question Capture**: Stores the original customer question
- **🌙 Theme Support**: Light/dark mode toggle
- **🔄 Session Management**: Persistent chat sessions via localStorage
- **👍 Feedback System**: Thumbs up/down for responses
- **🎨 Customizable Branding**: Configure colors and appearance

#### Multi-Tenancy
- **🏢 Tenant Isolation**: Header-based tenant separation (`X-Tenant-ID`)
- **🗄️ Data Isolation**: All queries filter by tenant ID
- **🔐 Secure by Default**: Role-based access control (ADMIN, TENANT_ADMIN)

#### Production Features
- **🚨 Global Exception Handling**: Centralized error handling with consistent responses
- **📝 Structured Logging**: Separate log files with rolling policy (30-day retention)
- **🔍 Request Tracking**: Unique request IDs for correlation across logs
- **⚡ Async Logging**: Non-blocking logging with Logback async appenders
- **📊 Custom Error Responses**: Standardized error format across all endpoints

### 🚧 Planned (Roadmap)

- [ ] **LLM Integration**: RAG pipeline with OpenAI GPT-4 and vector embeddings
- [ ] **Streaming Responses**: Server-Sent Events (SSE) for real-time chat
- [ ] **Document Ingestion**: Upload and process URLs, PDFs, DOCX
- [ ] **Analytics Dashboard**: Top questions, deflection rate, trends
- [ ] **Rate Limiting**: API protection against overload
- [ ] **Monitoring & Metrics**: Prometheus/Grafana integration
- [ ] **Multi-Language Support**: i18n for different languages
- [ ] **File Attachments**: Upload files with support tickets

## 🛠️ Tech Stack

### Backend
| Technology | Version | Purpose |
|------------|---------|---------|
| **Spring Boot** | 3.2.0 | Application Framework |
| **Java** | 21 | Programming Language |
| **PostgreSQL** | 16+ | Primary Database |
| **pgvector** | latest | Vector Extension (prepared for future RAG) |
| **Flyway** | 9.x | Database Migration |
| **Spring Mail** | - | SMTP Email Integration |
| **Logback** | - | Structured Logging |
| **Lombok** | latest | Boilerplate Reduction |

### Frontend
| Technology | Version | Purpose |
|------------|---------|---------|
| **Angular** | 17.x | Frontend Framework (Standalone Components) |
| **TypeScript** | 5.x | Programming Language |
| **RxJS** | 7.x | Reactive Programming |
| **HttpClient** | Angular | HTTP Communication |

### DevOps
- **Docker** + **Docker Compose**: Containerization
- **Maven**: Build Tool (Backend)
- **Angular CLI**: Build Tool (Frontend)

## 📁 Project Structure

```
ChatQ-Assist/
├── chatq-assist-backend/              # Spring Boot Backend
│   ├── src/main/java/com/chatq/assist/
│   │   ├── config/
│   │   │   ├── WebMvcConfig.java      # Request Interceptor Config
│   │   │   └── LoggingInterceptor.java # HTTP Request Tracking
│   │   ├── controller/
│   │   │   ├── FaqController.java     # /api/faq
│   │   │   └── SupportTicketController.java  # /api/tickets
│   │   ├── domain/
│   │   │   ├── entity/
│   │   │   │   ├── FaqEntry.java      # FAQ Entity
│   │   │   │   ├── Conversation.java  # Chat Sessions
│   │   │   │   ├── Message.java       # Chat Messages
│   │   │   │   └── SupportTicket.java # Support Tickets
│   │   │   ├── dto/
│   │   │   │   ├── FaqEntryDto.java
│   │   │   │   ├── HandoffRequestDto.java
│   │   │   │   ├── TicketResponseDto.java
│   │   │   │   └── ErrorResponse.java
│   │   │   └── enums/
│   │   │       ├── TicketStatus.java
│   │   │       └── TicketPriority.java
│   │   ├── repository/
│   │   │   ├── FaqRepository.java
│   │   │   ├── ConversationRepository.java
│   │   │   ├── MessageRepository.java
│   │   │   └── SupportTicketRepository.java
│   │   ├── service/
│   │   │   ├── FaqService.java        # FAQ CRUD
│   │   │   ├── SupportTicketService.java  # Ticket Management
│   │   │   └── EmailService.java      # SMTP Email Sender
│   │   └── exception/
│   │       ├── GlobalExceptionHandler.java
│   │       ├── ResourceNotFoundException.java
│   │       └── BusinessException.java
│   ├── src/main/resources/
│   │   ├── db/migration/
│   │   │   ├── V1__init_schema.sql
│   │   │   ├── V2__add_chat_tables.sql
│   │   │   ├── ...
│   │   │   ├── V8__create_support_tickets_table.sql
│   │   │   └── V9__add_customer_question_to_support_tickets.sql
│   │   ├── logback-spring.xml         # Logging Configuration
│   │   └── application.properties
│   ├── Dockerfile
│   └── pom.xml
│
├── chatq-assist-frontend/             # Angular Widget
│   ├── src/app/
│   │   ├── components/
│   │   │   ├── chat-widget/
│   │   │   │   ├── chat-widget.component.ts
│   │   │   │   ├── chat-widget.component.html
│   │   │   │   └── chat-widget.component.css
│   │   │   └── ticket-management/
│   │   │       ├── ticket-management.component.ts
│   │   │       ├── ticket-management.component.html
│   │   │       └── ticket-management.component.css
│   │   └── services/
│   │       ├── chat.service.ts        # Chat API Client
│   │       └── ticket.service.ts      # Ticket API Client
│   ├── package.json
│   └── angular.json
│
├── docker-compose.yml
├── CLAUDE.md                          # Project Instructions for Claude Code
└── README.md
```

## 🚀 Quick Start

### Prerequisites

- **Docker** & **Docker Compose** (for easiest setup)
- **Java 21** (for local backend development)
- **Node.js 18+** (for frontend development)
- **PostgreSQL 16+** with **pgvector extension** (if running locally without Docker)

### Option 1: Docker Compose (Recommended)

```bash
# 1. Clone repository
git clone https://github.com/Blindworks/chatq-assist.git
cd ChatQ-Assist

# 2. Start all services
docker-compose up -d

# 3. Follow logs
docker-compose logs -f backend
```

**Access**:
- Backend API: http://localhost:8080
- Frontend Widget: http://localhost:4200
- PostgreSQL: localhost:5433 (User: `postgres`, PW: `taxcRH51#`)

### Option 2: Local Development

#### Start Backend

```bash
cd chatq-assist-backend

# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

**Important**: PostgreSQL with pgvector must be running! See [pgvector Installation](#pgvector-installation).

#### Start Frontend

```bash
cd chatq-assist-frontend
npm install
ng serve
```

Widget runs on http://localhost:4200

## 📋 API Documentation

### FAQ Endpoints

#### GET /api/faq
Get all FAQs for a tenant

**Headers**: `X-Tenant-ID: default-tenant`

**Response**:
```json
[
  {
    "id": 1,
    "question": "What are your business hours?",
    "answer": "Monday to Friday 9am-6pm",
    "tags": ["hours", "service"],
    "isActive": true,
    "displayOrder": 1,
    "usageCount": 42,
    "createdAt": "2024-01-15T10:00:00Z",
    "updatedAt": "2024-01-15T10:00:00Z"
  }
]
```

#### POST /api/faq
Create a single FAQ entry

**Headers**: `X-Tenant-ID: default-tenant`

**Request**:
```json
{
  "question": "How can I contact you?",
  "answer": "You can reach us by phone at...",
  "tags": ["contact"],
  "isActive": true,
  "displayOrder": 2
}
```

#### PUT /api/faq/{id}
Update FAQ entry

#### DELETE /api/faq/{id}
Delete FAQ entry

### Support Ticket Endpoints

All ticket endpoints require authentication with roles: `ADMIN`, `SUPER_ADMIN`, or `TENANT_ADMIN`.

#### GET /api/tickets
Get paginated list of tickets

**Headers**: `X-Tenant-ID: default-tenant`

**Query Parameters**:
- `status` (optional): Filter by status (OPEN, IN_PROGRESS, RESOLVED, CLOSED)
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)

**Response**:
```json
{
  "content": [
    {
      "id": 1,
      "tenantId": "default-tenant",
      "sessionId": "uuid-here",
      "customerName": "John Doe",
      "customerEmail": "john@example.com",
      "customerPhone": "+49123456789",
      "customerQuestion": "I need help with my account",
      "status": "OPEN",
      "priority": "MEDIUM",
      "assignedTo": null,
      "notes": null,
      "createdAt": "2024-01-15T10:00:00Z",
      "updatedAt": "2024-01-15T10:00:00Z"
    }
  ],
  "pageable": {...},
  "totalElements": 10,
  "totalPages": 1
}
```

#### GET /api/tickets/{id}
Get single ticket by ID

#### PUT /api/tickets/{id}
Update ticket (status, priority, assignment, notes)

**Request**:
```json
{
  "status": "IN_PROGRESS",
  "priority": "HIGH",
  "assignedTo": "admin@example.com",
  "notes": "Working on this issue now"
}
```

#### DELETE /api/tickets/{id}
Delete ticket

#### GET /api/tickets/stats
Get ticket statistics for tenant

**Response**:
```json
{
  "total": 45,
  "open": 12,
  "inProgress": 8,
  "resolved": 20,
  "closed": 5
}
```

### Chat & Handoff Endpoints

#### POST /api/chat/handoff
Submit handoff request and create support ticket

**Headers**: `X-Tenant-ID: default-tenant`

**Request**:
```json
{
  "sessionId": "uuid",
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+49123456789",
  "question": "I need help with my account"
}
```

**Response**: Created ticket details

## 🔧 Configuration

### application.properties

```properties
# Database
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5433}/${DB_NAME:chatq_assist}
spring.datasource.username=${DB_USER:postgres}
spring.datasource.password=${DB_PASSWORD:taxcRH51#}

# JPA (Flyway handles schema)
spring.jpa.hibernate.ddl-auto=validate

# Server
server.port=8080

# Email Configuration
spring.mail.host=${MAIL_HOST:smtp.gmail.com}
spring.mail.port=${MAIL_PORT:587}
spring.mail.username=${MAIL_USERNAME:benedikt.lind@gmail.com}
spring.mail.password=${MAIL_PASSWORD:}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Email Settings
email.from=${EMAIL_FROM:noreply@chatq-assist.com}
email.admin=${EMAIL_ADMIN:admin@chatq-assist.com}
email.enabled=${EMAIL_ENABLED:true}

# Logging
logging.file.name=logs/chatq-assist.log
logging.file.path=logs
logging.level.com.chatq.assist=INFO
logging.level.org.springframework.web=INFO
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_HOST` | PostgreSQL Host | `localhost` |
| `DB_PORT` | PostgreSQL Port | `5433` |
| `DB_NAME` | Database Name | `chatq_assist` |
| `DB_USER` | Database User | `postgres` |
| `DB_PASSWORD` | Database Password | `taxcRH51#` |
| `MAIL_HOST` | SMTP Server Host | `smtp.gmail.com` |
| `MAIL_PORT` | SMTP Server Port | `587` |
| `MAIL_USERNAME` | SMTP Username | - |
| `MAIL_PASSWORD` | SMTP Password | - |
| `EMAIL_FROM` | From Email Address | `noreply@chatq-assist.com` |
| `EMAIL_ADMIN` | Admin Email Address | `admin@chatq-assist.com` |
| `EMAIL_ENABLED` | Enable/Disable Emails | `true` |

## 🗄️ Database Schema

### faq_entries
```sql
CREATE TABLE faq_entries (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    tags TEXT[],
    is_active BOOLEAN DEFAULT true,
    display_order INTEGER,
    usage_count BIGINT DEFAULT 0,
    embedding vector(1536),  -- Prepared for future RAG
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_faq_tenant ON faq_entries(tenant_id);
```

### support_tickets
```sql
CREATE TABLE support_tickets (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    conversation_id BIGINT REFERENCES conversations(id),
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(50),
    customer_question TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    assigned_to VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_tickets_tenant ON support_tickets(tenant_id);
CREATE INDEX idx_tickets_status ON support_tickets(tenant_id, status);
```

### conversations
```sql
CREATE TABLE conversations (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(255) UNIQUE NOT NULL,
    tenant_id VARCHAR(255) NOT NULL,
    user_email VARCHAR(255),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    last_activity_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW()
);
```

### messages
```sql
CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT REFERENCES conversations(id),
    role VARCHAR(50) NOT NULL,  -- USER, ASSISTANT
    content TEXT NOT NULL,
    confidence_score DOUBLE PRECISION,
    faq_entry_id BIGINT,
    tenant_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);
```

## 🧪 Testing

### cURL Examples

```bash
# Create FAQ
curl -X POST http://localhost:8080/api/faq \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: default-tenant" \
  -d '{
    "question": "What are your business hours?",
    "answer": "Monday to Friday from 9am to 6pm.",
    "tags": ["hours", "service"],
    "displayOrder": 1
  }'

# Get all FAQs
curl -X GET http://localhost:8080/api/faq \
  -H "X-Tenant-ID: default-tenant"

# Get all tickets
curl -X GET http://localhost:8080/api/tickets \
  -H "X-Tenant-ID: default-tenant"

# Get ticket statistics
curl -X GET http://localhost:8080/api/tickets/stats \
  -H "X-Tenant-ID: default-tenant"
```

## 🔐 pgvector Installation

### Windows (PostgreSQL 16+)

1. Download pgvector for your PostgreSQL version from:
   https://github.com/pgvector/pgvector/releases

2. Extract `vector.dll` to:
   ```
   C:\Program Files\PostgreSQL\16\lib\
   ```

3. Extract SQL files to:
   ```
   C:\Program Files\PostgreSQL\16\share\extension\
   ```

4. In psql:
   ```sql
   CREATE EXTENSION vector;
   ```

### Linux (Ubuntu/Debian)

```bash
sudo apt install postgresql-16-pgvector
```

### macOS

```bash
brew install pgvector
```

### Docker

```dockerfile
FROM postgres:16
RUN apt-get update && apt-get install -y postgresql-16-pgvector
```

## 📝 Logging

The application uses structured logging with Logback:

- **Main Log**: `logs/chatq-assist.log` - All log messages
- **Error Log**: `logs/chatq-assist-error.log` - Only ERROR level
- **Rolling Policy**: Daily rotation, 30-day retention
- **Async Appenders**: Non-blocking for better performance
- **Request IDs**: Each HTTP request gets a unique UUID for tracing

Log pattern includes: timestamp, level, thread, logger, request ID, and message.

## 🚨 Exception Handling

The application uses a global exception handler (`@RestControllerAdvice`) that returns consistent error responses:

```json
{
  "timestamp": "2024-01-15T10:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Ticket not found with ID: 123",
  "path": "/api/tickets/123"
}
```

Handled exceptions:
- **Validation Errors**: Field-level validation with detailed error messages
- **Resource Not Found**: 404 with specific error message
- **Business Logic Errors**: Custom business exceptions
- **Generic Errors**: Catch-all for unexpected exceptions

## 🚨 Troubleshooting

### "Type vector does not exist"
→ pgvector extension not installed. See [pgvector Installation](#pgvector-installation)

### "Query returned no result" (Hibernate)
→ PostgreSQL dependency must have `compile` scope (not `runtime`)

### Email not sending
→ Check SMTP credentials and ensure `EMAIL_ENABLED=true`
→ For Gmail, use an App Password, not your regular password

### "Access Denied" on ticket endpoints
→ Ticket endpoints require authentication with ADMIN role

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push: `git push origin feature/amazing-feature`
5. Open Pull Request

## 📝 License

This project is licensed under the **MIT License** - see [LICENSE](LICENSE) for details.

## 🙋 Support

- **Issues**: [GitHub Issues](https://github.com/Blindworks/chatq-assist/issues)
- **Repository**: [https://github.com/Blindworks/chatq-assist](https://github.com/Blindworks/chatq-assist)

## 👏 Credits

- **pgvector**: https://github.com/pgvector/pgvector
- **Spring Boot**: https://spring.io/projects/spring-boot
- **Angular**: https://angular.io

---

**Built for SMBs who need smart, GDPR-compliant customer support**
