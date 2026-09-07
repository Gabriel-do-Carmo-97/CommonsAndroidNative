# 🤖 Sistema de Agentes IA & Orquestrador WGC

Este diretório contém o **Sistema de Agentes de Inteligência Artificial** para operação e automação da Fábrica de Software **WGC**.

---

## 🏛️ Estrutura do Sistema de Agentes

| Arquivo | Papel | Descrição |
| :--- | :--- | :--- |
| [`00_orchestrator_agent.md`](./00_orchestrator_agent.md) | **Agente Orquestrador Master** | Recebe o briefing do cliente/nicho de mercado e define os módulos necessários do monorepo. |
| [`01_specialist_core_platform_agent.md`](./01_specialist_core_platform_agent.md) | **Especialista em Plataforma & Core** | Especializado em `:authentication`, `:profile`, `:settings`, `:biometric`, `:onboarding`, `:force-update`, `:feedback`. |
| [`02_specialist_ecommerce_delivery_agent.md`](./02_specialist_ecommerce_delivery_agent.md) | **Especialista em E-Commerce & Delivery** | Especializado em `:catalog`, `:cart`, `:order-tracking`, `:promotions`, `:whatsapp-direct`. |
| [`03_specialist_services_loyalty_agent.md`](./03_specialist_services_loyalty_agent.md) | **Especialista em Serviços & Fidelização** | Especializado em `:scheduling`, `:quotation`, `:loyalty`, `:stores`, `:reviews-store`. |
| [`04_specialist_media_platform_agent.md`](./04_specialist_media_platform_agent.md) | **Especialista em Mídia & Integrações** | Especializado em `:media-picker`, `:search`, `:maps`, `:message`, `:payment`. |

---

## 🚀 Como Utilizar o Sistema de Agentes

1. **Início da Demanda**: Abra o prompt do [`00_orchestrator_agent.md`](./00_orchestrator_agent.md) e passe o briefing do novo aplicativo que sua fábrica de software precisa criar (ex: *"Preciso criar um app para uma Pizzaria com entrega e programa de fidelidade"*).
2. **Definição do Blueprint**: O Orquestrador retornará a lista exata dos módulos do `CommonsAndroidNative` que devem ser conectados no `:app`.
3. **Execução Especializada**: Chame o agente especialista responsável pelo grupo de módulos para implementar as regras de negócio e telas específicas.
