# CommonsAndroidNative

Monorepo de bibliotecas e SDKs Android reutilizáveis da organização **WGC**, para construção acelerada de aplicativos comerciais, e-commerce, delivery, serviços e soluções white-label.

Todos os módulos são publicados e distribuídos individualmente via **GitHub Packages** (`br.com.wgc:*`).

---

## 📦 Estrutura Completa de Módulos (33 Módulos Organizados por Domínio)

Os módulos estão agrupados na pasta `feature/` por domínios de negócio e registrados com separação por `:` (padrão `CoreAndroidNative`: `:feature:<tema>:<nome>`), mantendo a publicação de primeiro nível (`br.com.wgc:<module>`).

### 🚀 Aplicação & Showcase
| Módulo | Tipo | Caminho | Descrição |
| :--- | :--- | :--- | :--- |
| [`:app`](./app) | Aplicação | `app/` | Showcase interativo com BottomBar e navegação para testar todos os módulos. |

---

### 🔐 1. Auth & Segurança (`feature/auth/`)
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:feature:auth:authentication`](./feature/auth/authentication) | Biblioteca | Fluxos de Login, Cadastro e Recuperação integrados ao `OmniBackend`. |
| [`:feature:auth:biometric`](./feature/auth/biometric) | Biblioteca | Autenticação Biométrica (Fingerprint/FaceID), App Lock e PIN de segurança. |

---

### 👤 2. Conta & Usuário (`feature/account/`)
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:feature:account:onboarding`](./feature/account/onboarding) | Biblioteca | Pagers de boas-vindas, tour do app e solicitação contextual de permissões. |
| [`:feature:account:profile`](./feature/account/profile) | Biblioteca | Perfil do usuário, avatar, gestão de endereços e conformidade LGPD. |
| [`:feature:account:settings`](./feature/account/settings) | Biblioteca | Preferências do aplicativo, tema dinâmico e configurações de notificações. |

---

### 🏪 3. Vitrine & Descoberta (`feature/storefront/`)
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:feature:storefront:catalog`](./feature/storefront/catalog) | Biblioteca | Cardápio/Catálogo digital, categorização, opções e customização de itens. |
| [`:feature:storefront:search`](./feature/storefront/search) | Biblioteca | Busca reativa com debounce, histórico de pesquisas e filtros avançados. |
| [`:feature:storefront:promotions`](./feature/storefront/promotions) | Biblioteca | Banners promocionais, carrossel de ofertas e cupons de desconto. |
| [`:feature:storefront:stores`](./feature/storefront/stores) | Biblioteca | Localização de filiais, seletor de lojas físicas e raio de proximidade via GPS. |

---

### 💳 4. Checkout & Compra (`feature/checkout/`)
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:feature:checkout:cart`](./feature/checkout/cart) | Biblioteca | Carrinho persistente, cálculo de frete/taxas e validação de disponibilidade. |
| [`:feature:checkout:payment`](./feature/checkout/payment) | Biblioteca | Gateway de pagamento, PIX com QRCode copia-e-cola, cartão de crédito e histórico. |
| [`:feature:checkout:quotation`](./feature/checkout/quotation) | Biblioteca | Solicitação e negociação de orçamentos personalizados com anexo de mídias. |

---

### 🚚 5. Entrega & Logística (`feature/delivery/`)
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:feature:delivery:order-tracking`](./feature/delivery/order-tracking) | Biblioteca | Rastreio em tempo real com timeline de status e tempo estimado de entrega. |
| [`:feature:delivery:maps`](./feature/delivery/maps) | Biblioteca | Exibição de mapas, rotas, geolocalização e pin de destinos. |
| [`:feature:delivery:driver-app`](./feature/delivery/driver-app) | Biblioteca | Interface e fluxo operacional para motoristas e entregadores parceiros. |
| [`:feature:delivery:dispatch`](./feature/delivery/dispatch) | Biblioteca | Gestão de expedição, despacho de encomendas e distribuição de rotas. |
| [`:feature:delivery:geofencing`](./feature/delivery/geofencing) | Biblioteca | Cercas virtuais com alertas de entrada/saída de perímetro operacional. |
| [`:feature:delivery:offline-maps`](./feature/delivery/offline-maps) | Biblioteca | Armazenamento e renderização de mapas em cache para operação offline. |

---

### 💬 6. Comunicação (`feature/communication/`)
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:feature:communication:message`](./feature/communication/message) | Biblioteca | Chat em tempo real, suporte ao cliente, FCM push e mensagens in-app. |
| [`:feature:communication:whatsapp-direct`](./feature/communication/whatsapp-direct) | Biblioteca | Transbordo direto de pedidos e atendimento formatado via API WhatsApp. |

---

