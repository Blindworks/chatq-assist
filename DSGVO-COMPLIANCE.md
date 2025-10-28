# DSGVO-Compliance Dokumentation

## Übersicht

Dieses Dokument beschreibt die DSGVO-Compliance-Maßnahmen für das ChatQ Assist System.

## Rechtsgrundlagen

Das System verarbeitet personenbezogene Daten auf Grundlage von:
- **Art. 6 Abs. 1 lit. f DSGVO**: Berechtigtes Interesse (Customer Support)
- **Art. 6 Abs. 1 lit. a DSGVO**: Einwilligung (wenn E-Mail für Handoff angegeben wird)

## Verarbeitete Daten

### Pflichtdaten
- Session-ID (UUID, technisch notwendig)
- Chat-Nachrichten (Fragen & Antworten)
- Timestamps

### Optionale Daten
- E-Mail-Adresse (nur bei Handoff)
- User-Agent (technische Metadaten)

### KEINE Speicherung von
- IP-Adressen (werden nicht geloggt)
- Cookies (außer Session-ID im localStorage)
- Biometrische Daten
- Finanzinformationen

## Implementierte DSGVO-Maßnahmen

### 1. Datenminimierung (Art. 5 Abs. 1 lit. c DSGVO)
- **Anonymisierung**: Fragen werden als SHA-256 Hash gespeichert (Analytics)
- **Keine IP-Speicherung**: IP-Adressen werden nicht persistiert
- **Session-basiert**: Keine User-Accounts, nur temporäre Sessions

### 2. Zweckbindung (Art. 5 Abs. 1 lit. b DSGVO)
```java
// chatq-assist-backend/src/main/java/com/chatq/assist/service/ChatService.java:171
private String hashQuestion(String question) {
    // SHA-256 Hash für Privacy-Compliance
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    return String.format("%064x", new BigInteger(1, hash));
}
```

### 3. Speicherbegrenzung (Art. 5 Abs. 1 lit. e DSGVO)
```yaml
# chatq-assist-backend/src/main/resources/application.yml:48
chatq:
  analytics:
    retention-days: 90  # Automatische Löschung nach 90 Tagen
```

### 4. Transparenz (Art. 12 DSGVO)
- Privacy Notice im Chat-Widget angezeigt
- Quellen-Angaben bei jeder Antwort
- Nutzer wird über Handoff informiert

### 5. Sicherheit (Art. 32 DSGVO)
- HTTPS (in Produktion erforderlich)
- JWT-Authentication (für Admin-Bereich)
- CORS-Restriktion
- Keine Stacktrace-Exposition

```java
// chatq-assist-backend/src/main/java/com/chatq/assist/security/SecurityConfig.java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // CSRF-Schutz, CORS-Konfiguration, Session-Management
}
```

### 6. Datenübermittlung an Drittländer

#### OpenAI (USA)
- **Rechtsgrundlage**: EU-DPA (Data Processing Agreement) verfügbar
- **Standardvertragsklauseln**: OpenAI bietet EU SCCs an
- **Empfehlung**: EU-Endpoint nutzen (wenn verfügbar)
  ```yaml
  base-url: https://eu.api.openai.com/v1
  ```

## Betroffenenrechte

### Art. 15 DSGVO - Auskunftsrecht
**Umsetzung**:
- Session-ID als Identifikator
- Endpoint (TODO): `GET /api/data-subject/export?sessionId={id}`

### Art. 16 DSGVO - Berichtigungsrecht
**Umsetzung**:
- Chat-Historie kann nicht berichtigt werden (Audit-Trail)
- E-Mail-Adresse kann aktualisiert werden

### Art. 17 DSGVO - Recht auf Löschung
**Umsetzung**:
- Endpoint (TODO): `DELETE /api/data-subject/delete?sessionId={id}`
- Automatische Löschung nach Retention-Period

### Art. 20 DSGVO - Datenübertragbarkeit
**Umsetzung**:
- Export als JSON via `/api/data-subject/export`

