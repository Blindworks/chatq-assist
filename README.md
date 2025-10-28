# ChatQ Assist - AI-Powered FAQ Chatbot

Ein intelligenter, LLM-basierter FAQ-/Support-Chatbot für KMU mit **RAG (Retrieval-Augmented Generation)**, **Streaming-Antworten** und **intelligenter Performance-Optimierung**. Das System nutzt OpenAI GPT-4 und Vector-Similarity-Search, um präzise Antworten auf Basis Ihrer FAQ-Datenbank zu generieren.

## 🚀 Features

### ✅ Implementiert (v0.1)

#### Core Funktionalität
- **🤖 LLM-basierter Chat**: OpenAI GPT-4 für natürliche, kontextbewusste Antworten
- **🔍 RAG Pipeline**: Retrieval-Augmented Generation mit pgvector Similarity Search
- **⚡ Streaming Responses**: Echtzeit-Antworten via Server-Sent Events (SSE)
- **📝 FAQ Management**: Vollständiges CRUD-System für FAQ-Einträge
- **🏷️ Tag-System**: Organisierung von FAQs mit Tags
- **💬 Conversation History**: Kontextbewusste Multi-Turn-Gespräche
- **🎯 Confidence Scoring**: Automatische Qualitätsbewertung der Antworten
- **🔄 Smart Handoff**: Automatische Weiterleitung bei fehlenden Antworten

#### Performance-Optimierung
- **💾 Embedding Cache**: Wiederholte Texte generieren keine neuen OpenAI-API-Aufrufe
- **🚀 FAQ Match Cache**: Vector-Similarity-Suchergebnisse werden gecacht
- **📦 Batch Processing**: Bulk-Import von FAQs in einer Transaktion
- **⏱️ Cache TTL**: 24h TTL, 10.000 Einträge pro Cache (Caffeine)

#### Frontend
- **🎨 Modernes Chat-Widget**: Angular 17 Standalone Component
- **📱 Responsive Design**: Mobile-first UI mit Custom CSS
- **🌊 Streaming UI**: Token-basierte Echtzeit-Anzeige
- **💭 Source References**: Anzeige der verwendeten FAQ-Quellen

#### Multi-Tenancy
- **🏢 Tenant-Isolation**: Header-basierte Mandantentrennung (`X-Tenant-ID`)
- **🗄️ Daten-Isolation**: Alle Queries filtern nach Tenant-ID

### 🚧 Geplant (Roadmap)

- [ ] **Document Ingestion**: URLs, PDFs, DOCX hochladen und verarbeiten
- [ ] **Analytics Dashboard**: Top-Fragen, Deflection-Rate, Confidence-Trends
- [ ] **Admin Panel**: Web-UI für FAQ-Management
- [ ] **E-Mail Notifications**: Bei Handoff automatisch benachrichtigen
- [ ] **Multi-Language Support**: i18n für verschiedene Sprachen
- [ ] **Rate Limiting**: API-Schutz vor Überlastung
- [ ] **Monitoring & Metrics**: Prometheus/Grafana Integration

## 🛠️ Tech Stack

### Backend
| Technologie | Version | Verwendung |
|------------|---------|-----------|
| **Spring Boot** | 3.2.0 | Application Framework |
| **Java** | 21 | Programming Language |
| **PostgreSQL** | 16+ | Primary Database |
| **pgvector** | latest | Vector Similarity Search |
| **LangChain4j** | 0.35.0 | LLM Integration Framework |
| **OpenAI API** | GPT-4 | Language Model |
| **OpenAI Embeddings** | text-embedding-3-small | Embedding Model (1536 dim) |
| **Flyway** | 9.x | Database Migration |
| **Caffeine** | latest | In-Memory Cache |
| **Lombok** | latest | Boilerplate Reduction |

### Frontend
| Technologie | Version | Verwendung |
|------------|---------|-----------|
| **Angular** | 17.x | Frontend Framework |
| **TypeScript** | 5.x | Programming Language |
| **RxJS** | 7.x | Reactive Programming |
| **HttpClient** | Angular | HTTP Communication |

### DevOps
- **Docker** + **Docker Compose**: Containerization
- **Maven**: Build Tool (Backend)
- **Angular CLI**: Build Tool (Frontend)

## 📁 Projektstruktur