### ⭐ 7. Fidelidade & Satisfação (`feature/customer/`)
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:feature:customer:feedback`](./feature/customer/feedback) | Biblioteca | In-App Reviews do Google Play, pesquisa NPS e Shake-to-Report de bugs. |
| [`:feature:customer:loyalty`](./feature/customer/loyalty) | Biblioteca | Cartão fidelidade digital, clube de pontos e acúmulo de cashback. |
| [`:feature:customer:reviews-store`](./feature/customer/reviews-store) | Biblioteca | Avaliações por estrelas (1-5) e comentários diretos da loja/estabelecimento. |

---

### 📅 8. Serviços & Assinaturas (`feature/services/`)
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:feature:services:scheduling`](./feature/services/scheduling) | Biblioteca | Agenda de horários, seleção de profissionais e reagendamento de serviços. |
| [`:feature:services:subscriptions`](./feature/services/subscriptions) | Biblioteca | Planos recorrentes, assinaturas periódicas e gestão de benefícios ativos. |

---

### ⚙️ 9. Sistema & Dispositivo (`feature/system/`)
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:feature:system:force-update`](./feature/system/force-update) | Biblioteca | Bloqueio de versão obsoleta (Force Update) e tela de manutenção remota. |
| [`:feature:system:media-picker`](./feature/system/media-picker) | Biblioteca | PhotoPicker, integração com câmera, compressão e scanner MLKit. |
| [`:feature:system:multi-language`](./feature/system/multi-language) | Biblioteca | Internacionalização dinâmica e alternância de idiomas em tempo de execução. |

---

### 🧠 10. Plataforma & Infraestrutura (`feature/platform/`)
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:feature:platform:ai-assistant`](./feature/platform/ai-assistant) | Biblioteca | Assistente conversacional inteligente com suporte a recomendações. |
| [`:feature:platform:analytics`](./feature/platform/analytics) | Biblioteca | Telemetria comportamental, rastreio de eventos e métricas de conversão. |
| [`:feature:platform:emergency`](./feature/platform/emergency) | Biblioteca | Botão de pânico, contatos de emergência e transmissão rápida de localização. |
| [`:feature:platform:offline-sync`](./feature/platform/offline-sync) | Biblioteca | Fila de sincronização resiliente com reconexão automática e retry. |
| [`:feature:platform:telemetry`](./feature/platform/telemetry) | Biblioteca | Monitoramento de performance, logs de diagnósticos e integridade do app. |

---

## 📦 Bundles Comerciais (14 Domínios × 3 Níveis = 42 Bundles)

Para simplificar a composição comercial de soluções white-label e aplicativos clientes (como no projeto `SolutionsAndroid`), o repositório disponibiliza **42 bundles modulares** organizados sob a pasta `bundle/` nos níveis **Basic**, **Standard** e **Pro**.

Cada bundle agrega e expõe via `api(...)` os módulos granulares de features necessários para o respectivo escopo.

### Coordenadas de Publicação
Todos os bundles são publicados no GitHub Packages sob o groupId `br.com.wgc`:
```text
br.com.wgc:bundle-<dominio>-<nivel>:<versao>
```
*Exemplo:* `br.com.wgc:bundle-ecommerce-pro:0.0.1` ou `br.com.wgc:bundle-delivery-standard:0.0.1`.