### Art. 21 DSGVO - Widerspruchsrecht
**Umsetzung**:
- Chat kann jederzeit geschlossen werden
- Session-Daten werden gelöscht

## Technische und Organisatorische Maßnahmen (TOM)

### Zutrittskontrolle
- Server in EU-Rechenzentrum (z.B. AWS eu-central-1)
- Physische Sicherheit durch Hosting-Provider

### Zugangskontrolle
- JWT-basierte Admin-Authentifizierung
- Passwort-Policy (TODO: implementieren)
- 2FA für Admin-Bereich (TODO)

### Zugriffskontrolle
- Role-Based Access Control (RBAC) vorbereitet
- Tenant-Isolation via `tenant_id` Spalte

### Weitergabekontrolle
- HTTPS/TLS-Verschlüsselung
- CORS-Whitelist
- No Cross-Tenant Data Access

### Eingabekontrolle
- Audit-Trail via JPA Auditing (`created_at`, `updated_at`)
- Change-Tracking via Version-Spalte

### Auftragskontrolle
- Separate Tenant-IDs
- Datenbanktrennung möglich (Multi-Tenancy)

### Verfügbarkeitskontrolle
- Database Backups (TODO: implementieren)
- Health Checks via Actuator
- Docker Restart-Policy

### Trennungsgebot
- Tenant-Isolation auf Datenbank-Ebene
- Keine Shared-Data zwischen Tenants

## Datenverarbeitungsvertrag (AVV/DPA)

### Mit OpenAI
- [ ] EU DPA unterzeichnen
- [ ] SCCs (Standard Contractual Clauses) akzeptieren
- [ ] Privacy Shield Alternative prüfen

### Mit Hosting-Provider
- [ ] AVV abschließen
- [ ] EU-Hosting sicherstellen
- [ ] Sub-Processor-Liste erhalten

## Datenschutz-Folgenabschätzung (DSFA)

**Bewertung**: Keine DSFA erforderlich, da:
- Keine automatisierte Einzelentscheidung (Art. 22 DSGVO)
- Keine umfangreiche Verarbeitung besonderer Kategorien (Art. 9 DSGVO)
- Kein systematisches Monitoring

**Empfehlung**: Bei >10.000 Nutzern/Monat DSFA durchführen

## Meldepflichten

### Datenpanne (Art. 33 DSGVO)
**Prozess**:
1. Erkennung: Monitoring-Alerts
2. Dokumentation: Incident-Log führen
3. Meldung: Innerhalb 72h an Aufsichtsbehörde
4. Benachrichtigung: Betroffene bei hohem Risiko

**Technische Maßnahmen**:
- Logging aktivieren
- Alerting konfigurieren
- Backup-Strategie

## Checkliste für Produktions-Deployment

- [ ] **Datenschutzerklärung** auf Website verlinken
- [ ] **Cookie-Banner** implementieren (falls Cookies genutzt)
- [ ] **Impressum** mit Datenschutzbeauftragten
- [ ] **OpenAI EU DPA** unterzeichnen
- [ ] **Hosting-Provider AVV** abschließen
- [ ] **SSL/TLS-Zertifikate** konfigurieren
- [ ] **EU-Server** für Deployment wählen
- [ ] **Backup-Strategie** implementieren
- [ ] **Incident Response Plan** dokumentieren
- [ ] **Data-Subject-Rights-Endpoints** implementieren
- [ ] **Admin-Authentifizierung** aktivieren
- [ ] **Log-Retention** auf 90 Tage begrenzen
- [ ] **Monitoring & Alerting** einrichten

## Nützliche Ressourcen

- DSGVO-Text: https://dsgvo-gesetz.de/
- OpenAI DPA: https://openai.com/policies/data-processing-addendum
- EU SCC Templates: https://ec.europa.eu/info/law/law-topic/data-protection/international-dimension-data-protection/standard-contractual-clauses-scc_en

## Kontakt

**Datenschutzbeauftragter**: datenschutz@example.com
**Support**: support@example.com

---

**Letzte Aktualisierung**: 2025-01-23
**Version**: 1.0
