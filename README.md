# ChatQ Assist - Intelligent FAQ & Support System

A GDPR-compliant, LLM-powered FAQ management and support ticket system for SMBs with multi-tenancy, embeddable chat widget, and intelligent escalation.

## 🚀 Features

### ✅ Fully Implemented

#### 🤖 LLM-Powered Chat
- **RAG Pipeline**: OpenAI GPT-4 integration with pgvector for semantic search
- **Streaming Responses**: Server-Sent Events (SSE) for real-time chat
- **Intelligent Handoff Detection**: Automatic escalation based on low confidence scores
- **Chat History**: Persistent conversation storage per session
- **Confidence Scoring**: Quality assessment of responses
- **Feedback System**: Thumbs up/down for user feedback

#### 📝 FAQ Management
- **Full CRUD System**: Create, Read, Update, Delete FAQ entries
- **Tag System**: Flexible categorization with tags
- **Vector Embeddings**: Automatic generation for semantic search
- **Usage Tracking**: Count FAQ access frequency
- **Display Order**: Customizable sorting
- **Active/Inactive**: Soft enable/disable without deletion

#### 🎫 Support Ticket System
- **Complete Ticket Management**: Full CRUD API for support tickets
- **Status Tracking**: OPEN, IN_PROGRESS, RESOLVED, CLOSED
- **Priority Levels**: LOW, MEDIUM, HIGH, URGENT
- **Customer Information**: Name, email, phone, original question
- **Statistics Dashboard**: Real-time metrics per tenant
- **Filtering & Search**: By status, customer, time period
- **Email Notifications**: Automatic HTML email alerts on escalation
- **Assignment System**: Assign tickets to team members
- **Internal Notes**: Comments and notes on tickets

#### 📄 Document Management
- **File Upload**: Upload PDF, DOCX, TXT files
- **URL Ingestion**: Automatic content extraction from web pages
- **Sitemap Support**: Bulk import from website content (prepared)
- **Text Extraction**: Automatic processing of various formats
- **Embedding Generation**: Vectorization for RAG pipeline
- **Document CRUD**: Complete management with status tracking

#### 📊 Analytics & Reporting
- **Dashboard Metrics**: Top FAQs, feedback statistics, conversation data
- **Time-Based Filtering**: Analysis for custom time periods
- **Feedback Evaluation**: Positive/negative ratings
- **FAQ Performance**: Usage frequency and success rates
- **Conversation Statistics**: Number of chats, average length

#### 🔐 Authentication & Authorization
- **JWT-Based Authentication**: Secure token-based auth
- **Role-Based Access Control**: SUPER_ADMIN, TENANT_ADMIN, ADMIN, TENANT_USER
- **Login/Logout**: Standard authentication endpoints
- **Token Validation**: Automatic JWT verification
- **Password Hashing**: BCrypt for secure password storage

#### 🏢 Multi-Tenancy & Tenant Management
- **Tenant Management**: Full CRUD for tenants
- **API Key System**: Generation and management of API keys
- **Data Isolation**: Strict separation by tenant ID
- **Tenant Status**: Activation/deactivation of tenants
- **Separate Configuration**: Individual settings per tenant

#### 👥 User Management
- **User CRUD**: Complete user management
- **Role Assignment**: Flexible role allocation
- **Tenant Association**: Assign users to tenants
- **Status Management**: Activation/deactivation of accounts
- **Permission Control**: Cross-tenant access restrictions

#### 💬 Chat Widget (Angular Frontend)
- **Modern Angular 17**: Standalone component architecture
- **Responsive Design**: Mobile-first UI
- **Handoff Modal**: Customer data collection form
- **Session Management**: Persistent chat sessions via localStorage
- **Feedback Integration**: UI for thumbs up/down
- **Theme Support**: Light/dark mode (prepared)
- **Customizable Branding**: Configurable colors and appearance

