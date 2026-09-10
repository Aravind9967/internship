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



# Cervical Screening Backend

Spring Boot backend for nurse-uploaded patient screening images, AI risk classification, and specialist review routing.

## Tech Stack

- Java 21
- Spring Boot 4.1
- Spring Data JPA
- Spring Security (Basic Auth)
- MySQL
- File-based Image Storage

## Features

- Nurse can upload screening images
- AI classification (LOW / MEDIUM / HIGH risk)
- Route screening to Specialist
- Specialist can confirm or override the result
- Secure image viewing
- Audit logging

## Project Structure
src/main/java/com/screenings/cervical_screenings/
├── Controllers/
├── Services/
├── Entitys/
├── Respository/
├── DTOs/
├── Config/
└── CervicalScreeningsApplication.java
text## How to Run

### 1. Create Database
```sql
CREATE DATABASE hospitals;
2. Update application.properties
propertiesspring.datasource.url=jdbc:mysql://localhost:3306/hospitals
spring.datasource.username=root
spring.datasource.password=your_password
server.port=8081
storage.images.base-path=./secure-images
3. Run the Application
Bash./mvnw spring-boot:run
Application will start on: http://localhost:8081

API Endpoints

MethodEndpointDescriptionRolePOST/users/registerRegister a new userPublicPOST/api/screening/uploadNurse uploads screening imageNURSEPOST/api/screening/{id}/classifyRun AI classificationNURSE / SPECIALISTPOST/api/screening/{id}/route-to-specialistRoute to specialistNURSEGET/api/screening/images/{id}View stored imageNURSE / SPECIALISTPOST/api/screening/{id}/resultSpecialist confirms / overridesSPECIALIST

Authentication
This project uses HTTP Basic Authentication.
Create Users
Register Nurse:
httpPOST /users/register
username=nurse1
password=password123
role=NURSE
Register Specialist:
httpPOST /users/register
username=specialist1
password=password123
role=SPECIALIST

Testing with Postman

Register a Nurse and a Specialist
Login using Basic Auth
Upload an image
Classify the screening
Route to Specialist
View the image
Specialist submits result



