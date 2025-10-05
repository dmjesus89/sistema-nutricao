# Sistema de Nutrição - Epic 2: Perfil do Usuário & Dados Corporais

## 📋 Visão Geral

Implementação completa do **Epic 2** do Sistema de Nutrição, focado na gestão do perfil do usuário e dados corporais. Este módulo permite aos usuários cadastrar e gerenciar suas informações pessoais, acompanhar o progresso de peso e receber cálculos metabólicos personalizados.

## 🚀 Funcionalidades Implementadas

### Epic 2 - Perfil do Usuário & Dados Corporais ✅

- **[US-201] Cadastro de dados básicos**
  - ✅ Endpoint POST /api/v1/profile
  - ✅ Campos: idade, sexo, altura, peso atual, peso alvo
  - ✅ Validações robustas de entrada
  - ✅ Cálculos automáticos de BMI

- **[US-202] Atualização de peso**
  - ✅ Endpoint POST /api/v1/profile/weight
  - ✅ Histórico completo com timestamp
  - ✅ Prevenção de registros duplicados por data
  - ✅ Cálculo de diferenças entre pesagens

- **[US-203] Cálculo do TDEE**
  - ✅ Fórmula Mifflin-St Jeor para BMR
  - ✅ Fatores de atividade física
  - ✅ Ajustes baseados em objetivos
  - ✅ Calorias diárias personalizadas

## 🏗️ Arquitetura Técnica

### Entidades Principais

**UserProfile**
- Dados pessoais (nascimento, sexo, altura)
- Métricas corporais (peso atual, alvo)
- Configurações (nível de atividade, objetivo)
- Cálculos metabólicos (BMR, TDEE, calorias diárias)

**WeightHistory**
- Registro histórico de peso
- Constraint única por usuário/data
- Cálculo automático de diferenças
- Suporte a anotações

### Serviços Especializados

**TdeeCalculationService**
- Implementação da fórmula Mifflin-St Jeor
- Cálculo de BMR, TDEE e necessidade calórica
- Validações de segurança (mínimos calóricos)
- Cálculo de necessidade hídrica

**ProfileValidationService**
- Validações abrangentes de dados
- Sistema de erros e warnings
- Recomendações personalizadas baseadas em IMC
- Validações de objetivos saudáveis

## 📡 Endpoints da API

### Perfil do Usuário

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/profile` | Criar perfil inicial |
| PUT | `/api/v1/profile` | Atualizar perfil |
| GET | `/api/v1/profile` | Obter perfil completo |
| GET | `/api/v1/profile/totalDailyEnergyExpenditure` | Cálculos TDEE detalhados |

### Gestão de Peso

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/profile/weight` | Registrar peso |
| GET | `/api/v1/profile/weight/history` | Histórico completo |
| GET | `/api/v1/profile/weight/stats` | Estatísticas e progresso |

## 🧮 Cálculos Implementados

### BMR (Taxa Metabólica Basal)
**Fórmula Mifflin-St Jeor:**
- **Homens**: BMR = 10 × peso(kg) + 6.25 × altura(cm) - 5 × idade + 5
- **Mulheres**: BMR = 10 × peso(kg) + 6.25 × altura(cm) - 5 × idade - 161
- **Outros**: Média entre os valores

### TDEE (Gasto Energético Total Diário)
**BMR × Fator de Atividade:**
- Sedentário: 1.2
- Levemente ativo: 1.375
- Moderadamente ativo: 1.55
- Muito ativo: 1.725
- Extremamente ativo: 1.9

### Calorias Diárias
**TDEE + Ajuste do Objetivo:**
- Perder peso: TDEE - 500 kcal
- Manter peso: TDEE
- Ganhar peso: TDEE + 500 kcal

### IMC (Índice de Massa Corporal)
**peso(kg) / (altura(m))²**
- Abaixo do peso: < 18.5
- Peso normal: 18.5 - 24.9
- Sobrepeso: 25.0 - 29.9
- Obesidade: ≥ 30.0

### Necessidade Hídrica
**35ml × peso corporal (kg)**
- Mínimo: 1.5L
- Máximo: 4L

## 📊 Exemplos de Uso

### 1. Criar Perfil Completo
```bash
curl -X POST http://localhost:8080/api/v1/profile \
  -H "Authorization: Bearer seu-token-jwt" \
  -H "Content-Type: application/json" \
  -d '{
    "birth_date": "1990-05-15",
    "gender": "MALE",
    "height": 180.0,
    "current_weight": 80.0,
    "target_weight": 75.0,
    "activity_level": "MODERATELY_ACTIVE",
    "goal": "LOSE_WEIGHT"
  }'
```

