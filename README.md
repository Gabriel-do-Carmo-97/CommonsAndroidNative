# CommonsAndroidNative

Monorepo de bibliotecas e SDKs Android reutilizÃ¡veis da organizaÃ§Ã£o **WGC**, para construÃ§Ã£o acelerada de aplicativos comerciais, e-commerce, delivery, serviÃ§os e soluÃ§Ãµes white-label.

Todos os mÃ³dulos sÃ£o publicados e distribuÃ­dos individualmente via **GitHub Packages** (`br.com.wgc:*`).

---

## ðŸ“¦ Estrutura Completa de MÃ³dulos (33 MÃ³dulos Organizados por DomÃ­nio)

Os mÃ³dulos estÃ£o agrupados na pasta `feature/` por domÃ­nios de negÃ³cio e registrados com separaÃ§Ã£o por `:` (padrÃ£o `CoreAndroidNative`: `:feature:<tema>:<nome>`), mantendo a publicaÃ§Ã£o de primeiro nÃ­vel (`br.com.wgc:<module>`).

### ðŸš€ AplicaÃ§Ã£o & Showcase
| MÃ³dulo | Tipo | Caminho | DescriÃ§Ã£o |
| :--- | :--- | :--- | :--- |
| [`:app`](./app) | AplicaÃ§Ã£o | `app/` | Showcase interativo com BottomBar e navegaÃ§Ã£o para testar todos os mÃ³dulos. |

---

### ðŸ” 1. Auth & SeguranÃ§a (`feature/auth/`)
| MÃ³dulo | Tipo | DescriÃ§Ã£o |
| :--- | :--- | :--- |
| [`:feature:auth:authentication`](./feature/auth/authentication) | Biblioteca | Fluxos de Login, Cadastro e RecuperaÃ§Ã£o integrados ao `OmniBackend`. |
| [`:feature:auth:biometric`](./feature/auth/biometric) | Biblioteca | AutenticaÃ§Ã£o BiomÃ©trica (Fingerprint/FaceID), App Lock e PIN de seguranÃ§a. |

---

### ðŸ‘¤ 2. Conta & UsuÃ¡rio (`feature/account/`)
| MÃ³dulo | Tipo | DescriÃ§Ã£o |
| :--- | :--- | :--- |
| [`:feature:account:onboarding`](./feature/account/onboarding) | Biblioteca | Pagers de boas-vindas, tour do app e solicitaÃ§Ã£o contextual de permissÃµes. |
| [`:feature:account:profile`](./feature/account/profile) | Biblioteca | Perfil do usuÃ¡rio, avatar, gestÃ£o de endereÃ§os e conformidade LGPD. |
| [`:feature:account:settings`](./feature/account/settings) | Biblioteca | PreferÃªncias do aplicativo, tema dinÃ¢mico e configuraÃ§Ãµes de notificaÃ§Ãµes. |

---

### ðŸª 3. Vitrine & Descoberta (`feature/storefront/`)
| MÃ³dulo | Tipo | DescriÃ§Ã£o |
| :--- | :--- | :--- |
| [`:feature:storefront:catalog`](./feature/storefront/catalog) | Biblioteca | CardÃ¡pio/CatÃ¡logo digital, categorizaÃ§Ã£o, opÃ§Ãµes e customizaÃ§Ã£o de itens. |
| [`:feature:storefront:search`](./feature/storefront/search) | Biblioteca | Busca reativa com debounce, histÃ³rico de pesquisas e filtros avanÃ§ados. |
| [`:feature:storefront:promotions`](./feature/storefront/promotions) | Biblioteca | Banners promocionais, carrossel de ofertas e cupons de desconto. |
| [`:feature:storefront:stores`](./feature/storefront/stores) | Biblioteca | LocalizaÃ§Ã£o de filiais, seletor de lojas fÃ­sicas e raio de proximidade via GPS. |

---

### ðŸ’³ 4. Checkout & Compra (`feature/checkout/`)
| MÃ³dulo | Tipo | DescriÃ§Ã£o |
| :--- | :--- | :--- |
| [`:feature:checkout:cart`](./feature/checkout/cart) | Biblioteca | Carrinho persistente, cÃ¡lculo de frete/taxas e validaÃ§Ã£o de disponibilidade. |
| [`:feature:checkout:payment`](./feature/checkout/payment) | Biblioteca | Gateway de pagamento, PIX com QRCode copia-e-cola, cartÃ£o de crÃ©dito e histÃ³rico. |
| [`:feature:checkout:quotation`](./feature/checkout/quotation) | Biblioteca | SolicitaÃ§Ã£o e negociaÃ§Ã£o de orÃ§amentos personalizados com anexo de mÃ­dias. |

