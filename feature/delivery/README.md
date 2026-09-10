# Categoria: `delivery` (Logística, Entrega & Rastreamento)

Esta pasta agrupa os módulos de **Logística de Última Milha, Rastreamento GPS, Mapas e Motoboys**.

---

## 📦 Módulos Nesta Pasta

| Módulo | Caminho | Descrição |
| :--- | :--- | :--- |
| **`:feature:delivery:order-tracking`** | [`./order-tracking`](./order-tracking) | Acompanhamento do Pedido em Tempo Real com Status e ETA. |
| **`:feature:delivery:maps`** | [`./maps`](./maps) | Geolocalização e Rastreamento GPS via `core-location`. |
| **`:feature:delivery:driver-app`** | [`./driver-app`](./driver-app) | Aplicativo operacional do entregador/motoboy. |
| **`:feature:delivery:dispatch`** | [`./dispatch`](./dispatch) | Motor de alocação automática de pedidos (Matching). |
| **`:feature:delivery:geofencing`** | [`./geofencing`](./geofencing) | Cercas virtuais automáticas no mapa. |
| **`:feature:delivery:offline-maps`** | [`./offline-maps`](./offline-maps) | Navegação Turn-by-Turn e mapas offline. |

---

## 🛠️ Importações e Build Logic
- Consomem `br.com.wgc:core-location`, `br.com.wgc:design-system` e convencionam pelo `build-logic`.
