# CommonsAndroidNative

Monorepo de bibliotecas e SDKs Android reutilizáveis da organização **WGC**, para construção acelerada de aplicativos comerciais, e-commerce, delivery, serviços e soluções white-label.

Todos os módulos são publicados e distribuídos individualmente via **GitHub Packages** (`br.com.wgc:*`).

---

## 📦 Estrutura Completa de Módulos (33 Módulos Organizados por Domínio)

Os módulos estão agrupados na pasta `features/` por domínios de negócio coesos e balanceados, mantendo publicação direta em primeiro nível (`br.com.wgc:<module>`).

### 🚀 Aplicação & Showcase
| Módulo | Tipo | Caminho | Descrição |
| :--- | :--- | :--- | :--- |
| [`:app`](./app) | Aplicação | `app/` | Showcase interativo com BottomBar e navegação para testar todos os módulos. |

---

### 🔐 1. Auth & Segurança (`features/auth/`)
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:authentication`](./features/auth/authentication) | Biblioteca | Fluxos de Login, Cadastro e Recuperação integrados ao `OmniBackend`. |
| [`:biometric`](./features/auth/biometric) | Biblioteca | Autenticação Biométrica (Fingerprint/FaceID), App Lock e PIN de segurança. |

---

### 👤 2. Conta & Usuário (`features/account/`)
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:onboarding`](./features/account/onboarding) | Biblioteca | Pagers de boas-vindas, tour do app e solicitação contextual de permissões. |
| [`:profile`](./features/account/profile) | Biblioteca | Perfil do usuário, avatar, gestão de endereços e conformidade LGPD. |
| [`:settings`](./features/account/settings) | Biblioteca | Preferências do aplicativo, tema dinâmico e configurações de notificações. |

---

### 🏪 3. Vitrine & Descoberta (`features/storefront/`)
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:catalog`](./features/storefront/catalog) | Biblioteca | Cardápio/Catálogo digital, categorização, opções e customização de itens. |
| [`:search`](./features/storefront/search) | Biblioteca | Busca reativa com debounce, histórico de pesquisas e filtros avançados. |
| [`:promotions`](./features/storefront/promotions) | Biblioteca | Banners promocionais, carrossel de ofertas e cupons de desconto. |
| [`:stores`](./features/storefront/stores) | Biblioteca | Localização de filiais, seletor de lojas físicas e raio de proximidade via GPS. |

---

### 💳 4. Checkout & Compra (`features/checkout/`)
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:cart`](./features/checkout/cart) | Biblioteca | Carrinho persistente, cálculo de frete/taxas e validação de disponibilidade. |
| [`:payment`](./features/checkout/payment) | Biblioteca | Gateway de pagamento, PIX com QRCode copia-e-cola, cartão de crédito e histórico. |
| [`:quotation`](./features/checkout/quotation) | Biblioteca | Solicitação e negociação de orçamentos personalizados com anexo de mídias. |

---

### 🚚 5. Entrega & Logística (`features/delivery/`)
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:order-tracking`](./features/delivery/order-tracking) | Biblioteca | Rastreio em tempo real com timeline de status e tempo estimado de entrega. |
| [`:maps`](./features/delivery/maps) | Biblioteca | Exibição de mapas, rotas, geolocalização e pin de destinos. |
| [`:driver-app`](./features/delivery/driver-app) | Biblioteca | Interface e fluxo operacional para motoristas e entregadores parceiros. |
| [`:dispatch`](./features/delivery/dispatch) | Biblioteca | Gestão de expedição, despacho de encomendas e distribuição de rotas. |
| [`:geofencing`](./features/delivery/geofencing) | Biblioteca | Cercas virtuais com alertas de entrada/saída de perímetro operacional. |
| [`:offline-maps`](./features/delivery/offline-maps) | Biblioteca | Armazenamento e renderização de mapas em cache para operação offline. |

---

### 💬 6. Comunicação (`features/communication/`)
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:message`](./features/communication/message) | Biblioteca | Chat em tempo real, suporte ao cliente, FCM push e mensagens in-app. |
| [`:whatsapp-direct`](./features/communication/whatsapp-direct) | Biblioteca | Transbordo direto de pedidos e atendimento formatado via API WhatsApp. |

---

### ⭐ 7. Fidelidade & Satisfação (`features/customer/`)
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:feedback`](./features/customer/feedback) | Biblioteca | In-App Reviews do Google Play, pesquisa NPS e Shake-to-Report de bugs. |
| [`:loyalty`](./features/customer/loyalty) | Biblioteca | Cartão fidelidade digital, clube de pontos e acúmulo de cashback. |
| [`:reviews-store`](./features/customer/reviews-store) | Biblioteca | Avaliações por estrelas (1-5) e comentários diretos da loja/estabelecimento. |

---

### 📅 8. Serviços & Assinaturas (`features/services/`)
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:scheduling`](./features/services/scheduling) | Biblioteca | Agenda de horários, seleção de profissionais e reagendamento de serviços. |
| [`:subscriptions`](./features/services/subscriptions) | Biblioteca | Planos recorrentes, assinaturas periódicas e gestão de benefícios ativos. |

---

### ⚙️ 9. Sistema & Dispositivo (`features/system/`)
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:force-update`](./features/system/force-update) | Biblioteca | Bloqueio de versão obsoleta (Force Update) e tela de manutenção remota. |
| [`:media-picker`](./features/system/media-picker) | Biblioteca | PhotoPicker, integração com câmera, compressão e scanner MLKit. |
| [`:multi-language`](./features/system/multi-language) | Biblioteca | Internacionalização dinâmica e alternância de idiomas em tempo de execução. |

---

### 🧠 10. Plataforma & Infraestrutura (`features/platform/`)
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:ai-assistant`](./features/platform/ai-assistant) | Biblioteca | Assistente conversacional inteligente com suporte a recomendações. |
| [`:analytics`](./features/platform/analytics) | Biblioteca | Telemetria comportamental, rastreio de eventos e métricas de conversão. |
| [`:emergency`](./features/platform/emergency) | Biblioteca | Botão de pânico, contatos de emergência e transmissão rápida de localização. |
| [`:offline-sync`](./features/platform/offline-sync) | Biblioteca | Fila de sincronização resiliente com reconexão automática e retry. |
| [`:telemetry`](./features/platform/telemetry) | Biblioteca | Monitoramento de performance, logs de diagnósticos e integridade do app. |

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