---

### ðŸšš 5. Entrega & LogÃ­stica (`feature/delivery/`)
| MÃ³dulo | Tipo | DescriÃ§Ã£o |
| :--- | :--- | :--- |
| [`:feature:delivery:order-tracking`](./feature/delivery/order-tracking) | Biblioteca | Rastreio em tempo real com timeline de status e tempo estimado de entrega. |
| [`:feature:delivery:maps`](./feature/delivery/maps) | Biblioteca | ExibiÃ§Ã£o de mapas, rotas, geolocalizaÃ§Ã£o e pin de destinos. |
| [`:feature:delivery:driver-app`](./feature/delivery/driver-app) | Biblioteca | Interface e fluxo operacional para motoristas e entregadores parceiros. |
| [`:feature:delivery:dispatch`](./feature/delivery/dispatch) | Biblioteca | GestÃ£o de expediÃ§Ã£o, despacho de encomendas e distribuiÃ§Ã£o de rotas. |
| [`:feature:delivery:geofencing`](./feature/delivery/geofencing) | Biblioteca | Cercas virtuais com alertas de entrada/saÃ­da de perÃ­metro operacional. |
| [`:feature:delivery:offline-maps`](./feature/delivery/offline-maps) | Biblioteca | Armazenamento e renderizaÃ§Ã£o de mapas em cache para operaÃ§Ã£o offline. |

---

### ðŸ’¬ 6. ComunicaÃ§Ã£o (`feature/communication/`)
| MÃ³dulo | Tipo | DescriÃ§Ã£o |
| :--- | :--- | :--- |
| [`:feature:communication:message`](./feature/communication/message) | Biblioteca | Chat em tempo real, suporte ao cliente, FCM push e mensagens in-app. |
| [`:feature:communication:whatsapp-direct`](./feature/communication/whatsapp-direct) | Biblioteca | Transbordo direto de pedidos e atendimento formatado via API WhatsApp. |

---

### â­ 7. Fidelidade & SatisfaÃ§Ã£o (`feature/customer/`)
| MÃ³dulo | Tipo | DescriÃ§Ã£o |
| :--- | :--- | :--- |
| [`:feature:customer:feedback`](./feature/customer/feedback) | Biblioteca | In-App Reviews do Google Play, pesquisa NPS e Shake-to-Report de bugs. |
| [`:feature:customer:loyalty`](./feature/customer/loyalty) | Biblioteca | CartÃ£o fidelidade digital, clube de pontos e acÃºmulo de cashback. |
| [`:feature:customer:reviews-store`](./feature/customer/reviews-store) | Biblioteca | AvaliaÃ§Ãµes por estrelas (1-5) e comentÃ¡rios diretos da loja/estabelecimento. |

---

### ðŸ“… 8. ServiÃ§os & Assinaturas (`feature/services/`)
| MÃ³dulo | Tipo | DescriÃ§Ã£o |
| :--- | :--- | :--- |
| [`:feature:services:scheduling`](./feature/services/scheduling) | Biblioteca | Agenda de horÃ¡rios, seleÃ§Ã£o de profissionais e reagendamento de serviÃ§os. |
| [`:feature:services:subscriptions`](./feature/services/subscriptions) | Biblioteca | Planos recorrentes, assinaturas periÃ³dicas e gestÃ£o de benefÃ­cios ativos. |

---

### âš™ï¸ 9. Sistema & Dispositivo (`feature/system/`)
| MÃ³dulo | Tipo | DescriÃ§Ã£o |
| :--- | :--- | :--- |
| [`:feature:system:force-update`](./feature/system/force-update) | Biblioteca | Bloqueio de versÃ£o obsoleta (Force Update) e tela de manutenÃ§Ã£o remota. |
| [`:feature:system:media-picker`](./feature/system/media-picker) | Biblioteca | PhotoPicker, integraÃ§Ã£o com cÃ¢mera, compressÃ£o e scanner MLKit. |
| [`:feature:system:multi-language`](./feature/system/multi-language) | Biblioteca | InternacionalizaÃ§Ã£o dinÃ¢mica e alternÃ¢ncia de idiomas em tempo de execuÃ§Ã£o. |

---