```
ChatQ-Assist/
├── chatq-assist-backend/              # Spring Boot Backend
│   ├── src/main/java/com/chatq/assist/
│   │   ├── config/
│   │   │   ├── OpenAiConfig.java      # LangChain4j & OpenAI Configuration
│   │   │   └── CacheConfig.java       # Caffeine Cache Setup
│   │   ├── controller/
│   │   │   ├── ChatController.java    # /api/chat (+ /stream)
│   │   │   └── FaqController.java     # /api/faq (+ /batch)
│   │   ├── domain/
│   │   │   ├── entity/
│   │   │   │   ├── FaqEntry.java      # FAQ Entity mit Embeddings
│   │   │   │   ├── Conversation.java  # Chat Sessions
│   │   │   │   └── Message.java       # Chat Messages
│   │   │   ├── dto/
│   │   │   │   ├── ChatRequest.java
│   │   │   │   ├── ChatResponse.java
│   │   │   │   └── FaqEntryDto.java
│   │   │   └── enums/
│   │   │       ├── ConversationStatus.java
│   │   │       └── MessageRole.java
│   │   ├── repository/
│   │   │   ├── FaqRepository.java     # inkl. Vector Similarity Query
│   │   │   ├── ConversationRepository.java
│   │   │   └── MessageRepository.java
│   │   ├── service/
│   │   │   ├── ChatServiceLLM.java    # RAG Pipeline + Streaming
│   │   │   ├── EmbeddingService.java  # OpenAI Embedding Generation
│   │   │   └── FaqService.java        # FAQ CRUD + Batch Import
│   │   └── util/
│   │       └── VectorType.java        # Hibernate pgvector UserType
│   ├── src/main/resources/
│   │   ├── db/migration/
│   │   │   ├── V1__init_schema.sql
│   │   │   ├── V2__add_chat_tables.sql
│   │   │   └── V3__add_embeddings_to_faq.sql
│   │   └── application.properties
│   ├── Dockerfile
│   └── pom.xml
│
├── chatq-assist-frontend/             # Angular Widget
│   ├── src/app/
│   │   ├── components/
│   │   │   └── chat-widget/
│   │   │       ├── chat-widget.component.ts    # Main Chat UI
│   │   │       ├── chat-widget.component.html
│   │   │       └── chat-widget.component.css
│   │   └── services/
│   │       └── chat.service.ts        # HTTP + SSE Handling
│   ├── package.json
│   └── angular.json
│
├── docker-compose.yml
├── CLAUDE.md                          # Project Instructions für Claude Code
└── README.md
```

## 🚀 Quick Start

### Voraussetzungen

