# Equipe de Agentes Especialistas: CommonsAndroidNative

Este documento define a governança, as responsabilidades e a arquitetura de **Agentes Especialistas** que operam no monorepo `CommonsAndroidNative`.

---

## 🏛️ Topologia da Equipe de Agentes

A equipe é liderada por um **Orquestrador Central (Tech Lead / Arquiteto)**, que atua como ponto único de contato com o usuário e coordena **6 Agentes Especialistas de Domínio** e **2 Agentes Técnicos Transversais**.

```text
                               ┌─────────────────────────────────┐
                               │       USUÁRIO / TECH LEAD       │
                               └────────────────┬────────────────┘
                                                │
                                                ▼
                               ┌─────────────────────────────────┐
                               │       AGENTE ORQUESTRADOR       │
                               │  (Planejamento, Roteamento, CI) │
                               └────────────────┬────────────────┘
                                                │
       ┌───────────────────────────────┬────────┴───────────────────────────────┬───────────────────────────────┐
       │                               │                                        │                               │
       ▼                               ▼                                        ▼                               ▼
┌──────────────┐               ┌──────────────┐                         ┌──────────────┐                ┌──────────────┐
│  E-commerce  │               │  Logística   │                         │  Identidade  │                │ Comunicação  │
│  & Varejo    │               │  & Delivery  │                         │  & Segurança │                │  & Suporte   │
└──────────────┘               └──────────────┘                         └──────────────┘                └──────────────┘
       │                               │                                        │                               │
       ▼                               ▼                                        ▼                               ▼
┌──────────────┐               ┌──────────────┐                         ┌──────────────┐                ┌──────────────┐
│   Serviços   │               │ Plataforma & │                         │ Build & CI   │                │ QA & Testes  │
│  & Finanças  │               │   Sistema    │                         │    Master    │                │  Automação   │
└──────────────┘               └──────────────┘                         └──────────────┘                └──────────────┘
```

---

## 👥 Especialistas e Matriz de Responsabilidade

### 1. 🛒 `ecommerce-specialist` (E-commerce & Varejo)
- **Escopo de Features**:
  - `:feature:storefront:catalog`
  - `:feature:storefront:search`
  - `:feature:storefront:promotions`
  - `:feature:storefront:stores`
  - `:feature:checkout:cart`
  - `:feature:checkout:payment`
  - `:feature:customer:loyalty`
- **Bundles Comerciais**:
  - `:bundles:ecommerce:basic`, `:bundles:ecommerce:standard`, `:bundles:ecommerce:pro`
  - `:bundles:real-estate:basic`, `:bundles:real-estate:standard`, `:bundles:real-estate:pro`
- **Foco Técnico**: Catálogo reativo, paginação, carrinho com persistência local, gateways de pagamento (PIX, Cartão), cupons e fidelização.

---

### 2. 🚚 `logistics-specialist` (Logística & Delivery)
- **Escopo de Features**:
  - `:feature:delivery:order-tracking`
  - `:feature:delivery:maps`
  - `:feature:delivery:driver-app`
  - `:feature:delivery:dispatch`
  - `:feature:delivery:geofencing`
  - `:feature:delivery:offline-maps`
  - `:feature:platform:emergency`
- **Bundles Comerciais**:
  - `:bundles:delivery:basic`, `:bundles:delivery:standard`, `:bundles:delivery:pro`
  - `:bundles:emergency:basic`, `:bundles:emergency:standard`, `:bundles:emergency:pro`
- **Foco Técnico**: Rastreamento em tempo real, integração de mapas e geolocalização com cache offline, timelines de status e cercamento eletrônico.

---

### 3. 🔐 `identity-specialist` (Identidade, Acesso & Segurança)
- **Escopo de Features**:
  - `:feature:auth:authentication`
  - `:feature:auth:biometric`
  - `:feature:account:onboarding`
  - `:feature:account:profile`
  - `:feature:account:settings`
  - `:feature:system:force-update`
