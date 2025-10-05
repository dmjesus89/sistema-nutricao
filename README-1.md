# Sistema de Nutrição - Epic 1: Autenticação & Gestão de Usuários

## 📋 Visão Geral

Este é o primeiro módulo do Sistema de Nutrição, focado na **autenticação e gestão de usuários**. O sistema foi desenvolvido usando Spring Boot 3 com Java 17 e segue as melhores práticas de arquitetura e segurança.

## 🚀 Funcionalidades Implementadas

### Epic 1 - Autenticação & Gestão de Usuários ✅

- **[US-101] Cadastro de usuário**
   - ✅ Endpoint POST /api/v1/auth/register
   - ✅ Validação de campos obrigatórios (nome, sobrenome, email, senha)
   - ✅ Criptografia de senha com BCrypt
   - ✅ Testes unitários e de integração

- **[US-102] Confirmação de email**
   - ✅ Geração de token único com expiração
   - ✅ Envio de email com link de confirmação
   - ✅ Endpoint GET /api/v1/auth/confirm?token=
   - ✅ Tratamento de casos de expiração/invalidez de token

- **[US-103] Login**
   - ✅ Endpoint POST /api/v1/auth/login
   - ✅ Geração de JWT + refresh token
   - ✅ Persistência de refresh token
   - ✅ Validação de login correto/incorreto

- **[US-104] Recuperação de senha**
   - ✅ Endpoint POST /api/v1/auth/forgot-password
   - ✅ Envio de email com link de reset
   - ✅ Endpoint POST /api/v1/auth/reset-password
   - ✅ Testes de expiração de token

- **[US-105] Permissões**
   - ✅ RBAC implementado (roles USER, ADMIN)
   - ✅ Restrição de acesso aos endpoints
   - ✅ Garantia que usuários comuns só vejam seus dados

## 🏗️ Arquitetura

### Estrutura do Projeto
```
src/main/java/com/nutrition/
├── domain/
│   └── entity/           # Entidades JPA
├── application/
│   ├── dto/             # DTOs para transferência de dados
│   └── service/         # Serviços de aplicação
├── infrastructure/
│   ├── repository/      # Repositórios JPA
│   ├── security/        # Configurações de segurança
│   ├── service/         # Serviços de infraestrutura
│   └── exception/       # Tratamento de exceções
└── presentation/
    └── controller/      # Controllers REST
```

### Stack Tecnológica

- **Backend**: Java 17 + Spring Boot 3.2
- **Segurança**: Spring Security + JWT
- **Banco de Dados**: PostgreSQL + Liquibase
- **Cache**: Redis
- **Email**: Spring Mail + MailHog (dev)
- **Testes**: JUnit 5 + Testcontainers
- **Containerização**: Docker + Docker Compose

## 🛠️ Setup do Projeto

### Pré-requisitos
- Java 17+
- Docker e Docker Compose
- Maven 3.8+

### 1. Clone o repositório
```bash
git clone <repository-url>
cd sistema-nutricao
```

### 2. Suba a infraestrutura local
```bash
docker-compose up -d
```

Isso irá inicializar:
- PostgreSQL (porta 5432)
- Redis (porta 6379)
- MailHog (porta 8025 para web UI, 1025 para SMTP)

### 3. Configure as variáveis de ambiente (opcional)
```bash
export JWT_SECRET=meuSecretoSuperSeguro123456
export MAIL_USERNAME=seu-email@gmail.com
export MAIL_PASSWORD=sua-senha-de-app
```

### 4. Execute a aplicação
```bash
mvn spring-boot:run
```

A aplicação estará disponível em: http://localhost:8080

### 5. Interface de email (MailHog)
Acesse http://localhost:8025 para ver os emails enviados durante o desenvolvimento.

## 📡 Endpoints da API

### Autenticação

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/auth/register` | Cadastro de usuário |
| GET | `/api/v1/auth/confirm?token=` | Confirmação de email |
| POST | `/api/v1/auth/login` | Login |
| POST | `/api/v1/auth/forgot-password` | Esqueci minha senha |
| POST | `/api/v1/auth/reset-password` | Reset de senha |
| POST | `/api/v1/auth/refresh-token` | Renovar token |
| POST | `/api/v1/auth/logout` | Logout |

### Exemplo de Uso

#### 1. Registro
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "first_name": "João",
    "last_name": "Silva", 
    "email": "joao@exemplo.com",
    "password": "minhasenha123"
  }'
```

#### 2. Confirmação (use o token do email)
```bash
curl -X GET "http://localhost:8080/api/v1/auth/confirm?token=seu-token-aqui"
```

#### 3. Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@exemplo.com",
    "password": "minhasenha123"
  }'
```

## 🧪 Testes

### Executar todos os testes
```bash
mvn test
```

### Executar apenas testes de integração
```bash
mvn test -Dtest="*IntegrationTest"
```

### Coverage
```bash
mvn jacoco:report
```

## 🔒 Segurança

### Recursos Implementados

1. **Autenticação JWT**: Tokens stateless com refresh token
2. **Rate Limiting**: Proteção contra brute force (5 tentativas por 15 min)
3. **Validação de Input**: Validação robusta com Bean Validation
4. **CORS**: Configurado para desenvolvimento
5. **RBAC**: Controle de acesso baseado em roles
6. **Password Hashing**: BCrypt com salt automático

### Usuário Admin Padrão
- **Email**: admin@sistema-nutricao.com
- **Senha**: admin123

## 📊 Monitoramento

### Health Checks
- http://localhost:8080/actuator/health

### Métricas (Admin apenas)
- http://localhost:8080/actuator/metrics

## 🐳 Docker

### Build da imagem
```bash
docker build -t sistema-nutricao:latest .
```

### Executar com Docker
```bash
docker run -p 8080:8080 \
  --env-file .env \
  sistema-nutricao:latest
```

## 📝 Logs

Os logs são salvos em:
- Console (desenvolvimento)
- `logs/sistema-nutricao.log` (produção)

Níveis configurados:
- Application: INFO
- Security: DEBUG (desenvolvimento)

## 🚧 Próximos Passos (Epic 2)

O próximo módulo a ser implementado será:

**Epic 2 - Perfil do Usuário & Dados Corporais**
- Cadastro de dados básicos (idade, sexo, altura, peso)
- Atualização de peso com histórico
- Cálculo automático do TDEE

## 🤝 Contribuição

1. Crie uma branch para sua feature
2. Implemente os testes
3. Garanta 80%+ de cobertura
4. Submeta um Pull Request

## 📄 Licença

Este projeto está sob licença MIT. Veja o arquivo LICENSE para mais detalhes.

---

**Desenvolvido por**: Principal Java Architect  
**Data**: Dezembro 2024  
**Versão**: 1.0.0