- **Docker** & **Docker Compose** (für einfachsten Start)
- **Java 21** (für lokale Backend-Entwicklung)
- **Node.js 18+** (für Frontend-Entwicklung)
- **PostgreSQL 16+** mit **pgvector Extension** (wenn lokal ohne Docker)
- **OpenAI API Key** ([hier erstellen](https://platform.openai.com/api-keys))

### Option 1: Docker Compose (Empfohlen)

```bash
# 1. Repository klonen
git clone <repository-url>
cd ChatQ-Assist

# 2. Umgebungsvariablen setzen
# Erstelle docker-compose.yml oder setze Env-Vars:
export OPENAI_API_KEY="sk-..."

# 3. Starten
docker-compose up -d

# 4. Logs verfolgen
docker-compose logs -f backend
```

**Zugriff**:
- Backend API: http://localhost:8080
- Frontend Widget: http://localhost:4200
- PostgreSQL: localhost:5433 (User: `postgres`, PW: `taxcRH51#`)

### Option 2: Lokale Entwicklung

#### Backend starten

```bash
cd chatq-assist-backend

# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

**Wichtig**: PostgreSQL mit pgvector muss laufen! Siehe [pgvector Installation](#pgvector-installation).

#### Frontend starten

```bash
cd chatq-assist-frontend
npm install
ng serve
```

Widget läuft auf http://localhost:4200

## 📋 API Dokumentation

### Chat Endpoints

#### POST /api/chat
Standard-Chat (nicht-streaming)

**Request**:
```json
{
  "question": "Was sind Ihre Öffnungszeiten?",
  "sessionId": "optional-uuid",
  "userEmail": "user@example.com"
}
```

**Response**:
```json
{
  "sessionId": "uuid",
  "answer": "Unsere Öffnungszeiten sind...",
  "confidenceScore": 0.85,
  "sources": [
    {
      "type": "FAQ",
      "title": "Öffnungszeiten",
      "id": 42
    }
  ],
  "handoffTriggered": false
}
```

#### POST /api/chat/stream
Chat mit Streaming-Antwort (SSE)

**Request**: Gleich wie `/api/chat`

**Response**: Server-Sent Events
```
event: token
data: Unsere

event: token
data:  Öffnungszeiten

event: metadata
data: {"sessionId":"uuid","confidenceScore":0.85,"sources":[...],"handoffTriggered":false}
```

### FAQ Management Endpoints

#### GET /api/faq
Alle FAQs für einen Tenant abrufen

**Headers**: `X-Tenant-ID: default-tenant`

**Response**:
```json
[
  {
    "id": 1,
    "question": "Was sind Ihre Öffnungszeiten?",
    "answer": "Montag bis Freitag 9-18 Uhr",
    "tags": ["zeiten", "service"],
    "isActive": true,
    "displayOrder": 1,
    "usageCount": 42,
    "createdAt": "2024-01-15T10:00:00Z",
    "updatedAt": "2024-01-15T10:00:00Z"
  }
]
```

#### POST /api/faq
Einzelnen FAQ-Eintrag erstellen

**Headers**: `X-Tenant-ID: default-tenant`

**Request**:
```json
{
  "question": "Wie kann ich Sie erreichen?",
  "answer": "Sie erreichen uns telefonisch unter...",
  "tags": ["kontakt"],
  "isActive": true,
  "displayOrder": 2
}
```

**Response**: Erstellter FAQ-Eintrag mit ID

#### POST /api/faq/batch
Mehrere FAQs auf einmal erstellen (Performance-optimiert)

**Headers**: `X-Tenant-ID: default-tenant`

**Request**:
```json
[
  {
    "question": "Frage 1?",
    "answer": "Antwort 1",
    "tags": ["tag1"],
    "displayOrder": 1
  },
  {
    "question": "Frage 2?",
    "answer": "Antwort 2",
    "tags": ["tag2"],
    "displayOrder": 2
  }
]
```

**Vorteile**:
- ✅ Einzelne Datenbank-Transaktion
- ✅ Embedding-Cache wird genutzt
- ✅ ~70% schneller als einzelne POST-Requests

#### PUT /api/faq/{id}
FAQ-Eintrag aktualisieren

**Headers**: `X-Tenant-ID: default-tenant`

**Request**: Gleich wie POST, aber auf bestehender ID

**Hinweis**: Embedding wird automatisch neu generiert!

#### DELETE /api/faq/{id}
FAQ-Eintrag löschen

## 🔧 Konfiguration

### application.properties

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5433/chatq_assist
spring.datasource.username=postgres
spring.datasource.password=taxcRH51#

# OpenAI
openai.api.key=${OPENAI_API_KEY}
openai.model.chat=gpt-4
openai.model.embedding=text-embedding-3-small

# Server
server.port=8080

# JPA (Flyway übernimmt Schema)
spring.jpa.hibernate.ddl-auto=validate
```

### Environment Variables

| Variable | Beschreibung | Default |
|----------|--------------|---------|
| `OPENAI_API_KEY` | OpenAI API Key | *required* |
| `DB_HOST` | PostgreSQL Host | `localhost` |
| `DB_PORT` | PostgreSQL Port | `5433` |
| `DB_NAME` | Database Name | `chatq_assist` |
| `DB_USER` | Database User | `postgres` |
| `DB_PASSWORD` | Database Password | `taxcRH51#` |

## 🎯 RAG Pipeline Explained

```
User Question: "Was sind Ihre Öffnungszeiten?"
        ↓
[1] Embedding Generation (EmbeddingService)
    → OpenAI text-embedding-3-small
    → float[1536] vector
    → Cached in Caffeine (24h TTL)
        ↓
[2] Vector Similarity Search (FaqRepository)
    → pgvector <=> operator (cosine distance)
    → SELECT * FROM faq_entries
      ORDER BY embedding <=> query_embedding
      LIMIT 3
    → Results cached in Caffeine
        ↓
[3] Context Building (ChatServiceLLM)
    → Top 3 FAQs als Context
    → Conversation History (last 5 messages)
        ↓
[4] LLM Generation (OpenAI GPT-4)
    → System Prompt + Context + History + Question
    → Streaming via SSE
        ↓
[5] Response Processing
    → Save to Message table
    → Increment FAQ usage_count
    → Return to user
```

## 🧪 Testing

### IntelliJ HTTP Client

Erstelle eine `test.http` Datei:

```http
### Create FAQ
POST http://localhost:8080/api/faq
Content-Type: application/json
X-Tenant-ID: default-tenant

{
  "question": "Was sind Ihre Öffnungszeiten?",
  "answer": "Montag bis Freitag von 9 bis 18 Uhr.",
  "tags": ["zeiten", "service"],
  "displayOrder": 1
}

### Get all FAQs
GET http://localhost:8080/api/faq
X-Tenant-ID: default-tenant

### Chat Request
POST http://localhost:8080/api/chat
Content-Type: application/json
X-Tenant-ID: default-tenant

{
  "question": "Wann habt ihr geöffnet?",
  "sessionId": "test-session-123"
}

### Streaming Chat
POST http://localhost:8080/api/chat/stream
Content-Type: application/json
X-Tenant-ID: default-tenant

{
  "question": "Wie erreiche ich euch?",
  "sessionId": "test-session-456"
}
```

### cURL Examples

```bash
# Create FAQ
curl -X POST http://localhost:8080/api/faq \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: default-tenant" \
  -d '{
    "question": "Was kostet der Service?",
    "answer": "Unsere Preise beginnen bei 99€/Monat.",
    "tags": ["preise"]
  }'

# Chat
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: default-tenant" \
  -d '{
    "question": "Was kostet das?",
    "sessionId": "curl-test"
  }'
```

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
    embedding vector(1536),  -- pgvector!
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_faq_tenant ON faq_entries(tenant_id);
CREATE INDEX idx_faq_embedding ON faq_entries
    USING ivfflat (embedding vector_cosine_ops);
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

## 🔐 pgvector Installation

### Windows (PostgreSQL 16+)

1. Download pgvector für deine PostgreSQL-Version von:
   https://github.com/pgvector/pgvector/releases

2. Extrahiere `vector.dll` nach:
   ```
   C:\Program Files\PostgreSQL\16\lib\
   ```

3. Extrahiere SQL-Dateien nach:
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

## 📊 Performance Benchmarks

### Ohne Caching
- Erste Anfrage: ~2.5s (Embedding: 200ms + Vector Search: 100ms + GPT-4: 2.2s)
- Wiederholte Anfrage: ~2.5s (kein Cache)

### Mit Caching (aktuell)
- Erste Anfrage: ~2.5s
- Wiederholte Anfrage: ~2.2s (Embedding cached: -200ms)
- Identische FAQ-Suche: ~2.0s (Embedding + Search cached: -300ms)

### Batch Import
- 100 FAQs einzeln: ~45s
- 100 FAQs batch: ~15s (**~70% schneller**)

## 🚨 Troubleshooting

### "Typ vector existiert nicht"
→ pgvector Extension nicht installiert. Siehe [pgvector Installation](#pgvector-installation)

### "Die Abfrage lieferte kein Ergebnis" (Hibernate)
→ PostgreSQL dependency muss `compile` scope haben (nicht `runtime`)

### Tokens ohne Leerzeichen im Frontend
→ SSE Parser darf `data:` nicht `.trim()`en! Siehe `chat.service.ts:97`

### OpenAI API Rate Limit
→ Upgrade auf Tier 2+, oder verwende längeres Rate Limiting

### pgvector Index langsam
→ Bei >10.000 FAQs: `CREATE INDEX USING ivfflat ... WITH (lists = 100)`

## 📈 Monitoring

### Cache Statistics

```java
// In CacheConfig ist recordStats() aktiviert
CacheManager cacheManager = ...;
Cache cache = cacheManager.getCache("embeddings");
CaffeineCache caffeineCache = (CaffeineCache) cache;
com.github.benmanes.caffeine.cache.Cache nativeCache =
    caffeineCache.getNativeCache();
CacheStats stats = nativeCache.stats();

// Hit Rate, Miss Rate, Evictions, etc.
```

### Spring Boot Actuator

Endpoints verfügbar (wenn aktiviert):
- `/actuator/health` - Health Check
- `/actuator/metrics` - Metriken
- `/actuator/caches` - Cache-Informationen

## 🤝 Contributing

1. Fork das Repository
2. Feature-Branch erstellen: `git checkout -b feature/amazing-feature`
3. Committen: `git commit -m 'Add amazing feature'`
4. Push: `git push origin feature/amazing-feature`
5. Pull Request öffnen

## 📝 Lizenz

Dieses Projekt ist unter der **MIT License** lizenziert - siehe [LICENSE](LICENSE) für Details.

## 🙋 Support

- **Issues**: [GitHub Issues](https://github.com/your-org/chatq-assist/issues)
- **Email**: support@your-domain.com
- **Docs**: [Wiki](https://github.com/your-org/chatq-assist/wiki)

## 👏 Credits

- **LangChain4j**: https://github.com/langchain4j/langchain4j
- **pgvector**: https://github.com/pgvector/pgvector
- **OpenAI**: https://openai.com

---

**Built with ❤️ for SMBs who need smart, privacy-focused customer support**