**Resposta:**
```json
{
  "success": true,
  "message": "Perfil criado com sucesso",
  "data": {
    "id": 1,
    "birth_date": "1990-05-15",
    "age": 33,
    "gender": "MALE",
    "gender_display": "Masculino",
    "height": 180.0,
    "current_weight": 80.0,
    "target_weight": 75.0,
    "activity_level": "MODERATELY_ACTIVE",
    "activity_level_display": "Moderadamente ativo",
    "goal": "LOSE_WEIGHT",
    "goal_display": "Perder peso",
    "bmr": 1765.00,
    "totalDailyEnergyExpenditure": 2735.75,
    "daily_calories": 2236,
    "bmi": 24.69,
    "bmi_category": "Peso normal",
    "created_at": "2024-12-01T10:30:00"
  }
}
```

### 2. Registrar Novo Peso
```bash
curl -X POST http://localhost:8080/api/v1/profile/weight \
  -H "Authorization: Bearer seu-token-jwt" \
  -H "Content-Type: application/json" \
  -d '{
    "weight": 79.5,
    "recorded_date": "2024-12-01",
    "notes": "Progresso semanal"
  }'
```

### 3. Obter Histórico de Peso
```bash
curl -X GET http://localhost:8080/api/v1/profile/weight/history \
  -H "Authorization: Bearer seu-token-jwt"
```

### 4. Visualizar Cálculos TDEE
```bash
curl -X GET http://localhost:8080/api/v1/profile/totalDailyEnergyExpenditure \
  -H "Authorization: Bearer seu-token-jwt"
```

## 🧪 Validações Implementadas

### Validações de Segurança
- Idade: 13-120 anos
- Altura: 100-250 cm
- Peso: 30-300 kg
- Mudanças drásticas de peso (>5kg) geram alertas

### Validações de Saúde
- IMC muito baixo (<16) ou alto (>40)
- Objetivos irrealistas (diferença >50kg)
- Taxa de perda/ganho muito alta (>1kg/semana)
- Calorias mínimas por sexo (1200♀/1500♂)

### Recomendações Automáticas
- Baseadas em IMC atual
- Ajustadas por idade e nível de atividade
- Sugestões de acompanhamento profissional

## 🗄️ Estrutura do Banco de Dados

### Tabela user_profiles
```sql
CREATE TABLE user_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE REFERENCES users(id),
    birth_date DATE,
    gender VARCHAR(10) CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    height DECIMAL(5,2) CHECK (height BETWEEN 100 AND 250),
    current_weight DECIMAL(5,2) CHECK (current_weight BETWEEN 30 AND 300),
    target_weight DECIMAL(5,2) CHECK (target_weight BETWEEN 30 AND 300),
    activity_level VARCHAR(20) DEFAULT 'SEDENTARY',
    goal VARCHAR(20) DEFAULT 'MAINTAIN_WEIGHT',
    bmr DECIMAL(7,2),
    tdee DECIMAL(7,2),
    daily_calories DECIMAL(7,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Tabela weight_history
```sql
CREATE TABLE weight_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    weight DECIMAL(5,2) NOT NULL CHECK (weight BETWEEN 30 AND 300),
    recorded_date DATE NOT NULL,
    notes VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE(user_id, recorded_date)
);
```

## 🎯 Métricas de Qualidade

### Cobertura de Testes
- ✅ Testes unitários para cálculos TDEE
- ✅ Testes de integração para API
- ✅ Validação de cenários edge cases
- ✅ Testes de performance para cálculos

### Segurança
- ✅ Autorização por usuário (RBAC)
- ✅ Validação de entrada robusta
- ✅ Sanitização de dados sensíveis
- ✅ Rate limiting nos endpoints

### Performance
- ✅ Índices otimizados no banco
- ✅ Queries eficientes com JPA
- ✅ Cache de cálculos pesados
- ✅ Paginação em históricos grandes

## 📈 Próximos Passos (Epic 3)

**Epic 3 - Base de Alimentos & Suplementos**
- Catálogo pré-carregado de alimentos
- Gerenciamento administrativo
- Sistema de favoritos e restrições
- Busca e filtros avançados

## 🔧 Configuração para Desenvolvimento

### Swagger UI
Acesse a documentação interativa em: http://localhost:8080/swagger-ui/index.html

### Usuário de Teste
Um perfil de exemplo é criado automaticamente:
- **Email**: admin@sistema-nutricao.com
- **Senha**: admin123
- **Perfil**: Homem, 38 anos, 180cm, objetivo de perda de peso

### Scripts de Teste
```bash
# Executar todos os testes do Epic 2
mvn test -Dtest="*Profile*Test"

# Executar apenas testes de cálculo TDEE
mvn test -Dtest="TdeeCalculationServiceTest"

# Executar testes de integração
mvn test -Dtest="ProfileControllerIntegrationTest"
```

---

**Status**: ✅ Epic 2 Completo e Operacional  
**Próximo**: Epic 3 - Base de Alimentos & Suplementos  
**Data**: Dezembro 2024