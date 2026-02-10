# ? ProntoCAPS — MVP Back-end

ProntoCAPS é um **MVP de API back-end** desenvolvido para o hackathon da pós-graduação, com foco na **gestão de prontuários eletrônicos de pacientes atendidos pelo CAPS**, priorizando **segurança, versionamento de dados clínicos e regras de negócio claras**.

O projeto foi desenvolvido com **arquitetura em camadas**, foco em **domínio** e **cobertura de testes**, atendendo aos requisitos de um sistema de saúde pública.

---

## ? Stack Tecnológica

- **Java 17**
- **Spring Boot 3**
- **Spring Security + JWT**
- **Spring Data JPA**
- **PostgreSQL**
- **Docker & Docker Compose**
- **Swagger / OpenAPI**
- **JUnit 5 + Mockito**
- **JaCoCo (cobertura de testes)**

---

## ? Arquitetura

O projeto segue uma arquitetura em camadas:

- **Controller** — exposição de endpoints REST
- **Service** — regras de negócio e validações
- **Repository** — acesso a dados via JPA
- **DTOs** — isolamento de contratos de entrada e saída
- **Security/Auth** — autenticação e autorização via JWT

? As regras de negócio estão concentradas na camada de serviço.

---

## ? Principais Funcionalidades

### ? Paciente
- Cadastro de pacientes
- Atualização de dados
- Inativação lógica
- Validação de CPF único
- Bloqueio de inativação caso exista prontuário ativo

### ?? Prontuário
- Criação do primeiro prontuário
- **Versionamento de prontuários**
- Histórico clínico imutável
- Apenas uma versão ativa por paciente
- Inativação lógica
- Associação obrigatória a paciente ativo

### ? Segurança
- Autenticação via JWT
- Controle de acesso por perfil (RBAC)

---

## ? Roles do MVP

- `ROLE_ADMIN` — gestão do sistema
- `ROLE_MEDICO`
- `ROLE_PSI` — psicólogo
- `ROLE_ENFERMEIRO`
- `ROLE_ASSISTENTE_SOCIAL`

---

## ?? Executando o Projeto com Docker

```bash
docker compose up --build
```

A API ficará disponível em:

```
http://localhost:8080
```

---

## ? Swagger / OpenAPI

Documentação interativa da API:

```
http://localhost:8080/swagger
```

---

## ? Fluxo Rápido de Uso (Postman / Insomnia)

### 1?? Registro de usuário
**POST** `/auth/register`

```json
{
  "username": "med1",
  "password": "123456",
  "roles": "ROLE_MEDICO"
}
```

### 2?? Login
**POST** `/auth/login`

```json
{
  "username": "med1",
  "password": "123456"
}
```

Utilize o token retornado:

```
Authorization: Bearer <token>
```

### 3?? Criar paciente
**POST** `/pacientes`

### 4?? Criar primeiro prontuário
**POST** `/pacientes/{id}/prontuarios`

### 5?? Criar nova versão do prontuário
**POST** `/pacientes/{id}/nova-versao-prontuario`

---

## ? Testes e Qualidade

- Testes unitários **Paciente** e **Prontuário**
- Cobertura focada em **regras de negócio**
- Relatório gerado com **JaCoCo via Maven**

```bash
mvn clean test jacoco:report
```

Relatório disponível em:

```
target/site/jacoco/index.html
```

---

## ? Escopo do MVP

- Não possui front-end
- Auditoria avançada e logs como evolução futura
- Integração com sistemas do SUS fora do escopo do MVP

---

## ? Próximos Passos

- Auditoria de acesso a prontuários (LGPD)
- Observabilidade (logs e métricas)
- Integração com sistemas oficiais do SUS
- Front-end para profissionais de saúde

---

## ? Observação Final

Este projeto é uma **prova de conceito (MVP)** que demonstra a viabilidade técnica de uma API de prontuários eletrônicos para o CAPS, com foco em **qualidade, segurança e boas práticas de engenharia de software**.
