# 💬 Commercial Bundle Domain: `communication`

Esta pasta agrupa os pacotes comerciais de **Comunicação & Mensageria** da WGC, divididos em três tiers de entrega (`basic`, `standard`, `pro`) para atender diferentes portes de aplicações.

---

## 🏛️ Visão Geral dos Tiers e Diferenças

| Tier | Perfil do Cliente | Módulos e Recursos Agregados |
| :--- | :--- | :--- |
| **`basic`** | Pequenos comércios, MVPs | Envio rápido de mensagens e suporte básico via WhatsApp. |
| **`standard`** | Comércios estabelecidos | Chat em tempo real (`:message`) e notificações push. |
| **`pro`** | Empresas Enterprise | Suíte completa de chat, FCM, WhatsApp Business e Assistente de IA. |

---

## 📋 Detalhamento dos Submódulos por Tier

### 1. `bundle/communication/basic`
- **O que agrupa**: `:whatsapp-direct`.
- **Onde usar**: Apps de comércios que precisam apenas direcionar pedidos e dúvidas direto para o WhatsApp da loja.

### 2. `bundle/communication/standard`
- **O que agrupa**: `:whatsapp-direct`, `:message` (Chat em tempo real e FCM).
- **Onde usar**: Apps que exigem um canal de atendimento próprio dentro da aplicação com notificações push.

### 3. `bundle/communication/pro`
- **O que agrupa**: `:message`, `:whatsapp-direct`, `:ai-assistant` (Chatbot IA / Vertex AI).
- **Onde usar**: Soluções omnichannel de atendimento com inteligência artificial integrada.