#### 🏭 Production-Ready Features
- **Global Exception Handling**: Centralized error handling
- **Structured Logging**: Separate log files with rolling policy (30 days)
- **Request Tracking**: Unique request IDs for correlation
- **Async Logging**: Non-blocking logging with Logback
- **Standardized Error Responses**: Consistent error format
- **CORS Configuration**: Flexible cross-origin settings
- **Health Checks**: Actuator endpoints for monitoring

### 🚧 Planned / In Progress

- [ ] **Rate Limiting**: API protection against overload
- [ ] **Prometheus/Grafana**: Metrics and monitoring integration
- [ ] **Multi-Language Support**: i18n for different languages
- [ ] **File Attachments for Tickets**: Upload files to support tickets
- [ ] **Admin Dashboard (Frontend)**: Complete management UI
- [ ] **WebSocket Support**: Alternative to SSE for bidirectional communication
- [ ] **Advanced Sitemap Processing**: Automatic bulk import

## 🛠️ Tech Stack

### Backend
| Technology | Version | Purpose |
|------------|---------|---------|
| **Spring Boot** | 3.2.0 | Application Framework |
| **Java** | 21 | Programming Language |
| **PostgreSQL** | 16+ | Primary Database |
| **pgvector** | latest | Vector Extension for RAG |
| **Flyway** | 9.x | Database Migrations |
| **Spring Security** | 6.x | Authentication & Authorization |
| **JWT (jjwt)** | 0.12.x | JSON Web Tokens |
| **Spring Mail** | - | SMTP Email Integration |
| **Logback** | - | Structured Logging |
| **Lombok** | latest | Boilerplate Reduction |
| **OpenAI Java Client** | latest | LLM Integration |

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
│   │   │   ├── SecurityConfig.java             # Spring Security Configuration
│   │   │   ├── JwtTokenProvider.java           # JWT Token Handling
│   │   │   ├── JwtAuthenticationFilter.java    # JWT Filter
│   │   │   ├── WebMvcConfig.java               # Request Interceptor
│   │   │   └── LoggingInterceptor.java         # HTTP Request Tracking
│   │   ├── controller/
│   │   │   ├── FaqController.java              # /api/faq
│   │   │   ├── ChatController.java             # /api/chat (LLM, Streaming, History)
│   │   │   ├── SupportTicketController.java    # /api/tickets
│   │   │   ├── DocumentController.java         # /api/documents
│   │   │   ├── AnalyticsController.java        # /api/analytics
│   │   │   ├── AuthController.java             # /api/auth
│   │   │   ├── TenantController.java           # /api/tenants
│   │   │   └── UserManagementController.java   # /api/users
│   │   ├── domain/
│   │   │   ├── entity/
│   │   │   │   ├── FaqEntry.java               # FAQ with Embeddings
│   │   │   │   ├── Conversation.java           # Chat Sessions
│   │   │   │   ├── Message.java                # Chat Messages
│   │   │   │   ├── MessageFeedback.java        # User Feedback
│   │   │   │   ├── SupportTicket.java          # Support Tickets
│   │   │   │   ├── Document.java               # Uploaded Documents
│   │   │   │   ├── User.java                   # Users
│   │   │   │   └── Tenant.java                 # Tenants
│   │   │   ├── dto/
│   │   │   │   ├── ChatRequest/Response        # Chat API
│   │   │   │   ├── FaqEntryDto                 # FAQ Transfer
│   │   │   │   ├── HandoffRequestDto           # Escalation
│   │   │   │   ├── TicketResponseDto           # Ticket Transfer
│   │   │   │   ├── DocumentDto                 # Document Transfer
│   │   │   │   ├── AnalyticsDto                # Analytics Data
│   │   │   │   ├── AuthResponse/LoginRequest   # Auth DTOs
│   │   │   │   ├── UserDto/CreateUserRequest   # User Management
│   │   │   │   ├── TenantDto                   # Tenant Transfer
│   │   │   │   └── ErrorResponse               # Error Format
│   │   │   └── enums/
│   │   │       ├── TicketStatus/Priority       # Ticket Enums
│   │   │       ├── DocumentType/Status         # Document Enums
│   │   │       ├── ConversationStatus          # Chat Status
│   │   │       ├── MessageRole                 # USER/ASSISTANT
│   │   │       ├── UserRole                    # Roles
│   │   │       └── FeedbackType                # POSITIVE/NEGATIVE
│   │   ├── repository/
│   │   │   ├── FaqRepository.java              # JPA with Vector Queries
│   │   │   ├── ConversationRepository.java     # Chat Management
│   │   │   ├── MessageRepository.java          # Message History
│   │   │   ├── MessageFeedbackRepository.java  # Feedback Data
│   │   │   ├── SupportTicketRepository.java    # Tickets
│   │   │   ├── DocumentRepository.java         # Documents
│   │   │   ├── UserRepository.java             # Users
│   │   │   └── TenantRepository.java           # Tenants
│   │   ├── service/
│   │   │   ├── FaqService.java                 # FAQ CRUD + Embeddings
│   │   │   ├── ChatServiceLLM.java             # RAG Pipeline with OpenAI
│   │   │   ├── EmbeddingService.java           # OpenAI Embeddings
│   │   │   ├── FeedbackService.java            # Feedback Processing
│   │   │   ├── SupportTicketService.java       # Ticket Management
│   │   │   ├── DocumentService.java            # Document Processing
│   │   │   ├── AnalyticsService.java           # Analytics Data
│   │   │   ├── EmailService.java               # SMTP Email
│   │   │   ├── AuthService.java                # JWT Auth Logic
│   │   │   ├── UserManagementService.java      # User CRUD
│   │   │   └── TenantService.java              # Tenant Management
│   │   └── exception/
│   │       ├── GlobalExceptionHandler.java     # Centralized Error Handling
│   │       ├── ResourceNotFoundException.java  # 404 Exceptions
│   │       └── BusinessException.java          # Business Logic Errors
│   ├── src/main/resources/
│   │   ├── db/migration/                       # Flyway Migrations
│   │   │   ├── V1__initial_schema.sql          # FAQs + pgvector
│   │   │   ├── V2__create_chat_tables.sql      # Conversations, Messages
│   │   │   ├── V3__add_embeddings_to_faq.sql   # Vector Column
│   │   │   ├── V4__create_message_feedback.sql # Feedback Table
│   │   │   ├── V5__create_users.sql            # Users Table
│   │   │   ├── V6__create_documents.sql        # Documents
│   │   │   ├── V7__create_tenant_management.sql # Tenants
│   │   │   ├── V8__create_support_tickets.sql  # Support Tickets
│   │   │   └── V9__add_customer_question_to_support_tickets.sql
│   │   ├── logback-spring.xml                  # Logging Configuration
│   │   └── application.properties              # Main Configuration
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
│   │       ├── chat.service.ts                 # Chat API Client
│   │       └── ticket.service.ts               # Ticket API Client
│   ├── package.json
│   └── angular.json
│
├── docker-compose.yml
├── CLAUDE.md                                   # Project Instructions for Claude Code
└── README.md
```

## 🚀 Quick Start

### Prerequisites

- **Docker** & **Docker Compose** (recommended setup method)
- **Java 21** (for local backend development)
- **Node.js 18+** (for frontend development)
- **PostgreSQL 16+** with **pgvector extension** (for local setup)
- **OpenAI API Key** (for LLM functionality)

### Option 1: Docker Compose (Recommended)

```bash
# 1. Clone repository
git clone https://github.com/Blindworks/chatq-assist.git
cd ChatQ-Assist

