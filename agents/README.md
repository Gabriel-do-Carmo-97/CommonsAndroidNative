# 🤖 Agentes Especialistas — CommonsAndroidNative

Esta pasta contém as especificações, responsabilidades e diretrizes operacionais de cada Agente Especialista que atua no monorepo `CommonsAndroidNative`.

---

## 🏛️ Estrutura da Equipe

| Arquivo | Agente | Escopo Principal |
|---|---|---|
| [`orchestrator.md`](file:///c:/Users/gcarm/Documents/GitHub/CommonsAndroidNative/agents/orchestrator.md) | **Orquestrador Central** | Triagem, planejamento, delegação de tarefas e validação final |
| [`ecommerce-specialist.md`](file:///c:/Users/gcarm/Documents/GitHub/CommonsAndroidNative/agents/ecommerce-specialist.md) | **E-commerce & Varejo** | Catálogo, carrinho, checkout, pagamentos, cupons e fidelidade |
| [`logistics-specialist.md`](file:///c:/Users/gcarm/Documents/GitHub/CommonsAndroidNative/agents/logistics-specialist.md) | **Logística & Delivery** | Rastreamento em tempo real, mapas, geofencing, app do entregador |
| [`identity-specialist.md`](file:///c:/Users/gcarm/Documents/GitHub/CommonsAndroidNative/agents/identity-specialist.md) | **Identidade & Segurança** | Autenticação OmniBackend, biometria, perfil, LGPD, force update |
| [`communication-specialist.md`](file:///c:/Users/gcarm/Documents/GitHub/CommonsAndroidNative/agents/communication-specialist.md) | **Comunicação & Suporte** | Mensagens/chat em tempo real, WhatsApp Direct, avaliações e NPS |
| [`services-specialist.md`](file:///c:/Users/gcarm/Documents/GitHub/CommonsAndroidNative/agents/services-specialist.md) | **Serviços & Assinaturas** | Agendamentos, planos de assinatura, cotação e cobranças recorrentes |
| [`platform-specialist.md`](file:///c:/Users/gcarm/Documents/GitHub/CommonsAndroidNative/agents/platform-specialist.md) | **Plataforma & Transversais** | IA assistente, telemetria, offline-sync, mídia e internacionalização |
| [`build-master.md`](file:///c:/Users/gcarm/Documents/GitHub/CommonsAndroidNative/agents/build-master.md) | **Build & CI/CD Master** | Gradle, build-logic, GitHub Packages (`br.com.wgc:*`), Proguard, CI/CD |
| [`qa-specialist.md`](file:///c:/Users/gcarm/Documents/GitHub/CommonsAndroidNative/agents/qa-specialist.md) | **QA & Automação de Testes** | Testes unitários (JUnit4/MockK/Turbine), testes instrumentados e Compose |

---

## 📜 Regras Fundamentais da Equipe

1. **Documentação KDoc Obrigatória**: 100% de classes, interfaces, funções e propriedades públicas ou internas expostas DEVEM possuir documentação KDoc completa (`@param`, `@return`, `@throws`).
2. **Nomenclatura Padrão**: Publicação exclusiva sob namespace `br.com.wgc:*` e pacotes `br.com.wgc.commons.*`.
3. **Padrão de Bundles**: Todo bundle possui singleton (`*Bundle.kt`) com inicialização segura (`initialize()`) e contrato de validação.
4. **Qualidade Contínua**: Todo código novo deve passar por validação estática via **Detekt** e testes automatizados (unitários e instrumentados) antes do merge.