### ðŸ§  10. Plataforma & Infraestrutura (`feature/platform/`)
| MÃ³dulo | Tipo | DescriÃ§Ã£o |
| :--- | :--- | :--- |
| [`:feature:platform:ai-assistant`](./feature/platform/ai-assistant) | Biblioteca | Assistente conversacional inteligente com suporte a recomendaÃ§Ãµes. |
| [`:feature:platform:analytics`](./feature/platform/analytics) | Biblioteca | Telemetria comportamental, rastreio de eventos e mÃ©tricas de conversÃ£o. |
| [`:feature:platform:emergency`](./feature/platform/emergency) | Biblioteca | BotÃ£o de pÃ¢nico, contatos de emergÃªncia e transmissÃ£o rÃ¡pida de localizaÃ§Ã£o. |
| [`:feature:platform:offline-sync`](./feature/platform/offline-sync) | Biblioteca | Fila de sincronizaÃ§Ã£o resiliente com reconexÃ£o automÃ¡tica e retry. |
| [`:feature:platform:telemetry`](./feature/platform/telemetry) | Biblioteca | Monitoramento de performance, logs de diagnÃ³sticos e integridade do app. |

---

## ðŸ“¦ Bundles Comerciais (14 DomÃ­nios Ã— 3 NÃ­veis = 42 Bundles)

Para simplificar a composiÃ§Ã£o comercial de soluÃ§Ãµes white-label e aplicativos clientes (como no projeto `SolutionsAndroid`), o repositÃ³rio disponibiliza **42 bundles modulares** organizados sob a pasta `bundles/` nos nÃ­veis **Basic**, **Standard** e **Pro**.

Cada bundle agrega e expÃµe via `api(...)` os mÃ³dulos granulares de features necessÃ¡rios para o respectivo escopo.

### Coordenadas de PublicaÃ§Ã£o
Todos os bundles sÃ£o publicados no GitHub Packages sob o groupId `br.com.wgc`:
```text
br.com.wgc:bundle-<dominio>-<nivel>:<versao>
```
*Exemplo:* `br.com.wgc:bundle-ecommerce-pro:0.0.1` ou `br.com.wgc:bundle-delivery-standard:0.0.1`.

### Tabela de DomÃ­nios e Bundles
| DomÃ­nio | MÃ³dulo Gradle | NÃ­vel | MÃ³dulos Inclusos |
| :--- | :--- | :--- | :--- |
| **Communication** | `:bundles:communication:basic` | Basic | `:feature:communication:message`, `:feature:communication:whatsapp-direct` |
| | `:bundles:communication:standard` | Standard | Basic + `:feature:system:media-picker`, `:feature:customer:feedback` |
| | `:bundles:communication:pro` | Pro | Standard + `:feature:platform:ai-assistant` |
| **Delivery** | `:bundles:delivery:basic` | Basic | `:feature:delivery:order-tracking`, `:feature:delivery:maps` |
| | `:bundles:delivery:standard` | Standard | Basic + `:feature:delivery:driver-app`, `:feature:platform:telemetry` |
| | `:bundles:delivery:pro` | Pro | Standard + `:feature:delivery:dispatch`, `:feature:delivery:geofencing`, `:feature:delivery:offline-maps` |
| **Ecommerce** | `:bundles:ecommerce:basic` | Basic | `:feature:storefront:catalog`, `:feature:checkout:cart`, `:feature:checkout:payment` |
| | `:bundles:ecommerce:standard` | Standard | Basic + `:feature:storefront:search`, `:feature:storefront:promotions`, `:feature:customer:reviews-store` |
| | `:bundles:ecommerce:pro` | Pro | Standard + `:feature:customer:loyalty`, `:feature:storefront:stores`, `:feature:platform:ai-assistant` |
| **Education** | `:bundles:education:basic` | Basic | `:feature:services:scheduling`, `:feature:account:profile` |
| | `:bundles:education:standard` | Standard | Basic + `:feature:communication:message`, `:feature:system:media-picker` |
| | `:bundles:education:pro` | Pro | Standard + `:feature:account:settings`, `:feature:customer:feedback`, `:feature:platform:ai-assistant` |
| **Emergency** | `:bundles:emergency:basic` | Basic | `:feature:platform:emergency`, `:feature:delivery:maps` |
| | `:bundles:emergency:standard` | Standard | Basic + `:feature:platform:telemetry`, `:feature:communication:whatsapp-direct` |
| | `:bundles:emergency:pro` | Pro | Standard + `:feature:delivery:geofencing`, `:feature:delivery:offline-maps` |
| **Events** | `:bundles:events:basic` | Basic | `:feature:storefront:catalog`, `:feature:checkout:payment` |
| | `:bundles:events:standard` | Standard | Basic + `:feature:services:scheduling`, `:feature:delivery:maps` |
| | `:bundles:events:pro` | Pro | Standard + `:feature:system:media-picker`, `:feature:storefront:promotions` |
| **Finance** | `:bundles:finance:basic` | Basic | `:feature:checkout:payment` |
| | `:bundles:finance:standard` | Standard | Basic + `:feature:auth:biometric`, `:feature:account:settings` |
| | `:bundles:finance:pro` | Pro | Standard + `:feature:services:subscriptions`, `:feature:platform:analytics`, `:feature:platform:ai-assistant` |
| **Foundation** | `:bundles:foundation:basic` | Basic | `:feature:account:settings`, `:feature:system:force-update` |
| | `:bundles:foundation:standard` | Standard | Basic + `:feature:platform:analytics` |
| | `:bundles:foundation:pro` | Pro | Standard + `:feature:system:multi-language`, `:feature:platform:offline-sync` |
| **Health** | `:bundles:health:basic` | Basic | `:feature:services:scheduling`, `:feature:communication:message` |
| | `:bundles:health:standard` | Standard | Basic + `:feature:auth:biometric`, `:feature:system:media-picker` |
| | `:bundles:health:pro` | Pro | Standard + `:feature:checkout:quotation`, `:feature:platform:emergency` |
| **Identity** | `:bundles:identity:basic` | Basic | `:feature:auth:authentication`, `:feature:account:profile` |
| | `:bundles:identity:standard` | Standard | Basic + `:feature:auth:biometric` |
| | `:bundles:identity:pro` | Pro | Standard + `:feature:account:onboarding`, `:feature:system:multi-language` |
| **Real Estate** | `:bundles:real-estate:basic` | Basic | `:feature:storefront:catalog`, `:feature:storefront:search` |
| | `:bundles:real-estate:standard` | Standard | Basic + `:feature:delivery:maps`, `:feature:services:scheduling` |
| | `:bundles:real-estate:pro` | Pro | Standard + `:feature:system:media-picker`, `:feature:checkout:quotation` |
| **Services** | `:bundles:services:basic` | Basic | `:feature:services:scheduling`, `:feature:storefront:stores` |
| | `:bundles:services:standard` | Standard | Basic + `:feature:checkout:quotation`, `:feature:customer:feedback` |
| | `:bundles:services:pro` | Pro | Standard + `:feature:customer:reviews-store`, `:feature:checkout:payment` |
| **Social** | `:bundles:social:basic` | Basic | `:feature:account:profile`, `:feature:communication:message` |
| | `:bundles:social:standard` | Standard | Basic + `:feature:system:media-picker`, `:feature:customer:reviews-store` |
| | `:bundles:social:pro` | Pro | Standard + `:feature:customer:feedback`, `:feature:platform:ai-assistant` |
| **Subscriptions** | `:bundles:subscriptions:basic` | Basic | `:feature:services:subscriptions`, `:feature:checkout:payment` |
| | `:bundles:subscriptions:standard` | Standard | Basic + `:feature:storefront:promotions` |
| | `:bundles:subscriptions:pro` | Pro | Standard + `:feature:system:force-update`, `:feature:platform:analytics` |