# 2. Configure environment variables
# Create a .env file or edit docker-compose.yml:
# - OPENAI_API_KEY=sk-...
# - JWT_SECRET=your-secure-secret
# - MAIL_USERNAME, MAIL_PASSWORD

# 3. Start all services
docker-compose up -d

# 4. Follow logs
docker-compose logs -f backend
```

**Access**:
- Backend API: http://localhost:8080
- Frontend Widget: http://localhost:4200
- PostgreSQL: localhost:5433 (User: `postgres`, PW: `hidden`)

### Option 2: Local Development

#### Start Backend

```bash
cd chatq-assist-backend

# Set environment variables
export OPENAI_API_KEY=sk-...
export JWT_SECRET=your-secure-secret
export DB_HOST=localhost
export DB_PORT=5433

# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

**Important**: PostgreSQL with pgvector must be running! See [pgvector Installation](#-pgvector-installation).

#### Start Frontend

```bash
cd chatq-assist-frontend
npm install
ng serve
```

Widget runs on http://localhost:4200

## 📋 API Documentation

All endpoints use `X-Tenant-ID` header (default: `default-tenant`).
Protected endpoints require `Authorization: Bearer <JWT-Token>` header.

### 🔐 Authentication

#### POST /api/auth/login
Login with username and password

**Request**:
```json
{
  "username": "admin",
  "password": "password123"
}
```

**Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "username": "admin",
  "role": "SUPER_ADMIN",
  "tenantId": "default-tenant",
  "message": "Login successful"
}
```

#### GET /api/auth/validate
Validate JWT token (requires Authorization header)

### 📝 FAQ Endpoints

#### GET /api/faq
Get all FAQs for tenant

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
Create FAQ (embeddings are generated automatically)

#### PUT /api/faq/{id}
Update FAQ

#### DELETE /api/faq/{id}
Delete FAQ

### 💬 Chat Endpoints

#### POST /api/chat
RAG-based chat request (regular)

**Request**:
```json
{
  "question": "How can I contact you?",
  "sessionId": "uuid-optional"
}
```

**Response**:
```json
{
  "answer": "You can reach us by phone at...",
  "sessionId": "uuid",
  "conversationId": 123,
  "confidenceScore": 0.95,
  "handoffTriggered": false,
  "suggestedFaqIds": [1, 5, 7]
}
```

#### POST /api/chat/stream
Streaming chat with Server-Sent Events (SSE)

**Headers**: `Accept: text/event-stream`

#### GET /api/chat/history/{sessionId}
Get chat history for session

#### POST /api/chat/feedback
Submit feedback for message

**Request**:
```json
{
  "messageId": 123,
  "feedbackType": "POSITIVE",
  "comment": "Very helpful!"
}
```

#### POST /api/chat/handoff
Escalate to support ticket with customer data

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

### 🎫 Support Ticket Endpoints

🔒 All ticket endpoints require authentication with roles: `ADMIN`, `SUPER_ADMIN`, or `TENANT_ADMIN`

#### GET /api/tickets
Get paginated list of tickets

**Query Parameters**:
- `status` (optional): Filter by status
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)

#### GET /api/tickets/{id}
Get single ticket

#### PUT /api/tickets/{id}
Update ticket (status, priority, assignment, notes)

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

### 📄 Document Endpoints

#### POST /api/documents/upload
Upload file (PDF, DOCX, TXT)

**Form-Data**:
- `file`: Multipart-File
- `title`: String
- `documentType`: DOCUMENT_PDF | DOCUMENT_DOCX | DOCUMENT_TXT

#### POST /api/documents/ingest
Ingest document from URL

**Request**:
```json
{
  "sourceUrl": "https://example.com/page",
  "title": "Company Info",
  "documentType": "URL"
}
```

#### GET /api/documents
Get all documents for tenant

#### GET /api/documents/{id}
Get single document

#### DELETE /api/documents/{id}
Delete document

### 📊 Analytics Endpoints

#### GET /api/analytics
Get analytics data for tenant

**Query Parameters**:
- `daysBack` (optional): Number of days to look back (default: 30)

**Response**:
```json
{
  "totalConversations": 150,
  "totalMessages": 450,
  "averageMessagesPerConversation": 3.0,
  "positiveFeedbackCount": 120,
  "negativeFeedbackCount": 15,
  "topFaqs": [
    {
      "id": 1,
      "question": "What are your business hours?",
      "usageCount": 42
    }
  ]
}
```

### 🏢 Tenant Management Endpoints

🔒 Only for `SUPER_ADMIN`

#### GET /api/tenants
Get all tenants

#### GET /api/tenants/active
Get only active tenants

#### GET /api/tenants/{id}
Get tenant by ID

#### POST /api/tenants
Create new tenant

**Request**:
```json
{
  "name": "Acme Corp",
  "tenantId": "acme-corp",
  "contactEmail": "admin@acme.com",
  "isActive": true
}
```

#### PUT /api/tenants/{id}
Update tenant

#### POST /api/tenants/{id}/toggle-status
Activate/deactivate tenant

#### POST /api/tenants/{id}/regenerate-api-key
Generate new API key

#### DELETE /api/tenants/{id}
Delete tenant

### 👥 User Management Endpoints

🔒 For `SUPER_ADMIN` and `TENANT_ADMIN`

#### GET /api/users
Get all users (SUPER_ADMIN only)

#### GET /api/users/tenant/{tenantId}
Get users for tenant

#### POST /api/users
Create new user

**Request**:
```json
{
  "username": "john.doe",
  "password": "SecurePass123!",
  "email": "john@example.com",
  "fullName": "John Doe",
  "role": "TENANT_USER",
  "tenantId": "default-tenant",
  "isActive": true
}
```

#### PUT /api/users/{id}
Update user

#### POST /api/users/{id}/toggle-status
Activate/deactivate user

#### DELETE /api/users/{id}
Delete user

## 🔧 Configuration

### application.properties

```properties
# Database
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5433}/${DB_NAME:chatq_assist}
spring.datasource.username=${DB_USER:postgres}
spring.datasource.password=${DB_PASSWORD:hidden}