- **Bundles Comerciais**:
  - `:bundles:identity:basic`, `:bundles:identity:standard`, `:bundles:identity:pro`
  - `:bundles:foundation:basic`, `:bundles:foundation:standard`, `:bundles:foundation:pro`
- **Foco Técnico**: Autenticação com OmniBackend, biometria (BiometricPrompt), gerenciamento de sessão/tokens seguros, perfil do usuário, conformidade LGPD e manutenção remota.

---

### 4. 💬 `communication-specialist` (Comunicação & Engajamento)
- **Escopo de Features**:
  - `:feature:communication:message`
  - `:feature:communication:whatsapp-direct`
  - `:feature:customer:feedback`
  - `:feature:customer:reviews-store`
- **Bundles Comerciais**:
  - `:bundles:communication:basic`, `:bundles:communication:standard`, `:bundles:communication:pro`
  - `:bundles:social:basic`, `:bundles:social:standard`, `:bundles:social:pro`
- **Foco Técnico**: Chat em tempo real com mensagens de status, transbordo dinâmico via API do WhatsApp, In-App Review API e pesquisas de NPS.

---

### 5. 📅 `services-specialist` (Serviços, Assinaturas & Finanças)
- **Escopo de Features**:
  - `:feature:services:scheduling`
  - `:feature:services:subscriptions`
  - `:feature:checkout:quotation`
  - `:feature:checkout:payment`
- **Bundles Comerciais**:
  - `:bundles:services:basic`, `:bundles:services:standard`, `:bundles:services:pro`
  - `:bundles:subscriptions:basic`, `:bundles:subscriptions:standard`, `:bundles:subscriptions:pro`
  - `:bundles:finance:basic`, `:bundles:finance:standard`, `:bundles:finance:pro`
  - `:bundles:events:basic`, `:bundles:events:standard`, `:bundles:events:pro`
  - `:bundles:education:basic`, `:bundles:education:standard`, `:bundles:education:pro`
  - `:bundles:health:basic`, `:bundles:health:standard`, `:bundles:health:pro`
- **Foco Técnico**: Agendamentos por horário/profissional, cobranças recorrentes, planos de assinatura e cotação de serviços customizados.

---

### 6. 🧠 `platform-specialist` (Plataforma & Recursos Transversais)
- **Escopo de Features**:
  - `:feature:platform:ai-assistant`
  - `:feature:platform:analytics`
  - `:feature:platform:telemetry`
  - `:feature:platform:offline-sync`
  - `:feature:system:media-picker`
  - `:feature:system:multi-language`
- **Foco Técnico**: IA generativa contextual, pipeline de telemetria comportamental, fila de sincronização offline resiliente, captura e compressão de mídia e internacionalização dinâmica.

---

### 7. 🛠️ `build-master` (Build, Gradle & CI/CD Master)
- **Escopo de Arquivos**:
  - `build-logic/**`
  - `settings.gradle.kts`
  - `gradle/libs.versions.toml`
  - `.github/workflows/**`
  - Proguard / R8 rules
- **Foco Técnico**: Convenções de plugins Gradle, composite builds, publicação no GitHub Packages (`br.com.wgc:*`), controle de cache e pipelines de CI/CD.

---

### 8. 🧪 `qa-specialist` (Qualidade & Automação de Testes)
- **Escopo de Arquivos**:
  - `*/src/test/**` (Unit Tests)
  - `*/src/androidTest/**` (Instrumented Tests)
- **Foco Técnico**: Testes de ViewModel e UseCase (JUnit4, MockK, Turbine), testes de UI com Compose Test Rule, testes de integração de bundles e prevenção de regressões.

---

## 🔄 Fluxo de Trabalho do Orquestrador

Ao receber uma demanda do usuário:
1. **Análise & Decomposição**: O Orquestrador determina quais domínios são impactados.
2. **Delegação Especializada**: Invoca o especialista correto com instruções específicas de escopo.
3. **Validação de Testes & Build**: Aciona o `qa-specialist` e o `build-master` antes de aprovar a entrega.
4. **Consolidação**: Verifica a integridade do monorepo e apresenta a solução final ao usuário.