---

## ðŸ› ï¸ Tecnologias e Ferramentas

- **Linguagem**: Kotlin 2.2+ (Target JVM 17)
- **UI Framework**: Jetpack Compose com Material 3
- **InjeÃ§Ã£o de DependÃªncia**: Dagger Hilt com KSP
- **Arquitetura**: MVVM com Unidirectional Data Flow (UDF), Coroutines e Kotlin Flows
- **Build System**: Gradle 8.x com Version Catalogs (`gradle/libs.versions.toml`)
- **Qualidade & AnÃ¡lise EstÃ¡tica**: Detekt e Dokka (DocumentaÃ§Ã£o KDoc)
- **CI/CD**: GitHub Actions com execuÃ§Ã£o de testes unitÃ¡rios e publicaÃ§Ã£o automÃ¡tica de AARs

---

## ðŸ”‘ ConfiguraÃ§Ã£o Local de Credenciais (GitHub Packages)

Para que o Gradle consiga baixar e publicar dependÃªncias do GitHub Packages da organizaÃ§Ã£o, defina as credenciais de uma das seguintes formas:

### OpÃ§Ã£o 1: Via variÃ¡veis de ambiente
```bash
export GPR_USER="seu-usuario-github"
export GPR_KEY="seu-personal-access-token" # token com permissÃ£o read:packages / write:packages
```
*(No Windows PowerShell: `$env:GPR_USER="seu-usuario"` e `$env:GPR_KEY="seu-token"`)*

### OpÃ§Ã£o 2: No arquivo `local.properties` (nÃ£o commitado)
```properties
gpr.user=seu-usuario-github
gpr.key=seu-personal-access-token
```

---

## ðŸš€ Como Executar

### 1. Compilar o projeto
```bash
./gradlew build
```

### 2. Executar os Testes UnitÃ¡rios
```bash
./gradlew test
```

### 3. Executar a AplicaÃ§Ã£o Showcase (`:app`)
```bash
./gradlew :app:installDebug
```

