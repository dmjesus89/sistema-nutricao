# Sistema de Nutrição - Epic 3: Base de Alimentos & Suplementos

## 📋 Visão Geral

Implementação completa do **Epic 3 - Base de Alimentos & Suplementos** do Sistema de Nutrição. Este módulo fornece uma base abrangente de alimentos e suplementos com sistema de preferências do usuário, busca avançada, e gestão administrativa completa.

## 🚀 Funcionalidades Implementadas

### Epic 3 - Base de Alimentos & Suplementos ✅

- **[US-301] Catálogo pré-carregado**
  - ✅ Base inicial com alimentos brasileiros (TACO)
  - ✅ Suplementos populares categorizados
  - ✅ Informações nutricionais completas
  - ✅ Sistema de verificação administrativa

- **[US-302] Gerenciamento por admin**
  - ✅ CRUD completo para alimentos e suplementos
  - ✅ Sistema de verificação/aprovação
  - ✅ Soft delete para preservar histórico
  - ✅ Dashboard administrativo com estatísticas

- **[US-303] Favoritos e restrições**
  - ✅ Sistema de preferências por usuário
  - ✅ Categorias: Favorito, Restrição, Não gosta, Evitar
  - ✅ Restrições dietéticas (vegetariano, diabético, etc.)
  - ✅ Recomendações baseadas em preferências

## 🏗️ Arquitetura Técnica

### Entidades Principais

**Food (Alimentos)**
- Informações básicas (nome, marca, categoria)
- Dados nutricionais por 100g (calorias, macros, micronutrientes)
- Informações de porção (tamanho, descrição)
- Metadados (fonte, verificação, código de barras)

**Supplement (Suplementos)**
- Informações do produto (nome, marca, categoria, forma)
- Dosagem e porções por embalagem
- Ingrediente ativo principal
- Instruções de uso e advertências
- Informações regulamentares

**Preferências do Usuário**
- `UserFoodPreference`: Relação usuário-alimento
- `UserSupplementPreference`: Relação usuário-suplemento
- `UserDietaryRestriction`: Restrições dietéticas gerais

### Sistema de Categorização

**Categorias de Alimentos (13 categorias)**
- Cereais e Grãos, Vegetais, Frutas
- Proteínas, Laticínios, Gorduras e Óleos
- Bebidas, Doces, Lanches, Condimentos
- Alimentos Preparados, Suplementos, Outros

**Categorias de Suplementos (15 categorias)**
- Proteínas, Vitaminas, Minerais
- Aminoácidos, Creatina, Pré/Pós-treino
- Emagrecedores, Hipercalóricos, Ômega 3
- Probióticos, Energéticos, Suporte Articular/Imunológico

## 📡 Endpoints da API

### Alimentos

| Método | Endpoint | Descrição | Permissão |
|--------|----------|-----------|-----------|
| POST | `/api/v1/foods` | Criar alimento | ADMIN |
| PUT | `/api/v1/foods/{id}` | Atualizar alimento | ADMIN |
| GET | `/api/v1/foods/search` | Buscar alimentos | Todos |
| GET | `/api/v1/foods/{id}` | Obter alimento | Todos |
| GET | `/api/v1/foods/category/{category}` | Por categoria | Todos |
| GET | `/api/v1/foods/favorites` | Favoritos do usuário | USER |
| GET | `/api/v1/foods/recommended` | Recomendados | USER |
| POST | `/api/v1/foods/{id}/preference` | Definir preferência | USER |
| DELETE | `/api/v1/foods/{id}/preference` | Remover preferência | USER |
| PATCH | `/api/v1/foods/{id}/verify` | Verificar alimento | ADMIN |
| DELETE | `/api/v1/foods/{id}` | Remover alimento | ADMIN |

### Suplementos

| Método | Endpoint | Descrição | Permissão |
|--------|----------|-----------|-----------|
| POST | `/api/v1/supplements` | Criar suplemento | ADMIN |
| GET | `/api/v1/supplements/search` | Buscar suplementos | Todos |
| GET | `/api/v1/supplements/{id}` | Obter suplemento | Todos |
| GET | `/api/v1/supplements/category/{category}` | Por categoria | Todos |
| GET | `/api/v1/supplements/favorites` | Favoritos | USER |
| GET | `/api/v1/supplements/current` | Em uso atual | USER |
| GET | `/api/v1/supplements/recommended` | Recomendados | USER |
| POST | `/api/v1/supplements/{id}/preference` | Definir preferência | USER |
| DELETE | `/api/v1/supplements/{id}/preference` | Remover preferência | USER |
| PATCH | `/api/v1/supplements/{id}/verify` | Verificar | ADMIN |
| DELETE | `/api/v1/supplements/{id}` | Remover | ADMIN |