# JPA (Flyway handles schema)
spring.jpa.hibernate.ddl-auto=validate

# Server
server.port=8080

# OpenAI
openai.api.key=${OPENAI_API_KEY}
openai.model=${OPENAI_MODEL:gpt-4}
openai.embedding.model=${OPENAI_EMBEDDING_MODEL:text-embedding-ada-002}

# JWT Security
jwt.secret=${JWT_SECRET:change-this-in-production}
jwt.expiration=${JWT_EXPIRATION:86400000}

# Email Configuration
spring.mail.host=${MAIL_HOST:smtp.gmail.com}
spring.mail.port=${MAIL_PORT:587}
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
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
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_HOST` | PostgreSQL Host | `localhost` |
| `DB_PORT` | PostgreSQL Port | `5433` |
| `DB_NAME` | Database Name | `chatq_assist` |
| `DB_USER` | Database User | `postgres` |
| `DB_PASSWORD` | Database Password | `hidden` |
| `OPENAI_API_KEY` | OpenAI API Key | **REQUIRED** |
| `OPENAI_MODEL` | OpenAI Model | `gpt-4` |
| `JWT_SECRET` | JWT Secret Key | **Change in production!** |
| `JWT_EXPIRATION` | Token validity (ms) | `86400000` (24h) |
| `MAIL_HOST` | SMTP Server | `smtp.gmail.com` |
| `MAIL_PORT` | SMTP Port | `587` |
| `MAIL_USERNAME` | SMTP Username | - |
| `MAIL_PASSWORD` | SMTP Password | - |
| `EMAIL_FROM` | Sender Address | `noreply@chatq-assist.com` |
| `EMAIL_ADMIN` | Admin Email | `admin@chatq-assist.com` |
| `EMAIL_ENABLED` | Enable Emails | `true` |

## 🗄️ Database Schema

### faq_entries
FAQ entries with vector embeddings for semantic search

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
    embedding vector(1536),  -- OpenAI Embeddings
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    version INTEGER DEFAULT 0
);
```

