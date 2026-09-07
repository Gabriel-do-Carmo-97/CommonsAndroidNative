# CommonsAndroidNative

Monorepo de bibliotecas e SDKs Android reutilizáveis da organização **WGC**, para construção acelerada de aplicativos comerciais, e-commerce, delivery, serviços e soluções white-label.

Todos os módulos são publicados e distribuídos individualmente via **GitHub Packages** (`br.com.wgc:*`).

---

## 📦 Estrutura Completa de Módulos (23 Módulos)

### 🚀 Aplicação & Demonstration
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:app`](./app) | Aplicação | Showcase interativo com BottomBar e navegação para testar todos os módulos. |

---

### 🔐 Autenticação, Perfil & Segurança
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:authentication`](./authentication) | Biblioteca | Fluxos de Login, Cadastro, Recuperação de Senha integrados com `OmniBackend`. |
| [`:profile`](./profile) | Biblioteca | Gestão de Perfil, Foto, Endereço e Conformidade LGPD (Exclusão de Conta). |
| [`:settings`](./settings) | Biblioteca | Configurações do App, Troca de Tema (Claro/Escuro), Idiomas e In-App Updates. |
| [`:biometric`](./biometric) | Biblioteca | Biometria (Digital/FaceID), Teclado de PIN Numérico e Bloqueio de Tela (App Lock). |
| [`:onboarding`](./onboarding) | Biblioteca | Pagers de Boas-Vindas e Pedido Explicação de Permissões (Soft Prompts). |
| [`:force-update`](./force-update) | Biblioteca | Bloqueio de versão obsoleta (Force Update) e Tela de Manutenção Remota. |
| [`:feedback`](./feedback) | Biblioteca | Avaliação via Google In-App Review, Pesquisa NPS e Shake-to-Report. |

---

### 🛒 E-Commerce, Delivery & Alimentação
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:catalog`](./catalog) | Biblioteca | Cardápio Digital, Catálogo de Produtos e Montador de Adicionais/Opções. |
| [`:cart`](./cart) | Biblioteca | Carrinho Persistente, Cálculo de Taxas/Frete e Validação Aberto/Fechado. |
| [`:order-tracking`](./order-tracking) | Biblioteca | Acompanhamento do Pedido em Tempo Real com Status e Cronômetro. |
| [`:promotions`](./promotions) | Biblioteca | Cupons de Desconto, Carrossel de Banners e Ofertas Relâmpago. |
| [`:whatsapp-direct`](./whatsapp-direct) | Biblioteca | Disparo Direto de Pedidos Formatados e Suporte para o WhatsApp da Loja. |

---

### 📅 Serviços, Agendamento & Fidelização
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:scheduling`](./scheduling) | Biblioteca | Calendário de Agendamento de Horários, Profissionais e Reagendamento. |
| [`:quotation`](./quotation) | Biblioteca | Solicitação de Orçamentos com Anexo de Fotos e Aprovação no App. |
| [`:loyalty`](./loyalty) | Biblioteca | Cartão Carimbo Digital, Clube de Pontos e Acúmulo de Cashback. |
| [`:stores`](./stores) | Biblioteca | Multi-Lojas, Seletor de Filial mais Próxima via GPS e Rota no Mapa. |
| [`:reviews-store`](./reviews-store) | Biblioteca | Avaliação por Estrelas (1-5) e Feedback Direto do Atendimento do Comércio. |

---

### 📷 Mídia, Busca & Plataforma
| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:media-picker`](./media-picker) | Biblioteca | PhotoPicker, Câmera via CameraX, Compressão de Fotos e Leitor de QR Code/Barra MLKit. |
| [`:search`](./search) | Biblioteca | Barra de Busca Reativa com Debounce, Histórico Persistente e Filtros Dinâmicos. |
| [`:maps`](./maps) | Biblioteca | Geolocalização, Rastreamento GPS e Seletor de Endereço via `core-location`. |
| [`:message`](./message) | Biblioteca | Chat em Tempo Real, Push Notifications (FCM) e In-App Messaging. |
| [`:payment`](./payment) | Biblioteca | Checkout Seguro, Integração PIX, Cartão de Crédito e Histórico de Transações. |

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