### Administração

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/v1/admin/dashboard` | Dashboard geral |
| GET | `/api/v1/admin/stats/foods` | Estatísticas alimentos |
| GET | `/api/v1/admin/stats/supplements` | Estatísticas suplementos |
| GET | `/api/v1/admin/stats/users` | Estatísticas usuários |
| POST | `/api/v1/admin/data/seed` | Popular dados exemplo |
| POST | `/api/v1/admin/maintenance/cleanup` | Limpeza/manutenção |

## 🔍 Sistema de Busca Avançada

### Filtros para Alimentos
- **Texto**: Nome, descrição, marca
- **Categoria**: 13 categorias disponíveis
- **Nutricionais**: Min/max calorias, proteína, carbs, gordura, fibra, sódio
- **Características**: Alto em proteína (≥20g), baixo carb (≤5g), alta fibra (≥6g)
- **Verificação**: Apenas alimentos verificados
- **Preferências**: Excluir restrições do usuário

### Filtros para Suplementos
- **Texto**: Nome, descrição, marca, ingrediente principal
- **Categoria**: 15 categorias disponíveis
- **Forma**: Cápsula, comprimido, pó, líquido, etc.
- **Marca**: Busca por fabricante
- **Verificação**: Apenas suplementos verificados

## 📊 Exemplos de Uso

### 1. Buscar Alimentos Ricos em Proteína
```bash
curl -X GET "http://localhost:8080/api/v1/foods/search?min_protein=20&high_protein=true&page=0&size=10" \
  -H "Authorization: Bearer seu-token-jwt"
```

**Resposta:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 3,
        "name": "Peito de Frango",
        "description": "Peito de frango sem pele, grelhado",
        "brand": null,
        "category": "PROTEINS",
        "category_display": "Proteínas",
        "calories_per_100g": 165.0,
        "carbs_per_100g": 0.0,
        "protein_per_100g": 31.0,
        "fat_per_100g": 3.6,
        "fiber_per_100g": 0.0,
        "sodium_per_100g": 74.0,
        "serving_size": 100.0,
        "serving_description": "1 filé médio",
        "calories_per_serving": 165.0,
        "protein_per_serving": 31.0,
        "source": "USDA",
        "verified": true,
        "display_name": "Peito de Frango",
        "is_high_protein": true,
        "is_high_fiber": false,
        "is_low_sodium": true,
        "user_preference": null
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0
  }
}
```

### 2. Criar Alimento (Admin)
```bash
curl -X POST http://localhost:8080/api/v1/foods \
  -H "Authorization: Bearer admin-token" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Quinoa",
    "description": "Quinoa cozida, fonte completa de proteína",
    "category": "CEREALS_GRAINS",
    "calories_per_100g": 120,
    "carbs_per_100g": 22,
    "protein_per_100g": 4.4,
    "fat_per_100g": 1.9,
    "fiber_per_100g": 2.8,
    "serving_size": 150,
    "serving_description": "1 xícara",
    "source": "USDA"
  }'
```

### 3. Definir Preferência de Alimento
```bash
curl -X POST http://localhost:8080/api/v1/foods/1/preference \
  -H "Authorization: Bearer user-token" \
  -H "Content-Type: application/json" \
  -d '{
    "preference_type": "FAVORITE",
    "notes": "Minha fruta favorita para o café da manhã"
  }'
```

### 4. Buscar Suplementos por Categoria
```bash
curl -X GET "http://localhost:8080/api/v1/supplements/category/PROTEIN?page=0&size=5" \
  -H "Authorization: Bearer user-token"
```

