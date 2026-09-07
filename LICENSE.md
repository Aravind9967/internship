# Telehealth Cervical Cancer Screening Backend

A Spring Boot backend for a telehealth cervical cancer screening workflow.  
It supports image upload, AI-based risk scoring, specialist review queue, and final decision recording.

## Features

- **Nurse upload**: Nurses can upload patient images with metadata.
- **AI classification**: Calls an external AI service to compute a risk score.
- **Risk levels**: Converts risk score into LOW / MEDIUM / HIGH.
- **Escalation queue**: Screenings are routed to specialists via an escalation queue.
- **Specialist review**: Specialists can view images and submit a final decision.
- **Audit logging**: Basic audit logs for key actions (upload, classify, result).

## Tech Stack

- Java 17+
- Spring Boot 3.x
- Spring Data JPA
- Lombok
- Validation (Jakarta)
- `RestTemplate` for calling external AI classifier

## Project Structure

```text
src/main/java/com/telehealth/screening
├─ Controllers
│   └─ ScreeningController.java
├─ Services
│   ├─ ScreeningService.java
│   ├─ ImageStorageService.java
│   ├─ AiClassifierClient.java
│   └─ EscalationService.java
├─ Repositories
│   ├─ ScreeningRepository.java
│   └─ EscalationRepository.java
├─ entity
│   ├─ Screening.java
│   └─ Escalation.java
├─ enums
│   ├─ RiskLevel.java
│   ├─ ScreeningStatus.java
│   └─ EscalationType.java
├─ DTOs
│   ├─ ClassifyResponse.java
│   ├─ ReviewQueueItem.java
│   └─ ReviewResultRequest.java
├─ Exceptions
│   ├─ BadRequestException.java
│   └─ ResourceNotFoundException.java
└─ config
    └─ AppConfig.java
```

## Configuration

Add these properties to `application.properties` (or `application.yml`):

```properties
screening.ai.classifier-url=http://localhost:8081
screening.image.storage-dir=/tmp/screening-images
```

You will also need:

- `ScreeningRepository` and `EscalationRepository` interfaces extending `JpaRepository`.
- `Screening` and `Escalation` entities with appropriate fields and relationships.
- Enum classes: `RiskLevel`, `ScreeningStatus`, `EscalationType`.
- DTOs: `ClassifyResponse`, `ReviewQueueItem`, `ReviewResultRequest`.

## Key Components

### AppConfig

Configures shared Spring beans. Currently, it provides a `RestTemplate` used by `AiClassifierClient` to call the external AI service.

```java
package com.telehealth.screening.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

### AiClassifierClient

Calls the external AI classification service and returns a risk score. If the AI service is unavailable, it falls back to a safe default score.

### ImageStorageService

Stores and retrieves screening images on the local filesystem (dev mode). In production, replace this with secure object storage (e.g., S3) and encryption.

### EscalationService

Manages the specialist review queue using an `Escalation` entity. Supports:

- Pushing a screening into the queue.
- Fetching the queue for a given escalation type.
- Marking an escalation as resolved after specialist review.

### ScreeningService

Orchestrates the main workflow:

- Upload → create `Screening`.
- Classify → call AI, store score, set risk level.
- Route → push to specialist queue.
- Get review queue → map escalations to `ReviewQueueItem`.
- Submit result → update screening status and resolve escalation.

### ScreeningController

Exposes REST endpoints for nurses and specialists to interact with the screening workflow.

## API Endpoints

All endpoints are under `/api`.

### 1. Upload screening image

```http
POST /api/screening/upload
Content-Type: multipart/form-data

patientId=123
nurseId=456
image=<file>
```

**Response:** `Screening` entity (JSON).

---

### 2. Classify screening (call AI)

```http
POST /api/screening/{id}/classify
```

**Response:**

```json
{
  "screeningId": 1,
  "riskScore": 0.78,
  "riskLevel": "HIGH",
  "status": "CLASSIFIED"
}
```

---

### 3. Route to specialist

```http
POST /api/screening/{id}/route-to-specialist
```

Moves the screening into the specialist review queue.

---

### 4. Specialist review queue

```http
GET /api/specialist/review-queue
```

**Response:** List of `ReviewQueueItem`:

```json
[
  {
    "escalationId": 10,
    "screeningId": 1,
    "riskScore": 0.78,
    "riskLevel": "HIGH",
    "imageDownloadUrl": "/screening/images/1"
  }
]
```

---

### 5. Submit specialist result

```http
POST /api/screening/{id}/result
Content-Type: application/json

{
  "specialistId": 999,
  "notes": "Confirmed high risk, refer immediately",
  "decision": "CONFIRMED"
}
```

`decision` can be: `CONFIRMED`, `OVERRIDDEN`, `REJECTED`.

---

### 6. View screening image

```http
GET /api/screening/images/{id}
```

Returns the image as `image/jpeg`.

---

## How it works (flow)

1. **Nurse** uploads image → `Screening` created with status `UPLOADED`.
2. System calls **AI classifier** → stores `riskScore` and `riskLevel`, status becomes `CLASSIFIED`.
3. If further review is needed, `route-to-specialist` creates an **Escalation** and sets status `WITH_SPECIALIST`.
4. **Specialist** fetches review queue, views image, and submits a result.
5. System updates `Screening` status (`CONFIRMED` / `OVERRIDDEN` / `REJECTED`) and resolves the escalation.

## Running locally

1. Ensure Java 17+ and Maven/Gradle are installed.
2. Configure `application.properties` as shown above.
3. Run:

```bash
./mvnw spring-boot:run
# or
./gradlew bootRun
```

4. Test endpoints using Postman, curl, or your frontend.

## Security notes (for production)

- Add authentication & role-based access control (e.g., nurse vs specialist).
- Encrypt stored images or use secure object storage (S3, etc.).
- Validate and sanitize all inputs.
- Use HTTPS and proper CORS settings.
- Replace the simple file-based image storage with a secure solution.
- Add proper error handling and structured logging.

## License

Add your chosen license here (e.g., MIT, Apache-2.0).
