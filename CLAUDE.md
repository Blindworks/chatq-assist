# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ChatQ Assist is a DSGVO-compliant FAQ/Support chatbot system built for SMBs. It's designed as a minimal viable product (MVP) with a Spring Boot backend and Angular frontend widget that can be embedded into customer websites.

**Core Concept**: Simple FAQ management system without RAG or LLM integration (despite what the README mentions - this is a simplified implementation).

## Development Commands

### Backend (Spring Boot)

**Local development** (requires PostgreSQL running):
```bash
cd chatq-assist-backend
./mvnw spring-boot:run
```

**Build**:
```bash
cd chatq-assist-backend
./mvnw clean install
```

**Tests**:
```bash
cd chatq-assist-backend
./mvnw test
```

### Frontend (Angular)

**Local development**:
```bash
cd chatq-assist-frontend
npm install
ng serve
# Runs on http://localhost:4200
```

**Production build**:
```bash
cd chatq-assist-frontend
npm run build
# Or for widget deployment:
npm run build:widget
```

**Tests**:
```bash
cd chatq-assist-frontend
npm test
```

### Docker Compose

**Start all services** (PostgreSQL + Backend + Frontend):
```bash
docker-compose up -d
```

**View logs**:
```bash
docker-compose logs -f backend
```

**Stop all services**:
```bash
docker-compose down
```

## Architecture Overview

### Backend Structure

**Package organization** (`chatq-assist-backend/src/main/java/com/chatq/assist/`):
- `controller/` - REST API endpoints (only FaqController exists)
- `domain/entity/` - JPA entities (FaqEntry)
- `domain/dto/` - Data Transfer Objects for API requests/responses
- `domain/enums/` - Enums (ConversationStatus, DocumentStatus, DocumentType, EventType, MessageRole)
- `repository/` - Spring Data JPA repositories
- `service/` - Business logic layer (only FaqService exists)

**Database**: PostgreSQL with pgvector extension (though vector capabilities are not currently used). Flyway handles migrations in `src/main/resources/db/migration/`.

**Multi-tenancy**: The system uses a simple tenant_id field on entities with `X-Tenant-ID` header for tenant isolation. Default tenant is "default-tenant".

### Frontend Structure

**Component architecture** (`chatq-assist-frontend/src/app/`):
- `components/chat-widget/` - Main embeddable chat widget component
- `services/chat.service.ts` - HTTP service for backend communication

**Deployment**: The Angular app is built as a widget and served via nginx. It's designed to be embedded into third-party websites using script tags.

### API Design

All endpoints accept `X-Tenant-ID` header (defaults to "default-tenant" if not provided).

**Current endpoints**:
- `GET /api/faq` - List all FAQs for tenant
- `POST /api/faq` - Create FAQ entry
- `PUT /api/faq/{id}` - Update FAQ entry
- `DELETE /api/faq/{id}` - Delete FAQ entry
- `GET /actuator/health` - Health check

**Note**: The README mentions additional endpoints for chat, documents, and analytics, but these are not yet implemented in the codebase.

## Database Schema

**Tables**:
- `faq_entries` - Main FAQ storage with tenant_id, question, answer, is_active, display_order, usage_count
- `faq_tags` - Many-to-many tag association with FAQ entries

**Important columns**:
- `tenant_id` - Multi-tenant isolation key
- `version` - Optimistic locking for concurrent updates
- `usage_count` - Tracks how often FAQ is used
- `is_active` - Soft enable/disable flag

## Configuration

**Backend** (`application.properties`):
- Database connection uses environment variables: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
- Default database credentials in properties file: `taxcRH51#` (should be changed)
- Server runs on port 8080
- JPA DDL mode is `validate` (relies on Flyway for schema changes)

**Docker Compose** environment variables:
- `OPENAI_API_KEY` - Prepared for future LLM integration
- `JWT_SECRET` - Prepared for future authentication
- `CORS_ORIGINS` - Configure allowed origins

## Development Notes

### On Windows

This project was developed on Windows. Use `mvnw.cmd` instead of `./mvnw` when running Maven commands directly in Windows terminal (not WSL/Git Bash).

### Database Migrations

**Always use Flyway** for schema changes. Do not modify JPA entities and rely on `ddl-auto=create` or `update` as the mode is set to `validate`.

New migrations go in `chatq-assist-backend/src/main/resources/db/migration/` with naming pattern `V{version}__{description}.sql`.

### Multi-Tenant Considerations

When adding new entities:
1. Always include `tenant_id VARCHAR(255) NOT NULL` column
2. Add index on `(tenant_id, ...)` for common queries
3. Filter by tenant_id in repository queries
4. Accept `X-Tenant-ID` header in controllers

### CORS Configuration

The system currently uses `@CrossOrigin(origins = "*")` on controllers. For production, configure `CORS_ORIGINS` environment variable in docker-compose.yml to restrict allowed domains.

## Discrepancies with Documentation

The README.md describes a full RAG-based chatbot with:
- LangChain4j integration
- OpenAI embeddings and GPT-4
- Document ingestion from URLs/PDFs
- Vector similarity search
- Analytics and handoff logic

**Current implementation status**: Only the FAQ CRUD functionality exists. The RAG features, chat endpoints, document management, and analytics are not yet implemented but are planned according to the README.

When implementing these features, note:
- pgvector extension is already configured via Flyway
- Environment variables for OpenAI are already set up in docker-compose
- DTO classes exist for ChatRequest, ChatResponse, DocumentDto, AnalyticsDto
- Enums are defined for future use cases
- Ich kompiliere und starte das Backend.