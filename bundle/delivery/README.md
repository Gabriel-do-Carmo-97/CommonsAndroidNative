# 🚚 Commercial Bundle Domain: `delivery`

Esta pasta agrupa os pacotes comerciais de **Delivery & Logística** da WGC, divididos em três tiers de entrega (`basic`, `standard`, `pro`).

---

## 🏛️ Visão Geral dos Tiers e Diferenças

| Tier | Perfil do Cliente | Módulos e Recursos Agregados |
| :--- | :--- | :--- |
| **`basic`** | Lojas de bairro com entrega própria | Acompanhamento simples de pedidos. |
| **`standard`** | Delivery estruturado | Mapas, geolocalização e rotas. |
| **`pro`** | Operações logísticas complexas | Telemetria GPS em background, cercas virtuais, dispatch e mapas offline. |

---

## 📋 Detalhamento dos Submódulos por Tier

### 1. `bundle/delivery/basic`
- **O que agrupa**: `:order-tracking` (Status básico de entrega).
- **Onde usar**: Lojas que só querem mostrar o status textual do pedido ao cliente.

### 2. `bundle/delivery/standard`
- **O que agrupa**: `:order-tracking`, `:maps`, `:stores`.
- **Onde usar**: Apps de delivery com mapa interativo e seletor de unidades.

### 3. `bundle/delivery/pro`
- **O que agrupa**: `:driver-app`, `:telemetry`, `:geofencing`, `:dispatch`, `:offline-maps`, `:order-tracking`.
- **Onde usar**: Plataformas avançadas de entrega, motoboys e rastreamento em tempo real.