### Tabela de Domínios e Bundles
| Domínio | Módulo Gradle | Nível | Módulos Inclusos |
| :--- | :--- | :--- | :--- |
| **Communication** | `:bundle:communication:basic` | Basic | `:feature:communication:message`, `:feature:communication:whatsapp-direct` |
| | `:bundle:communication:standard` | Standard | Basic + `:feature:system:media-picker`, `:feature:customer:feedback` |
| | `:bundle:communication:pro` | Pro | Standard + `:feature:platform:ai-assistant` |
| **Delivery** | `:bundle:delivery:basic` | Basic | `:feature:delivery:order-tracking`, `:feature:delivery:maps` |
| | `:bundle:delivery:standard` | Standard | Basic + `:feature:delivery:driver-app`, `:feature:platform:telemetry` |
| | `:bundle:delivery:pro` | Pro | Standard + `:feature:delivery:dispatch`, `:feature:delivery:geofencing`, `:feature:delivery:offline-maps` |
| **Ecommerce** | `:bundle:ecommerce:basic` | Basic | `:feature:storefront:catalog`, `:feature:checkout:cart`, `:feature:checkout:payment` |
| | `:bundle:ecommerce:standard` | Standard | Basic + `:feature:storefront:search`, `:feature:storefront:promotions`, `:feature:customer:reviews-store` |
| | `:bundle:ecommerce:pro` | Pro | Standard + `:feature:customer:loyalty`, `:feature:storefront:stores`, `:feature:platform:ai-assistant` |
| **Education** | `:bundle:education:basic` | Basic | `:feature:services:scheduling`, `:feature:account:profile` |
| | `:bundle:education:standard` | Standard | Basic + `:feature:communication:message`, `:feature:system:media-picker` |
| | `:bundle:education:pro` | Pro | Standard + `:feature:account:settings`, `:feature:customer:feedback`, `:feature:platform:ai-assistant` |
| **Emergency** | `:bundle:emergency:basic` | Basic | `:feature:platform:emergency`, `:feature:delivery:maps` |
| | `:bundle:emergency:standard` | Standard | Basic + `:feature:platform:telemetry`, `:feature:communication:whatsapp-direct` |
| | `:bundle:emergency:pro` | Pro | Standard + `:feature:delivery:geofencing`, `:feature:delivery:offline-maps` |
| **Events** | `:bundle:events:basic` | Basic | `:feature:storefront:catalog`, `:feature:checkout:payment` |
| | `:bundle:events:standard` | Standard | Basic + `:feature:services:scheduling`, `:feature:delivery:maps` |
| | `:bundle:events:pro` | Pro | Standard + `:feature:system:media-picker`, `:feature:storefront:promotions` |
| **Finance** | `:bundle:finance:basic` | Basic | `:feature:checkout:payment` |
| | `:bundle:finance:standard` | Standard | Basic + `:feature:auth:biometric`, `:feature:account:settings` |
| | `:bundle:finance:pro` | Pro | Standard + `:feature:services:subscriptions`, `:feature:platform:analytics`, `:feature:platform:ai-assistant` |
| **Foundation** | `:bundle:foundation:basic` | Basic | `:feature:account:settings`, `:feature:system:force-update` |
| | `:bundle:foundation:standard` | Standard | Basic + `:feature:platform:analytics` |
| | `:bundle:foundation:pro` | Pro | Standard + `:feature:system:multi-language`, `:feature:platform:offline-sync` |
| **Health** | `:bundle:health:basic` | Basic | `:feature:services:scheduling`, `:feature:communication:message` |
| | `:bundle:health:standard` | Standard | Basic + `:feature:auth:biometric`, `:feature:system:media-picker` |
| | `:bundle:health:pro` | Pro | Standard + `:feature:checkout:quotation`, `:feature:platform:emergency` |
| **Identity** | `:bundle:identity:basic` | Basic | `:feature:auth:authentication`, `:feature:account:profile` |
| | `:bundle:identity:standard` | Standard | Basic + `:feature:auth:biometric` |
| | `:bundle:identity:pro` | Pro | Standard + `:feature:account:onboarding`, `:feature:system:multi-language` |
| **Real Estate** | `:bundle:real-estate:basic` | Basic | `:feature:storefront:catalog`, `:feature:storefront:search` |
| | `:bundle:real-estate:standard` | Standard | Basic + `:feature:delivery:maps`, `:feature:services:scheduling` |
| | `:bundle:real-estate:pro` | Pro | Standard + `:feature:system:media-picker`, `:feature:checkout:quotation` |
| **Services** | `:bundle:services:basic` | Basic | `:feature:services:scheduling`, `:feature:storefront:stores` |
| | `:bundle:services:standard` | Standard | Basic + `:feature:checkout:quotation`, `:feature:customer:feedback` |
| | `:bundle:services:pro` | Pro | Standard + `:feature:customer:reviews-store`, `:feature:checkout:payment` |
| **Social** | `:bundle:social:basic` | Basic | `:feature:account:profile`, `:feature:communication:message` |
| | `:bundle:social:standard` | Standard | Basic + `:feature:system:media-picker`, `:feature:customer:reviews-store` |
| | `:bundle:social:pro` | Pro | Standard + `:feature:customer:feedback`, `:feature:platform:ai-assistant` |
| **Subscriptions** | `:bundle:subscriptions:basic` | Basic | `:feature:services:subscriptions`, `:feature:checkout:payment` |
| | `:bundle:subscriptions:standard` | Standard | Basic + `:feature:storefront:promotions` |
| | `:bundle:subscriptions:pro` | Pro | Standard + `:feature:system:force-update`, `:feature:platform:analytics` |

---

## 🛠️ Tecnologias e Ferramentas

- **Linguagem**: Kotlin 2.2+ (Target JVM 17)
- **UI Framework**: Jetpack Compose com Material 3
- **Injeção de Dependência**: Dagger Hilt com KSP
- **Arquitetura**: MVVM com Unidirectional Data Flow (UDF), Coroutines e Kotlin Flows
- **Build System**: Gradle 8.x com Version Catalogs (`gradle/libs.versions.toml`)
- **Qualidade & Análise Estática**: Detekt e Dokka (Documentação KDoc)
- **CI/CD**: GitHub Actions com execução de testes unitários e publicação automática de AARs

---

## 🔑 Configuração Local de Credenciais (GitHub Packages)

Para que o Gradle consiga baixar e publicar dependências do GitHub Packages da organização, defina as credenciais de uma das seguintes formas:

### Opção 1: Via variáveis de ambiente
```bash
export GPR_USER="seu-usuario-github"
export GPR_KEY="seu-personal-access-token" # token com permissão read:packages / write:packages
```
*(No Windows PowerShell: `$env:GPR_USER="seu-usuario"` e `$env:GPR_KEY="seu-token"`)*

### Opção 2: No arquivo `local.properties` (não commitado)
```properties
gpr.user=seu-usuario-github
gpr.key=seu-personal-access-token
```

---

## 🚀 Como Executar

### 1. Compilar o projeto
```bash
./gradlew build
```

### 2. Executar os Testes Unitários
```bash
./gradlew test
```

### 3. Executar a Aplicação Showcase (`:app`)
```bash
./gradlew :app:installDebug
```