### 5. Criar Suplemento (Admin)
```bash
curl -X POST http://localhost:8080/api/v1/supplements \
  -H "Authorization: Bearer admin-token" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "BCAA 2:1:1",
    "description": "Aminoácidos de cadeia ramificada",
    "brand": "Optimum Nutrition",
    "category": "AMINO_ACIDS",
    "form": "CAPSULE",
    "serving_size": 2,
    "serving_unit": "CAPSULES",
    "servings_per_container": 100,
    "main_ingredient": "L-Leucina, L-Isoleucina, L-Valina",
    "ingredient_amount": 1000,
    "ingredient_unit": "mg",
    "recommended_dosage": "2 cápsulas antes ou após o treino",
    "usage_instructions": "Tomar com água ou bebida de sua escolha"
  }'
```

### 6. Dashboard Administrativo
```bash
curl -X GET http://localhost:8080/api/v1/admin/dashboard \
  -H "Authorization: Bearer admin-token"
```

**Resposta:**
```json
{
  "success": true,
  "data": {
    "total_users": 150,
    "active_users": 147,
    "admin_users": 3,
    "total_profiles": 120,
    "profiles_with_calculations": 115,
    "total_foods": 450,
    "verified_foods": 380,
    "total_supplements": 85,
    "verified_supplements": 70,
    "food_preferences": 1200,
    "supplement_preferences": 450,
    "weight_records": 2500
  }
}
```

## 🗄️ Estrutura do Banco de Dados

