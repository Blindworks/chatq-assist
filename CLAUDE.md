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

## Frontend Design System

**Reference Component**: `tenant-management` component serves as the design system reference. All frontend components should follow this consistent style.

### Color Palette

**Text Colors:**
- Primary text: `#0f172a` (dark slate)
- Secondary text: `#334155`, `#475569`, `#64748b` (gray shades)

**Background Colors:**
- Light background: `#f8fafc`
- Card background: `white`
- Secondary background: `#f1f5f9`

**Border Colors:**
- Primary border: `#e2e8f0`
- Hover border: `#cbd5e1`

**Status Colors:**
- Active: Background `#dcfce7`, Text `#166534` (green)
- Inactive: Background `#fee2e2`, Text `#991b1b` (red)
- Error: Background `#fef2f2`, Text `#991b1b`, Border `#fecaca`

**Brand Colors:**
- Primary: Gradient `#3b82f6` → `#2563eb` (blue)

### Typography

**Font Sizes:**
- Header (h2): `22px`, font-weight `700`
- Modal header (h3): `20px`, font-weight `700`
- Body text: `14px`
- Labels: `14px`, font-weight `600`
- Small text: `11px` - `13px`

### Buttons

**Primary Button:**
```css
background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
color: white;
box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
border-radius: 10px;
padding: 10px 20px;
font-weight: 600;
```
- Hover: `transform: translateY(-2px)` + stronger shadow

**Secondary Button:**
```css
background: #f1f5f9;
color: #475569;
border: 1px solid #e2e8f0;
border-radius: 10px;
padding: 10px 20px;
font-weight: 600;
```
- Hover: Background `#e2e8f0`, Color `#334155`

**Small Buttons (btn-sm):**
- Padding: `8px 16px`
- Font-size: `13px`

### Form Controls

**Input Fields:**
```css
padding: 12px 16px;
border: 2px solid #e2e8f0;
border-radius: 10px;
font-size: 14px;
```
- Focus state: `border-color: #3b82f6`, `box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1)`

### Cards & Containers

**Card Style:**
```css
background: white;
border-radius: 12px;
padding: 24px;
box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
border: 1px solid #e2e8f0;
```
- Hover: `transform: translateY(-2px)` + stronger shadow

### Status Badges

**Badge Style:**
```css
padding: 6px 14px;
border-radius: 20px;  /* pill shape */
font-size: 11px;
font-weight: 700;
text-transform: uppercase;
letter-spacing: 0.5px;
display: inline-flex;
align-items: center;
gap: 6px;
```

### Modal

**Modal Overlay:**
```css
background: rgba(15, 23, 42, 0.6);
backdrop-filter: blur(4px);
```

**Modal Content:**
```css
border-radius: 16px;
box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
animation: slideUp 0.3s cubic-bezier(0.4, 0, 0.2, 1);
```

### Animations

**Transition Timing:**
- Use `cubic-bezier(0.4, 0, 0.2, 1)` for smooth animations
- Duration: `0.2s` for most transitions

**Hover Effects:**
- Cards & Primary buttons: `transform: translateY(-2px)`
- Duration: `0.2s`

### Spacing

**Padding & Margins:**
- Small: `8px`, `12px`
- Medium: `16px`, `20px`, `24px`
- Large: `28px`, `32px`

**Gaps:**
- Icon gaps: `8px`
- Button groups: `8px` - `12px`
- Form fields: `20px` - `24px`

### Icons

- Use Lucide Angular icons consistently
- Standard size: `16px` - `20px`
- Header icons: `24px`
- Small icons (badges, buttons): `14px`

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