# ProntoCAPS (MVP Back-end)

Stack:
- Java 17
- Spring Boot 3
- PostgreSQL
- Docker / docker-compose
- JWT (Auth)
- Swagger/OpenAPI

## Rodar com Docker
```bash
docker compose up --build
```
Swagger:
- http://localhost:8080/swagger

## Fluxo rapido (Postman/Insomnia)
1) Register
POST http://localhost:8080/auth/register
```json
{ "username":"med1", "password":"123456", "roles":"ROLE_MEDICO" }
```

2) Login
POST http://localhost:8080/auth/login
```json
{ "username":"med1", "password":"123456" }
```

Use o token:
Authorization: Bearer <token>

3) Criar paciente
POST http://localhost:8080/pacientes

4) Criar prontuario
POST http://localhost:8080/pacientes/{id}/prontuarios

## Roles do MVP
- ROLE_ADMIN (gestao do sistema)
- ROLE_MEDICO
- ROLE_PSI (psicologo)
- ROLE_ENFERMEIRO
- ROLE_ASSISTENTE_SOCIAL