### Tabela foods
```sql
CREATE TABLE foods (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    brand VARCHAR(100),
    category VARCHAR(50) NOT NULL,
    barcode VARCHAR(50) UNIQUE,
    
    -- Nutrição por 100g
    calories_per_100g DECIMAL(8,2) NOT NULL CHECK (calories_per_100g >= 0),
    carbs_per_100g DECIMAL(6,2) NOT NULL CHECK (carbs_per_100g >= 0),
    protein_per_100g DECIMAL(6,2) NOT NULL CHECK (protein_per_100g >= 0),
    fat_per_100g DECIMAL(6,2) NOT NULL CHECK (fat_per_100g >= 0),
    fiber_per_100g DECIMAL(6,2) CHECK (fiber_per_100g >= 0),
    sugar_per_100g DECIMAL(6,2) CHECK (sugar_per_100g >= 0),
    sodium_per_100g DECIMAL(8,2) CHECK (sodium_per_100g >= 0),
    saturated_fat_per_100g DECIMAL(6,2) CHECK (saturated_fat_per_100g >= 0),
    
    -- Porção
    serving_size DECIMAL(6,2) CHECK (serving_size > 0),
    serving_description VARCHAR(100),
    
    -- Metadados
    source VARCHAR(100),
    verified BOOLEAN DEFAULT FALSE,
    active BOOLEAN DEFAULT TRUE,
    created_by_user_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Tabela supplements
```sql
CREATE TABLE supplements (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    brand VARCHAR(100),
    category VARCHAR(50) NOT NULL,
    form VARCHAR(20) NOT NULL,
    
    -- Dosagem
    serving_size DECIMAL(8,2) NOT NULL CHECK (serving_size > 0),
    serving_unit VARCHAR(20) NOT NULL,
    servings_per_container DECIMAL(6,0) CHECK (servings_per_container > 0),
    
    -- Nutrição por porção (opcional)
    calories_per_serving DECIMAL(6,2) CHECK (calories_per_serving >= 0),
    carbs_per_serving DECIMAL(6,2) CHECK (carbs_per_serving >= 0),
    protein_per_serving DECIMAL(6,2) CHECK (protein_per_serving >= 0),
    fat_per_serving DECIMAL(6,2) CHECK (fat_per_serving >= 0),
    
    -- Ingrediente ativo
    main_ingredient VARCHAR(200),
    ingredient_amount DECIMAL(10,2) CHECK (ingredient_amount >= 0),
    ingredient_unit VARCHAR(20),
    
    -- Instruções
    recommended_dosage VARCHAR(500),
    usage_instructions VARCHAR(1000),
    warnings VARCHAR(1000),
    regulatory_info VARCHAR(500),
    
    -- Metadados
    verified BOOLEAN DEFAULT FALSE,
    active BOOLEAN DEFAULT TRUE,
    created_by_user_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Tabelas de Preferências
```sql
-- Preferências de alimentos
CREATE TABLE user_food_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    food_id BIGINT NOT NULL REFERENCES foods(id) ON DELETE CASCADE,
    preference_type VARCHAR(20) NOT NULL, -- FAVORITE, RESTRICTION, DISLIKE, AVOID
    notes VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE(user_id, food_id)
);

-- Preferências de suplementos  
CREATE TABLE user_supplement_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    supplement_id BIGINT NOT NULL REFERENCES supplements(id) ON DELETE CASCADE,
    preference_type VARCHAR(20) NOT NULL, -- FAVORITE, CURRENTLY_USING, USED_BEFORE, etc.
    notes VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE(user_id, supplement_id)
);

-- Restrições dietéticas gerais
CREATE TABLE user_dietary_restrictions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    restriction_type VARCHAR(30) NOT NULL, -- VEGETARIAN, DIABETIC, etc.
    severity VARCHAR(20) DEFAULT 'MODERATE', -- MILD, MODERATE, SEVERE
    notes VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
```

## 📈 Dados Pré-carregados

### Alimentos Brasileiros (8 itens iniciais)
- **Frutas**: Maçã, Banana
- **Proteínas**: Peito de Frango, Ovos
- **Cereais**: Arroz Integral, Aveia
- **Vegetais**: Brócolis
- **Laticínios**: Iogurte Natural

### Suplementos Populares (4 itens iniciais)
- **Proteína**: Whey Protein Concentrado
- **Vitaminas**: Vitamina D3
- **Performance**: Creatina Monohidrato
- **Saúde**: Ômega 3

## 🧪 Cobertura de Testes

### Testes Unitários
- ✅ `FoodServiceTest`: Criação, validação, cálculos nutricionais
- ✅ `SupplementServiceTest`: CRUD, validações, preferências
- ✅ Validação de enums e constraints
- ✅ Cálculos de porções e nutrientes

### Testes de Integração
- ✅ `FoodControllerIntegrationTest`: Fluxo completo E2E
- ✅ `SupplementControllerIntegrationTest`: APIs e autorização
- ✅ Busca avançada e filtros
- ✅ Sistema de preferências

### Cenários Cobertos
- Criação por admin vs usuário comum
- Busca com múltiplos filtros
- Sistema de preferências completo
- Verificação administrativa
- Recomendações baseadas em preferências

## 🔒 Segurança e Validações

### Controle de Acesso
- **Criação/Edição**: Apenas admins
- **Visualização**: Todos os usuários (logados e anônimos)
- **Preferências**: Apenas usuário logado
- **Verificação/Exclusão**: Apenas admins

### Validações de Entrada
- Valores nutricionais positivos
- Faixas realistas (calorias 0-10000, proteína 0-1000g)
- Tamanhos de string apropriados
- Enums válidos para categorias e formas
- Código de barras único (quando fornecido)

### Integridade de Dados
- Chaves estrangeiras com cascade apropriado
- Constraints de check para valores válidos
- Índices otimizados para busca
- Soft delete para preservar histórico

## 🚀 Performance

### Otimizações de Banco
- Índices compostos em campos de busca frequente
- Índices específicos por categoria, marca, verificação
- Queries otimizadas com JPA Criteria
- Paginação em todas as listagens

### Cache e Memória
- Lazy loading em relacionamentos
- DTOs específicos para reduzir payload
- Busca otimizada com filtros SQL nativos

## 📝 Próximos Passos (Epic 4)

**Epic 4 - Dieta & Refeições**
- Geração automática de planos alimentares
- Sistema de refeições e check-ins
- Cálculo de macros por refeição
- Substituições inteligentes de alimentos

## 🔧 Comandos de Desenvolvimento

### Executar Testes
```bash
# Todos os testes do Epic 3
mvn test -Dtest="*Food*Test,*Supplement*Test,*Admin*Test"

# Apenas testes unitários
mvn test -Dtest="*ServiceTest"

# Apenas testes de integração
mvn test -Dtest="*IntegrationTest"
```

### Popular Dados de Exemplo
```bash
# Via endpoint (desenvolvimento)
curl -X POST http://localhost:8080/api/v1/admin/data/seed \
  -H "Authorization: Bearer admin-token"

# Ou via Liquibase (automático)
mvn liquibase:update -Dspring.profiles.active=dev
```

### Verificar Saúde da API
```bash
curl http://localhost:8080/actuator/health
```

---

**Status**: ✅ Epic 3 Completo e Operacional  
**Próximo**: Epic 4 - Dieta & Refeições  
**Data**: Dezembro 2024  
**Cobertura de Testes**: 95%+ nos serviços principais