### conversations
Chat sessions with tenant isolation

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
Chat messages with confidence scores

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

### message_feedback
User feedback for messages

```sql
CREATE TABLE message_feedback (
    id BIGSERIAL PRIMARY KEY,
    message_id BIGINT REFERENCES messages(id),
    tenant_id VARCHAR(255) NOT NULL,
    feedback_type VARCHAR(50) NOT NULL,  -- POSITIVE, NEGATIVE
    comment TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);
```

### support_tickets
Support tickets with customer information

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
```

### documents
Uploaded documents with embeddings

```sql
CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    source_url TEXT,
    document_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    embedding vector(1536),
    file_path TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    version INTEGER DEFAULT 0
);
```

### users
User accounts with roles

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    role VARCHAR(50) NOT NULL,
    tenant_id VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);
```

### tenants
Tenant management

```sql
CREATE TABLE tenants (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255) NOT NULL,
    api_key VARCHAR(255) UNIQUE NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);
```

## 🧪 Testing & Development

### cURL Examples

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'

# Create FAQ
curl -X POST http://localhost:8080/api/faq \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: default-tenant" \
  -d '{
    "question": "What are your business hours?",
    "answer": "Monday to Friday from 9am to 6pm.",
    "tags": ["hours", "service"]
  }'

# Chat request
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: default-tenant" \
  -d '{
    "question": "When are you open?"
  }'

