# Sistema de Nutrição 🥗

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-7-red.svg)](https://redis.io/)
[![Security](https://img.shields.io/badge/Security-Fixed-success.svg)](./SECURITY_CHECKLIST.md)

Sistema completo de gestão nutricional para auxiliar pessoas a alcançarem seus objetivos de saúde através do acompanhamento de refeições, calorias, água e suplementos.

---

## 📋 Índice

- [Funcionalidades](#-funcionalidades)
- [Começando](#-começando)
- [Configuração](#️-configuração)
- [Segurança](#-segurança)
- [API Documentation](#-api-documentation)
- [Tecnologias](#-tecnologias)

---

## ✨ Funcionalidades

### ✅ Autenticação & Autorização
- Registro de usuário com validação de email
- Login com JWT (access + refresh tokens)
- Recuperação de senha via email
- Rate limiting para proteção contra brute force
- RBAC (User/Admin roles)

### ✅ Perfil do Usuário
- Cadastro de dados corporais (idade, sexo, altura, peso)
- Histórico de peso com tracking de progresso
- Cálculo automático de BMR, TDEE e necessidades calóricas
- Cálculo de IMC e recomendações personalizadas

### ✅ Gerenciamento de Refeições
- Criação de refeições personalizadas
- Biblioteca de alimentos com informações nutricionais
- Tracking de consumo diário
- Histórico de refeições consumidas
- Análise nutricional automática

### ✅ Tracking de Calorias
- Registro manual de calorias
- Registro via alimentos da biblioteca
- Registro via refeições salvas
- Sumário diário com progresso vs meta
- Histórico semanal e mensal

### ✅ Hidratação
- Registro de consumo de água
- Meta diária personalizada baseada no peso
- Progresso em tempo real
- Histórico de consumo

### ✅ Dashboard Unificado
- Visão consolidada de calorias, água e macros
- Progresso diário vs metas estabelecidas
- Estatísticas e insights nutricionais

---

## 🏁 Começando

### Pré-requisitos
- Java 17+
- PostgreSQL 15+
- Redis 7+
- Maven 3.8+

### Instalação Rápida

1. **Clone o repositório**
   ```bash
   git clone https://github.com/seu-usuario/sistema-nutricao.git
   cd sistema-nutricao
   ```

2. **Configure o banco de dados**
   ```bash
   createdb nutricao_db
   ```

3. **Configure as variáveis de ambiente**
   ```bash
   cp .env.example .env
   # Edite o .env com suas configurações
   ```

4. **Execute o projeto**
   ```bash
   mvn spring-boot:run
   ```

5. **Acesse a aplicação**
   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui/index.html

---

## ⚙️ Configuração

### Variáveis de Ambiente Obrigatórias

⚠️ **IMPORTANTE**: Crie um arquivo `.env` na raiz com:

```bash
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/nutricao_db
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_password

# JWT (mínimo 32 caracteres)
JWT_SECRET=your_strong_jwt_secret_key_here

# Email (Gmail com App Password)
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your_gmail_app_password
EMAIL_FROM=your-email@gmail.com

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
```

### Gerar JWT Secret Forte
```bash
openssl rand -base64 32
```

### Configurar Gmail App Password
1. Ative verificação em 2 etapas: https://myaccount.google.com/security
2. Gere senha de app: https://myaccount.google.com/apppasswords
3. Use no `MAIL_PASSWORD`

---

## 🔐 Segurança

### ✅ Implementações de Segurança

O sistema implementa as melhores práticas de segurança:

- [x] **Autenticação JWT** com refresh tokens
- [x] **Rate Limiting** (5 tentativas/15 min no login)
- [x] **CORS** configurado com origins específicas
- [x] **BCrypt** para hash de senhas
- [x] **Validação robusta** de entrada
- [x] **Proteção SQL Injection** (JPA + named params)
- [x] **Headers de segurança** (XSS, etc)
- [x] **Logs seguros** (sem dados sensíveis)
- [x] **Secrets em env vars** (sem hardcode)

### 📚 Documentação de Segurança

- [✅ Security Checklist](./SECURITY_CHECKLIST.md) - Checklist completo
- [✅ Fixes Applied](./CODE_REVIEW_FIXES_APPLIED.md) - Correções implementadas

### 🔒 CORS Configuration

Origens permitidas (atualizar em produção):
- `http://localhost:3000` (dev)
- `https://v0-sistema-nutricao-auth.vercel.app` (prod)

Para alterar: `SecurityConfiguration.java:75-78`

### 🚨 Correções de Segurança Aplicadas

1. ✅ **Removido hardcoded credentials** de `application.yml`
2. ✅ **CORS fixado** - não permite wildcard com credentials
3. ✅ **Rate limit corrigido** - agora bloqueia corretamente
4. ✅ **Info leakage corrigido** - erros genéricos para cliente
5. ✅ **SQL logging desabilitado** em produção

---

## 📡 API Documentation

### Swagger UI
```
http://localhost:8080/swagger-ui/index.html
```

### Principais Endpoints

#### Autenticação
```http
POST   /api/v1/auth/register        # Registrar
POST   /api/v1/auth/login           # Login
POST   /api/v1/auth/refresh         # Refresh token
POST   /api/v1/auth/forgot-password # Recuperar senha
POST   /api/v1/auth/reset-password  # Resetar senha
GET    /api/v1/auth/confirm         # Confirmar email
```

#### Perfil
```http
POST   /api/v1/profile              # Criar perfil
PUT    /api/v1/profile              # Atualizar
GET    /api/v1/profile              # Obter
GET    /api/v1/profile/totalDailyEnergyExpenditure  # TDEE
POST   /api/v1/profile/weight       # Peso
GET    /api/v1/profile/weight/*     # Histórico/Stats
```

#### Refeições
```http
POST   /api/v1/meals                # Criar
GET    /api/v1/meals                # Listar
GET    /api/v1/meals/{id}           # Obter
DELETE /api/v1/meals/{id}           # Deletar
POST   /api/v1/meals/{id}/consume   # Consumir
GET    /api/v1/meals/consumed/*     # Histórico
```

#### Calorias
```http
POST   /api/v1/calories/manual      # Manual
POST   /api/v1/calories/food        # Via alimento
POST   /api/v1/calories/meal        # Via refeição
GET    /api/v1/calories/*           # Sumários
DELETE /api/v1/calories/{id}        # Deletar
```

#### Água
```http
POST   /api/v1/water                # Registrar
GET    /api/v1/water/today          # Hoje
GET    /api/v1/water/history        # Histórico
```

#### Dashboard
```http
GET    /api/v1/dashboard            # Dashboard completo
```

---

## 🚀 Tecnologias

### Backend
- **Java 17** - Linguagem
- **Spring Boot 3.2.0** - Framework
- **Spring Security** - Auth/AuthZ
- **Spring Data JPA** - ORM
- **PostgreSQL** - Database
- **Redis** - Cache/Rate Limit
- **Flyway** - Migrations
- **JWT (jjwt 0.12.3)** - Tokens
- **Lombok** - Boilerplate reduction
- **MapStruct** - DTO mapping

### Tools
- **Maven** - Build
- **Swagger/OpenAPI** - Docs
- **Testcontainers** - Integration tests

---

## 🏗️ Arquitetura

### Clean Architecture

```
📦 com.nutrition
├── 📂 application       # Use cases & DTOs
│   ├── dto/
│   └── service/
├── 📂 domain           # Entities & Business Logic
│   └── entity/
├── 📂 infrastructure   # Frameworks & Drivers
│   ├── config/
│   ├── exception/
│   ├── repository/
│   └── security/
└── 📂 presentation     # Controllers & API
    └── controller/
```

### Padrões
- Repository Pattern
- DTO Pattern
- Builder Pattern
- Dependency Injection
- Global Exception Handling
- Transaction Management

---

## 🗄️ Banco de Dados

### Schema Principal

```sql
users              # Autenticação
user_profiles      # Dados corporais
weight_history     # Histórico de peso
meals              # Refeições
meal_foods         # Alimentos por refeição
calorie_entries    # Tracking calorias
water_intake       # Consumo de água
foods              # Catálogo de alimentos
```

### Migrações

- **Flyway** gerencia migrations
- Localização: `src/main/resources/db/migration/`
- Formato: `V{version}__{description}.sql`
- Execução automática no startup

---

## 🧪 Testes

```bash
# Todos os testes
mvn test

# Testes específicos
mvn test -Dtest=ProfileServiceTest

# Cobertura
mvn jacoco:report
```

---

## 📊 Cálculos Nutricionais

### BMR (Mifflin-St Jeor)
- **Homens**: `10×peso + 6.25×altura - 5×idade + 5`
- **Mulheres**: `10×peso + 6.25×altura - 5×idade - 161`

### TDEE
- BMR × Fator de Atividade (1.2 a 1.9)

### Meta Calórica
- **Perder peso**: TDEE - 500 kcal
- **Manter**: TDEE
- **Ganhar**: TDEE + 500 kcal

---

## 🚀 Deployment Checklist

Antes de produção:

- [ ] Configurar todas env vars
- [ ] JWT_SECRET forte (32+ chars)
- [ ] CORS origins corretas
- [ ] Database credentials seguras
- [ ] Email configurado
- [ ] Redis com senha
- [ ] HTTPS habilitado
- [ ] Backups configurados
- [ ] Monitoring ativo
- [ ] Security tests OK

Ver: [SECURITY_CHECKLIST.md](./SECURITY_CHECKLIST.md)

---

## 📝 Documentação Adicional

- [README Epic 1](./README-1.md) - Autenticação
- [README Epic 2](./README-2.md) - Perfil
- [README Epic 3](./README-3.md) - Alimentos
- [Security Checklist](./SECURITY_CHECKLIST.md)
- [Code Review Fixes](./CODE_REVIEW_FIXES_APPLIED.md)

---

## 🐛 Reportar Issues

- GitHub Issues: [sistema-nutricao/issues](https://github.com/seu-usuario/sistema-nutricao/issues)
- Vulnerabilidades: Contato privado

---

## 📞 Suporte

- 📧 Email: dmjesus89@gmail.com
- 📖 Docs: Ver links acima

---

## 🙏 Credits

- Spring Framework Team
- PostgreSQL Community
- Redis Team

---

**Feito com ❤️ para ajudar pessoas a alcançarem seus objetivos de saúde**
