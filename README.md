# Telehealth Screening Service

Spring Boot backend for nurse-uploaded patient screening images,
AI risk classification, and specialist review routing.

## Stack
Java 21, Spring Boot 4.1, Spring Data JPA, MySQL

## Run locally
1. Create MySQL DB `telehealth_db`
2. Set env vars: `DB_USERNAME`, `DB_PASSWORD`
3. `./mvnw spring-boot:run` (port 9090)

## API
| Method | Endpoint | Purpose |
|---|---|---|
| POST | /api/screening/upload | Nurse uploads image + patient/nurse IDs |
| POST | /api/screening/{id}/classify | Runs AI classifier, stores risk score |
| POST | /api/screening/{id}/route-to-specialist | Pushes to review queue |
| GET  | /api/specialist/review-queue | Specialist-facing queue |
| POST | /api/screening/{id}/result | Specialist confirms/overrides |
| GET  | /api/screening/images/{id} | View stored image |