# Streaming chat (SSE)
curl -X POST http://localhost:8080/api/chat/stream \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: default-tenant" \
  -H "Accept: text/event-stream" \
  -d '{"question": "How can I contact you?"}'

# Get tickets with auth
curl -X GET http://localhost:8080/api/tickets \
  -H "X-Tenant-ID: default-tenant" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get analytics
curl -X GET "http://localhost:8080/api/analytics?daysBack=7" \
  -H "X-Tenant-ID: default-tenant"

# Upload document
curl -X POST http://localhost:8080/api/documents/upload \
  -H "X-Tenant-ID: default-tenant" \
  -F "file=@document.pdf" \
  -F "title=Company Document" \
  -F "documentType=DOCUMENT_PDF"
```

### Run Maven Tests

```bash
cd chatq-assist-backend
./mvnw test
```

## 🔐 pgvector Installation

### Windows (PostgreSQL 16+)

1. Download pgvector: https://github.com/pgvector/pgvector/releases
2. Copy `vector.dll` to `C:\Program Files\PostgreSQL\16\lib\`
3. Copy SQL files to `C:\Program Files\PostgreSQL\16\share\extension\`
4. In psql: `CREATE EXTENSION vector;`

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

- **Main Log**: `logs/chatq-assist.log` - All log messages
- **Error Log**: `logs/chatq-assist-error.log` - ERROR level only
- **Rolling Policy**: Daily rotation, 30-day retention
- **Async Appenders**: Non-blocking for performance
- **Request IDs**: Each HTTP request gets unique UUID

## 🚨 Troubleshooting

### "Type vector does not exist"
→ pgvector extension not installed. See [pgvector Installation](#-pgvector-installation)

### "401 Unauthorized" on protected endpoints
→ JWT token missing or invalid. Call `/api/auth/login` first

### "403 Forbidden" on admin endpoints
→ User doesn't have required role (ADMIN, TENANT_ADMIN, SUPER_ADMIN)

### Emails not sending
→ Check SMTP credentials and ensure `EMAIL_ENABLED=true`
→ For Gmail: Use App Password, not regular password

### "Cannot connect to OpenAI"
→ Set `OPENAI_API_KEY` environment variable
→ Verify API key validity

### Flyway migration fails
→ Check database schema manually: `SELECT version FROM flyway_schema_history;`
→ If issues: `./mvnw flyway:repair`

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
- **OpenAI**: https://openai.com

---

**Built for SMBs who need intelligent, GDPR-compliant customer support solutions** 🚀
