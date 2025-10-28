# Quick Start Guide

## 5-Minuten Setup

### 1. Umgebung vorbereiten

```bash
# .env Datei erstellen
cp .env.example .env

# OpenAI API Key eintragen
# Öffne .env und füge ein:
# OPENAI_API_KEY=sk-your-actual-api-key-here
```

### 2. System starten

```bash
# Alle Services starten (PostgreSQL, Backend, Frontend)
docker-compose up -d

# Logs verfolgen
docker-compose logs -f backend
```

### 3. Erste FAQ erstellen

```bash
curl -X POST http://localhost:8080/api/faq \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: default-tenant" \
  -d '{
    "question": "Was sind Ihre Öffnungszeiten?",
    "answer": "Wir sind Montag bis Freitag von 9:00 bis 18:00 Uhr erreichbar.",
    "tags": ["öffnungszeiten", "kontakt"],
    "isActive": true
  }'
```

### 4. Dokument hochladen (optional)

```bash
# PDF hochladen
curl -X POST http://localhost:8080/api/documents/upload \
  -H "X-Tenant-ID: default-tenant" \
  -F "file=@/pfad/zu/dokument.pdf" \
  -F "title=Produktdokumentation" \
  -F "documentType=PDF"

# Oder URL ingestieren
curl -X POST http://localhost:8080/api/documents/url \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: default-tenant" \
  -d '{
    "title": "Website FAQ",
    "sourceUrl": "https://example.com/faq",
    "documentType": "URL"
  }'
```

### 5. Chat testen

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: default-tenant" \
  -d '{
    "question": "Wann habt ihr offen?"
  }'
```

Erwartete Antwort:
```json
{
  "sessionId": "abc-123-...",
  "answer": "Wir sind Montag bis Freitag von 9:00 bis 18:00 Uhr erreichbar.",
  "confidenceScore": 0.95,
  "sources": [
    {
      "type": "FAQ",
      "title": "Was sind Ihre Öffnungszeiten?",
      "id": 1
    }
  ],
  "handoffTriggered": false
}
```

### 6. Widget im Browser testen

Öffne http://localhost:80 in deinem Browser. Du siehst:
- Einen Chat-Button rechts unten
- Klicke darauf und stelle eine Frage
- Toggle zwischen Light/Dark Theme

## Entwicklungsmodus

### Backend lokal (ohne Docker)

```bash
cd chatq-assist-backend

# PostgreSQL manuell starten (oder Docker)
docker run -d \
  -e POSTGRES_DB=chatq_assist \
  -e POSTGRES_USER=chatq \
  -e POSTGRES_PASSWORD=changeme \
  -p 5432:5432 \
  pgvector/pgvector:pg16

# Spring Boot starten
./mvnw spring-boot:run
```

### Frontend lokal

```bash
cd chatq-assist-frontend

# Dependencies installieren
npm install

# Dev-Server starten
ng serve

# Browser: http://localhost:4200
```

## Wichtige Endpoints

| Endpoint | Methode | Beschreibung |
|----------|---------|--------------|
| `/api/chat` | POST | Chat-Nachricht senden |
| `/api/faq` | GET/POST/PUT/DELETE | FAQ-Management |
| `/api/documents` | GET/POST/DELETE | Dokument-Management |
| `/api/analytics?days=30` | GET | Analytics abrufen |
| `/actuator/health` | GET | Health Check |

## Troubleshooting

### Backend startet nicht
```bash
# Logs prüfen
docker-compose logs backend

# Häufige Probleme:
# - OpenAI API Key fehlt in .env
# - PostgreSQL nicht erreichbar
# - Port 8080 bereits belegt
```

### Frontend lädt nicht
```bash
# Build prüfen
cd chatq-assist-frontend
npm run build

# Nginx-Logs
docker-compose logs frontend
```

### pgvector Extension fehlt
```bash
# In PostgreSQL Container einloggen
docker exec -it chatq-postgres psql -U chatq -d chatq_assist

# Extension manuell erstellen
CREATE EXTENSION IF NOT EXISTS vector;
```

### OpenAI Rate Limit
- Warte 60 Sekunden
- Prüfe dein OpenAI Tier/Quota
- Reduziere `RAG_TOP_K` in .env

## Nächste Schritte

1. **Mehr FAQs hinzufügen**: Nutze die `/api/faq` Endpoints
2. **Dokumente ingestieren**: PDFs, DOCX, oder ganze Websites
3. **Analytics prüfen**: `/api/analytics?days=7`
4. **Widget anpassen**: Ändere Farben in `chat-widget.component.css`
5. **DSGVO prüfen**: Lies `DSGVO-COMPLIANCE.md`

## Production Deployment

Siehe `README.md` für:
- SSL/TLS-Konfiguration
- Managed Services (AWS/Azure/GCP)
- Kubernetes-Deployment
- Backup-Strategie

## Support

Bei Fragen oder Problemen:
- Prüfe `README.md` für Details
- Schau in `DSGVO-COMPLIANCE.md` für Compliance-Fragen
- Öffne ein Issue im Repository
